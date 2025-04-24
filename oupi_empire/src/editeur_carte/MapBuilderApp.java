package editeur_carte;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class MapBuilderApp extends JFrame {

    private final int DEFAULT_ROWS = 30;
    private final int DEFAULT_COLS = 40;
    private final int TILE_SIZE = 50;
    private final int CANVAS_PADDING = 20;

    private MapData mapData;
    private MapPanel mapPanel;
    private TileType selectedTileType = TileType.GRASS;
    private Obstacle.Type selectedObstacleType = null;
    private final Map<TileType, Color> tileColors = new HashMap<>();
    private final Map<Obstacle.Type, Color> obstacleColors = new HashMap<>();

    private Map<Character, BufferedImage> terrainTextures = new HashMap<>();
    private Map<Obstacle.Type, BufferedImage> obstacleTextures = new HashMap<>();

    private boolean isMousePressed = false;

    public MapBuilderApp() {
        setupColorMaps();

        // Load textures before initializing the UI
        loadTerrainTextures();
        loadObstacleTextures();

        // Initialize map data
        mapData = new MapData(DEFAULT_ROWS, DEFAULT_COLS, TILE_SIZE);

        // Setup the JFrame
        setTitle("Map Builder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Create main layout
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        // Setup menu bar
        setJMenuBar(createMenuBar());

        // Setup map panel
        mapPanel = new MapPanel();
        JScrollPane scrollPane = new JScrollPane(mapPanel);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // Setup control panel
        JPanel controlPanel = createControlPanel();
        contentPane.add(controlPanel, BorderLayout.EAST);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New Map");
        JMenuItem loadItem = new JMenuItem("Load Map");
        JMenuItem exportItem = new JMenuItem("Export Map");

        newItem.addActionListener(e -> showNewMapDialog());
        loadItem.addActionListener(e -> loadMap());
        exportItem.addActionListener(e -> exportMap());

        fileMenu.add(newItem);
        fileMenu.add(loadItem);
        fileMenu.add(exportItem);
        menuBar.add(fileMenu);

        return menuBar;
    }

    private void showNewMapDialog() {
        JTextField rowsField = new JTextField(String.valueOf(DEFAULT_ROWS), 5);
        JTextField colsField = new JTextField(String.valueOf(DEFAULT_COLS), 5);

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Rows:"));
        panel.add(rowsField);
        panel.add(new JLabel("Columns:"));
        panel.add(colsField);

        int result = JOptionPane.showConfirmDialog(this, panel, "New Map",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int rows = Integer.parseInt(rowsField.getText());
                int cols = Integer.parseInt(colsField.getText());

                mapData = new MapData(rows, cols, TILE_SIZE);
                mapPanel.updateSize();
                mapPanel.repaint();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter numbers.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setupColorMaps() {
        // Setup tile colors
        tileColors.put(TileType.GRASS, Color.GREEN);
        tileColors.put(TileType.WATER, Color.BLUE);
        tileColors.put(TileType.SAND, new Color(210, 180, 140)); // SANDYBROWN
        tileColors.put(TileType.GRAVEL, Color.GRAY);

        // Setup obstacle colors
        obstacleColors.put(Obstacle.Type.TREE, new Color(0, 100, 0)); // DARKGREEN
        obstacleColors.put(Obstacle.Type.ROCK, Color.DARK_GRAY);
        obstacleColors.put(Obstacle.Type.BUSH, new Color(34, 139, 34)); // FORESTGREEN
    }

    private void loadTerrainTextures() {
        try {
            // Load textures for different terrain types
            terrainTextures.put('H', ImageIO.read(new File("res/tuilesTexture/herbe.png")));
            terrainTextures.put('W', ImageIO.read(new File("res/tuilesTexture/eau.png")));
            terrainTextures.put('S', ImageIO.read(new File("res/tuilesTexture/sable.png")));
            terrainTextures.put('G', ImageIO.read(new File("res/tuilesTexture/gravier.png")));

            System.out.println("Textures loaded successfully");
        } catch (IOException e) {
            System.err.println("Error loading textures: " + e.getMessage());
            e.printStackTrace();

            // Fall back to default colors if textures can't be loaded
            JOptionPane.showMessageDialog(this,
                    "Couldn't load textures. Using default colors instead.",
                    "Texture Loading Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadObstacleTextures() {
        try {
            // Load textures for different obstacle types
            obstacleTextures.put(Obstacle.Type.TREE, ImageIO.read(new File("res/tuilesTexture/arbre.png")));
            obstacleTextures.put(Obstacle.Type.ROCK, ImageIO.read(new File("res/tuilesTexture/rocher.png")));
            obstacleTextures.put(Obstacle.Type.BUSH, ImageIO.read(new File("res/tuilesTexture/buisson.png")));
            
            System.out.println("Obstacle textures loaded successfully");
        } catch (IOException e) {
            System.err.println("Error loading obstacle textures: " + e.getMessage());
            e.printStackTrace();
            
            // Fall back to default colors if textures can't be loaded
            JOptionPane.showMessageDialog(this, 
                "Couldn't load obstacle textures. Using default shapes instead.", 
                "Texture Loading Error", 
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(200, getHeight()));

        // Tile type selection
        panel.add(createTilePanel());
        panel.add(Box.createVerticalStrut(10));

        // Obstacle type selection
        panel.add(createObstaclePanel());

        return panel;
    }

    private JPanel createTilePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Tile Types",
                TitledBorder.LEFT, TitledBorder.TOP));

        ButtonGroup tileGroup = new ButtonGroup();

        for (TileType type : TileType.values()) {
            JRadioButton rb = new JRadioButton(type.getDisplayName());
            rb.setActionCommand(type.name());

            if (type == selectedTileType) {
                rb.setSelected(true);
            }

            rb.addActionListener(e -> {
                selectedTileType = TileType.valueOf(e.getActionCommand());
                selectedObstacleType = null;
            });

            tileGroup.add(rb);
            panel.add(rb);
        }

        return panel;
    }

    private JPanel createObstaclePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Obstacle Types",
                TitledBorder.LEFT, TitledBorder.TOP));

        ButtonGroup obstacleGroup = new ButtonGroup();

        // Add "No Obstacle" option
        JRadioButton noObstacleRb = new JRadioButton("No Obstacle");
        noObstacleRb.setActionCommand("NONE");
        noObstacleRb.setSelected(true);
        noObstacleRb.addActionListener(e -> selectedObstacleType = null);

        obstacleGroup.add(noObstacleRb);
        panel.add(noObstacleRb);

        for (Obstacle.Type type : Obstacle.Type.values()) {
            JRadioButton rb = new JRadioButton(type.getDisplayName());
            rb.setActionCommand(type.name());

            rb.addActionListener(e -> {
                selectedObstacleType = Obstacle.Type.valueOf(e.getActionCommand());
            });

            obstacleGroup.add(rb);
            panel.add(rb);
        }

        return panel;
    }

    private void handleTilePlacement(int x, int y) {
        int col = (x - CANVAS_PADDING) / TILE_SIZE;
        int row = (y - CANVAS_PADDING) / TILE_SIZE;

        if (col >= 0 && col < mapData.getCols() && row >= 0 && row < mapData.getRows()) {
            if (selectedObstacleType != null) {
                // Add or replace obstacle
                mapData.setObstacle(col, row, selectedObstacleType);
            } else {
                // Update the tile type
                mapData.setTile(col, row, selectedTileType);
                
                // When "No Obstacle" is selected, explicitly remove any obstacle
                mapData.setObstacle(col, row, null);
            }
            mapPanel.repaint();
        }
    }
    
    private void loadMap() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Map");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Text Files", "txt"));

        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                // Read dimensions
                int rows = Integer.parseInt(reader.readLine().trim());
                int cols = Integer.parseInt(reader.readLine().trim());
                int tileSize = Integer.parseInt(reader.readLine().trim());

                // Create new map data
                mapData = new MapData(rows, cols, tileSize);

                // Read map tiles
                for (int row = 0; row < rows; row++) {
                    String line = reader.readLine();
                    if (line == null || line.equals("OBS")) break;

                    for (int col = 0; col < Math.min(cols, line.length()); col++) {
                        char tileChar = line.charAt(col);
                        TileType tileType = TileType.fromSymbol(tileChar);
                        if (tileType != null) {
                            mapData.setTile(col, row, tileType);
                        }
                    }
                }

                // Look for obstacle section
                String line;
                boolean foundObsSection = false;
                while ((line = reader.readLine()) != null) {
                    if (line.equals("OBS")) {
                        foundObsSection = true;
                        break;
                    }
                }

                // Read obstacles if section is found
                if (foundObsSection) {
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;
                        
                        String[] parts = line.split(" ");
                        if (parts.length >= 3) {
                            String obstacleType = parts[0];
                            int col = Integer.parseInt(parts[1]);
                            int row = Integer.parseInt(parts[2]);
                            
                            Obstacle.Type type = Obstacle.Type.fromExportName(obstacleType);
                            if (type != null && col >= 0 && col < cols && row >= 0 && row < rows) {
                                mapData.setObstacle(col, row, type);
                            }
                        }
                    }
                }

                // Update UI
                mapPanel.updateSize();
                mapPanel.repaint();
                
                JOptionPane.showMessageDialog(this,
                        "Map loaded successfully from " + file.getAbsolutePath(),
                        "Load Successful", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "An error occurred while loading the map: " + e.getMessage(),
                        "Load Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void exportMap() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Map");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Text Files", "txt"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            // Add .txt extension if not present
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }

            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println(mapData.getRows());
                writer.println(mapData.getCols());
                writer.println(mapData.getTileSize());

                // Write tiles
                for (int row = 0; row < mapData.getRows(); row++) {
                    for (int col = 0; col < mapData.getCols(); col++) {
                        writer.print(mapData.getTile(col, row).getSymbol());
                    }
                    writer.println();
                }

                // Write obstacles
                writer.println("OBS");

                for (Obstacle obstacle : mapData.getObstacles()) {
                    writer.println(obstacle.getType().getExportName() + " " +
                            obstacle.getCol() + " " +
                            obstacle.getRow());
                }

                JOptionPane.showMessageDialog(this,
                        "Map was successfully exported to " + file.getAbsolutePath(),
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "An error occurred while exporting the map: " + e.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Custom JPanel for map drawing
    private class MapPanel extends JPanel {

        public MapPanel() {
            setBackground(Color.WHITE);
            updateSize();

            // Add mouse listeners for drawing
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    isMousePressed = true;
                    handleTilePlacement(e.getX(), e.getY());
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    isMousePressed = false;
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (isMousePressed) {
                        handleTilePlacement(e.getX(), e.getY());
                    }
                }
            });
        }

        public void updateSize() {
            Dimension size = new Dimension(
                    mapData.getCols() * TILE_SIZE + CANVAS_PADDING * 2,
                    mapData.getRows() * TILE_SIZE + CANVAS_PADDING * 2
            );
            setPreferredSize(size);
            setMinimumSize(size);
            revalidate();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Enable anti-aliasing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw grid
            g2d.setColor(Color.LIGHT_GRAY);

            for (int row = 0; row <= mapData.getRows(); row++) {
                g2d.drawLine(
                        CANVAS_PADDING,
                        CANVAS_PADDING + row * TILE_SIZE,
                        CANVAS_PADDING + mapData.getCols() * TILE_SIZE,
                        CANVAS_PADDING + row * TILE_SIZE
                );
            }

            for (int col = 0; col <= mapData.getCols(); col++) {
                g2d.drawLine(
                        CANVAS_PADDING + col * TILE_SIZE,
                        CANVAS_PADDING,
                        CANVAS_PADDING + col * TILE_SIZE,
                        CANVAS_PADDING + mapData.getRows() * TILE_SIZE
                );
            }

            // Draw tiles
            for (int row = 0; row < mapData.getRows(); row++) {
                for (int col = 0; col < mapData.getCols(); col++) {
                    TileType tileType = mapData.getTile(col, row);
                    char terrainType = tileType.getSymbol();

                    if (terrainTextures.containsKey(terrainType)) {
                        g2d.drawImage(
                                terrainTextures.get(terrainType),
                                CANVAS_PADDING + col * TILE_SIZE,
                                CANVAS_PADDING + row * TILE_SIZE,
                                TILE_SIZE,
                                TILE_SIZE,
                                null
                        );
                    } else {
                        g2d.setColor(tileColors.get(tileType));
                        g2d.fillRect(
                                CANVAS_PADDING + col * TILE_SIZE + 1,
                                CANVAS_PADDING + row * TILE_SIZE + 1,
                                TILE_SIZE - 2,
                                TILE_SIZE - 2
                        );
                    }

                    // Draw obstacle if there is one
                    Obstacle obstacle = mapData.getObstacle(col, row);
                    if (obstacle != null) {
                        // Use texture if available, otherwise fall back to colored oval
                        if (obstacleTextures.containsKey(obstacle.getType())) {
                            // Calculate position for the obstacle
                            int obstacleX = CANVAS_PADDING + col * TILE_SIZE;
                            int obstacleY = CANVAS_PADDING + row * TILE_SIZE;
                            
                            // Draw the obstacle texture
                            g2d.drawImage(
                                obstacleTextures.get(obstacle.getType()),
                                obstacleX,
                                obstacleY,
                                TILE_SIZE,
                                TILE_SIZE,
                                null
                            );
                        } else {
                            // Fallback to colored oval if texture is missing
                            g2d.setColor(obstacleColors.get(obstacle.getType()));
                            g2d.fillOval(
                                CANVAS_PADDING + col * TILE_SIZE + TILE_SIZE / 4,
                                CANVAS_PADDING + row * TILE_SIZE + TILE_SIZE / 4,
                                TILE_SIZE / 2,
                                TILE_SIZE / 2
                            );
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MapBuilderApp app = new MapBuilderApp();
            app.setVisible(true);
        });
    }
}

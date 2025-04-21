package application;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;

public class LoadingScreen extends JFrame {

    private JLabel loadingLabel;
    private JLabel spinnerLabel;

    public LoadingScreen() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setLocationRelativeTo(null);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        DisplayMode dm = gd.getDisplayMode();
        int screenWidth = dm.getWidth();
        int screenHeight = dm.getHeight();

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(screenWidth, screenHeight));
        setContentPane(layeredPane);

        // === Background GIF ===
        ImageIcon gifIcon = new ImageIcon(getClass().getResource("/bak/mainMenue.gif"));
        Image gifImage = gifIcon.getImage().getScaledInstance(screenWidth, screenHeight, Image.SCALE_DEFAULT);
        ImageIcon scaledGif = new ImageIcon(gifImage);

        JLabel backgroundLabel = new JLabel(scaledGif);
        backgroundLabel.setBounds(0, 0, screenWidth, screenHeight);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);
        
        // === Custom Font ===
        Font customFont = null;

            try {
            	customFont = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("res/fonts/UncialAntiqua-Regular.ttf"))
            		    .deriveFont(Font.BOLD, 36f);
			} catch (FontFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            GraphicsEnvironment ge1 = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge1.registerFont(customFont);

        // === Spinner ===
        ImageIcon spinnerIcon = new ImageIcon("res/bak/Gear.gif"); // your path to the spinner GIF
        spinnerLabel = new JLabel(spinnerIcon);
        int spinnerWidth = spinnerIcon.getIconWidth();
        int spinnerHeight = spinnerIcon.getIconHeight();
        spinnerLabel.setBounds(screenWidth - spinnerWidth, screenHeight - spinnerHeight - 20, spinnerWidth, spinnerHeight);
        layeredPane.add(spinnerLabel, JLayeredPane.PALETTE_LAYER);

        // === Loading text ===
        loadingLabel = new JLabel("Chargement en cours...", SwingConstants.CENTER);
        loadingLabel.setForeground(Color.WHITE);
        loadingLabel.setFont(customFont.deriveFont(Font.PLAIN, 30f));
        loadingLabel.setBounds(screenWidth - 600, screenHeight - 100, 400, 30);
        layeredPane.add(loadingLabel, JLayeredPane.PALETTE_LAYER);

        setVisible(true);
    }

    public void startLoading(Runnable loadingLogic, Runnable whenDone) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                loadingLogic.run();  // Do whatever loading was passed in
                return null;
            }

            @Override
            protected void done() {
            	Timer timer = new Timer(1000, e -> {
                    dispose();  // Close the loading screen
                    whenDone.run();  // Proceed with the next page
                });
                timer.setRepeats(false);  // Ensures the timer only runs once
                timer.start();  // Start the timer
            }
        };
        worker.execute();
    }
}



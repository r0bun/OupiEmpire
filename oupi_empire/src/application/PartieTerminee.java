package application;

import javax.swing.*;

import styles.RoundedButton;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;

public class PartieTerminee extends JFrame {

    private JLayeredPane layeredPane;
    private JLabel backgroundLabel;
    private MainMenu pageMenu;

    public PartieTerminee() {
        setTitle("Partie Terminé");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080); // Dimension de la fenêtre
        setLocationRelativeTo(null); // Centrer la fenêtre

        initUI();
    }

    private void initUI() {
        // Crée un layered pane de la taille de l'écran
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1920, 1080));

        // Ajoute l’image de fond
        ImageIcon backgroundImage = new ImageIcon("res/bak/End_Game_Screen.png"); // 🔁 adapte le chemin
        backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setBounds(0, 0, 1920, 1080);
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

        // Ajoute un bouton au-dessus
        JButton retourButton = new RoundedButton("Retour -->", 30);
        retourButton.setBounds(1430, 900, 400, 100); // Positionne le bouton manuellement
        retourButton.setFont(customFont.deriveFont(Font.PLAIN, 45f));
        retourButton.setForeground(new Color(0, 139, 139)); 
        retourButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        retourButton.setHorizontalAlignment(SwingConstants.CENTER);
        retourButton.addActionListener(e -> {
            System.out.println("Retour cliqué !");
            pageMenu = new MainMenu();
            pageMenu.setVisible(true);
            dispose();
        });
        layeredPane.add(retourButton, JLayeredPane.PALETTE_LAYER); // couche supérieure

        // Ajoute le layered pane au JFrame
        add(layeredPane);
    }

    public static void main(String[] args) {
        // Lance l'application
        SwingUtilities.invokeLater(() -> {
            PartieTerminee frame = new PartieTerminee();
            frame.setVisible(true);
        });
    }
}



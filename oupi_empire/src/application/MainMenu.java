package application;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import styles.RoundedButton;

public class MainMenu extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    private appLaunch affichierFrame = new appLaunch();

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MainMenu frame = new MainMenu();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public MainMenu() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setLocationRelativeTo(null);

        // Obtenir la taille de l'écran
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        DisplayMode dm = gd.getDisplayMode();
        int screenWidth = dm.getWidth();
        int screenHeight = dm.getHeight();

        // Création du LayeredPane pour gérer les couches (background + contenu)
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(screenWidth, screenHeight));
        setContentPane(layeredPane);

        // Charger le GIF et le redimensionner
        ImageIcon gifIcon = new ImageIcon(getClass().getResource("/bak/mainMenue.gif"));
        Image gifImage = gifIcon.getImage().getScaledInstance(screenWidth, screenHeight, Image.SCALE_DEFAULT);
        ImageIcon scaledGif = new ImageIcon(gifImage);

        // Ajouter le GIF en arrière-plan
        JLabel backgroundLabel = new JLabel(scaledGif);
        backgroundLabel.setBounds(0, 0, screenWidth, screenHeight);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        // Panel pour ajouter des boutons et éléments interactifs
        contentPane = new JPanel();
        contentPane.setOpaque(false);
        contentPane.setBounds(0, 0, screenWidth, screenHeight);
        layeredPane.add(contentPane, JLayeredPane.PALETTE_LAYER);
        contentPane.setLayout(null);

        // Charger l'image du titre
        ImageIcon titleIcon = new ImageIcon(getClass().getResource("/bak/title.png"));
        // Récupérer l'image et la redimensionner (facteur d'échelle 1.5)
        Image titleImg = titleIcon.getImage();
        double scaleFactor = 1.5; // Facteur d'échelle - ajustez selon vos besoins
        int scaledTitleWidth = (int)(titleIcon.getIconWidth() * scaleFactor);
        int scaledTitleHeight = (int)(titleIcon.getIconHeight() * scaleFactor);
        Image scaledTitleImg = titleImg.getScaledInstance(scaledTitleWidth, scaledTitleHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledTitleIcon = new ImageIcon(scaledTitleImg);
        
        JLabel titleLabel = new JLabel(scaledTitleIcon);
        titleLabel.setBounds(screenWidth / 2 - scaledTitleWidth / 2, screenHeight / 4 - scaledTitleHeight / 2, scaledTitleWidth, scaledTitleHeight);
        contentPane.add(titleLabel);

        // Création des boutons arrondis et transparents
        JButton playButton = new RoundedButton("Jouer", 30);
        JButton settingsButton = new RoundedButton("Options", 30);
        JButton exitButton = new RoundedButton("Quitter", 30);

        // Positionner les boutons au centre de l'écran
        int buttonWidth = 200;
        int buttonHeight = 50;
        int buttonSpacing = 20;

        int centerX = screenWidth / 2 - buttonWidth / 2;
        int centerY = screenHeight / 2 - buttonHeight / 2;

        playButton.setBounds(centerX, centerY - (buttonHeight + buttonSpacing), buttonWidth, buttonHeight);
        settingsButton.setBounds(centerX, centerY, buttonWidth, buttonHeight);
        exitButton.setBounds(centerX, centerY + (buttonHeight + buttonSpacing), buttonWidth, buttonHeight);

        // Ajout d'un MouseListener à chaque bouton
        playButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Bouton 'Jouer' cliqué !");
                affichierFrame.setVisible(true);
                dispose();
            }
        });

        settingsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Bouton 'Options' cliqué !");
            }
        });

        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("EXIT");
                System.exit(0);
            }
        });

        // Ajouter les boutons au panel
        contentPane.add(playButton);
        contentPane.add(settingsButton);
        contentPane.add(exitButton);
    }
}

package ecrans_jeu;

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;
import jeu_oupi.ZoneAnimationOupi;

public class ActionPanel extends JPanel {
    private JButton btnAttaque, btnDeplacer, btnConfirmer, btnReset;
    private JButton btnTerminerTour, btnQuitterPartie, btnPerdrePartie, btnDebuterPartie;
    private JLabel backgroundLabel;
    private JLabel lblJoueurActuel;
    private ZoneAnimationOupi zoneAnimationOupi;

    public ActionPanel(ZoneAnimationOupi zoneAnimationOupi) {
        this.zoneAnimationOupi = zoneAnimationOupi;
        setLayout(null);
        setOpaque(true);
        
        // Background
        backgroundLabel = new JLabel();
        backgroundLabel.setBounds(0, 0, 1000, 175);
        backgroundLabel.setIcon(new ImageIcon(scaleImage("res/bak/tapisserie_bas_rouge.png", 1000, 175)));
        add(backgroundLabel);
        setBackground(new Color(210, 180, 140));

        // Label joueur actuel
        lblJoueurActuel = new JLabel("Joueur 1");
        lblJoueurActuel.setBounds(40, 30, 200, 25);
        lblJoueurActuel.setForeground(Color.WHITE);
        lblJoueurActuel.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblJoueurActuel);

        // Configuration des boutons
        int buttonWidth = 160;
        int buttonHeight = 40;
        int startX = 40;
        int buttonY = 65;
        int spacing = 15;

        // Boutons de droite
        int rightX = 800; // Position X pour les boutons de droite
        btnTerminerTour = createStyledButton("Terminer Tour", rightX, buttonY, buttonWidth, buttonHeight);
        btnTerminerTour.setForeground(Color.BLACK);
        btnTerminerTour.addActionListener(e -> zoneAnimationOupi.toggleJoueur());

        btnQuitterPartie = createStyledButton("Quitter la Partie", rightX, buttonY + buttonHeight + spacing, buttonWidth, buttonHeight);
        btnQuitterPartie.setForeground(Color.RED);
        btnQuitterPartie.addActionListener(e -> zoneAnimationOupi.win());

        // Boutons de gauche en ligne
        int secondRowY = buttonY + buttonHeight + spacing;
        
        btnAttaque = createStyledButton("Attaquer (F)", startX, secondRowY, buttonWidth, buttonHeight);
        btnAttaque.addActionListener(e -> simulateKeyPress(KeyEvent.VK_F));

        btnConfirmer = createStyledButton("Confirmer (C)", startX + (buttonWidth + spacing), secondRowY, buttonWidth, buttonHeight);
        btnConfirmer.addActionListener(e -> simulateKeyPress(KeyEvent.VK_C));

        btnReset = createStyledButton("Reset (R)", startX + 2 * (buttonWidth + spacing), secondRowY, buttonWidth, buttonHeight);
        btnReset.addActionListener(e -> simulateKeyPress(KeyEvent.VK_R));

        // Ajouter tous les boutons
        add(btnTerminerTour);
        add(btnQuitterPartie);
        add(btnAttaque);
        add(btnConfirmer);
        add(btnReset);
        
        setComponentZOrder(backgroundLabel, getComponentCount() - 1);
    }

    private void simulateKeyPress(int keyCode) {
        KeyEvent event = new KeyEvent(
            zoneAnimationOupi, 
            KeyEvent.KEY_PRESSED,
            System.currentTimeMillis(),
            0,
            keyCode,
            KeyEvent.CHAR_UNDEFINED
        );
        zoneAnimationOupi.dispatchEvent(event);
    }

    private JButton createStyledButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        
        ImageIcon normalIcon = new ImageIcon(scaleImage("res/bak/gold_button.png", width, height));
        ImageIcon smallerIcon = new ImageIcon(scaleImage("res/bak/gold_button.png", width-4, height-4));
        
        button.setIcon(normalIcon);
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setForeground(Color.WHITE);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setIcon(smallerIcon);
                button.setBounds(x+2, y+2, width-4, height-4);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setIcon(normalIcon);
                button.setBounds(x, y, width, height);
            }
        });
        
        return button;
    }
    
    public void setJoueurActuel(int equipeActuelle) {
    	
    	System.out.println("On set le jouerActuel du cadre en bas: Equipe = " + equipeActuelle);
        
        lblJoueurActuel.setText("Joueur " + (equipeActuelle + 1));
        
        
        if (equipeActuelle == 0) {
            backgroundLabel.setIcon(new ImageIcon(scaleBackground("res/bak/tapisserie_bas_rouge.png", 1000, 175)));
        } else if (equipeActuelle == 1) {
            backgroundLabel.setIcon(new ImageIcon(scaleBackground("res/bak/tapisserie_bas_gris.png", 1000, 175)));
        }

    }
    
    private Image scaleImage(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image image = icon.getImage();
        return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
    
 // Méthode pour charger et redimensionner une image depuis un chemin
    public Image scaleBackground(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image image = icon.getImage();
        return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
}

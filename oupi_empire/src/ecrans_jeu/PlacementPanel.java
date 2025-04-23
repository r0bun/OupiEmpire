package ecrans_jeu;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import jeu_oupi.ZoneAnimationOupi;
import jeu_oupi.GameManager;

class RoundedLabel extends JLabel {
    private static final int ARC = 15; // Rayon des coins arrondis
    
    public RoundedLabel(String text) {
        super(text);
        setHorizontalAlignment(SwingConstants.CENTER); // Centre le texte horizontalement
        setVerticalAlignment(SwingConstants.CENTER);   // Centre le texte verticalement
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dessiner le fond arrondi
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);

        // Dessiner la bordure arrondie
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);

        // Appeler le paintComponent du parent après avoir dessiné le fond
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Ne rien faire ici car la bordure est déjà dessinée dans paintComponent
    }
}

public class PlacementPanel extends JPanel {
    private JButton btnFinirPlacer;
    private JRadioButton rdbtnOupi, rdbtnLobo, rdbtnElec, rdbtnGenial;
    private ButtonGroup buttonGroupTroupe;
    private JLabel backgroundLabel;
    private JLabel lblOupi, lblElec, lblGenial, lblLobo;
    private JLabel lblJoueurActuel;
    private ZoneAnimationOupi zoneAnimationOupi;
    private ActionPanel actionPanel; 

    public PlacementPanel(ZoneAnimationOupi zoneAnimationOupi, ActionPanel actionPanel) {
        this.zoneAnimationOupi = zoneAnimationOupi;
        this.actionPanel = actionPanel;
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
        int buttonY = 85;
        int spacing = 15;
        
        // Style commun pour les labels
        Color labelBackground = new Color(40, 40, 40, 200); // Gris-noir translucide
        int labelY = buttonY + buttonHeight + 5; // 5 pixels sous les boutons
        int labelWidth = 50;
        int labelHeight = 25;

        // Première colonne de contrôles (Oupi)
        rdbtnOupi = createStyledRadioButton("Oupi", startX, buttonY, buttonWidth, buttonHeight);
        rdbtnOupi.setSelected(true);
        rdbtnOupi.addActionListener(e -> zoneAnimationOupi.changerType(0));
        lblOupi = createStyledLabel("0", startX + (buttonWidth - labelWidth) / 2, labelY, labelWidth, labelHeight);

        // Electricien
        rdbtnElec = createStyledRadioButton("Electricien", startX + (buttonWidth + spacing), buttonY, buttonWidth, buttonHeight);
        rdbtnElec.addActionListener(e -> zoneAnimationOupi.changerType(1));
        lblElec = createStyledLabel("0", startX + (buttonWidth + spacing) + (buttonWidth - labelWidth) / 2, labelY, labelWidth, labelHeight);

        // Homme génial
        rdbtnGenial = createStyledRadioButton("Homme genial", startX + 2 * (buttonWidth + spacing), buttonY, buttonWidth, buttonHeight);
        rdbtnGenial.addActionListener(e -> zoneAnimationOupi.changerType(2));
        lblGenial = createStyledLabel("0", startX + 2 * (buttonWidth + spacing) + (buttonWidth - labelWidth) / 2, labelY, labelWidth, labelHeight);

        // Lobotomisateur
        rdbtnLobo = createStyledRadioButton("Lobotomisateur", startX + 3 * (buttonWidth + spacing), buttonY, buttonWidth, buttonHeight);
        rdbtnLobo.addActionListener(e -> zoneAnimationOupi.changerType(3));
        lblLobo = createStyledLabel("0", startX + 3 * (buttonWidth + spacing) + (buttonWidth - labelWidth) / 2, labelY, labelWidth, labelHeight);

        // Bouton Finir
        btnFinirPlacer = new JButton("Terminer placement");
     // Calculer la position pour le coin en bas à droite
        int finirX = 800; // Position fixe au lieu de getWidth()
        int finirY = 120; // Position plus haute pour être au-dessus du background
        btnFinirPlacer.setBounds(finirX, finirY, buttonWidth, buttonHeight);
        btnFinirPlacer.setIcon(new ImageIcon(scaleImage("res/bak/gold_button.png", buttonWidth, buttonHeight)));
        btnFinirPlacer.setHorizontalTextPosition(JButton.CENTER);
        btnFinirPlacer.setBorderPainted(false);
        btnFinirPlacer.setContentAreaFilled(false);
        btnFinirPlacer.setFocusPainted(false);
        btnFinirPlacer.setForeground(Color.RED);
        btnFinirPlacer.addActionListener(e -> {
            zoneAnimationOupi.finirPlacer();
            
            if(zoneAnimationOupi.getJoueurActuel() == 0) {
                setVisible(false);
            }
            
            if(zoneAnimationOupi.getJoueurActuel() == 1) {
            	setJoueurActuel(zoneAnimationOupi.getJoueurActuel());
            }
        });
        
        ImageIcon normalIcon = new ImageIcon(scaleImage("res/bak/gold_button.png", buttonWidth, buttonHeight));
        ImageIcon smallerIcon = new ImageIcon(scaleImage("res/bak/gold_button.png", buttonWidth-4, buttonHeight-4));
        btnFinirPlacer.setIcon(normalIcon);

     // Modifier aussi les coordonnées dans le MouseListener
        btnFinirPlacer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnFinirPlacer.setIcon(smallerIcon);
                btnFinirPlacer.setBounds(finirX + 2, finirY + 2, 
                                        buttonWidth-4, buttonHeight-4);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnFinirPlacer.setIcon(normalIcon);
                btnFinirPlacer.setBounds(finirX, finirY, 
                                        buttonWidth, buttonHeight);
            }
        });

        // Groupe de boutons radio
        buttonGroupTroupe = new ButtonGroup();
        buttonGroupTroupe.add(rdbtnOupi);
        buttonGroupTroupe.add(rdbtnElec);
        buttonGroupTroupe.add(rdbtnGenial);
        buttonGroupTroupe.add(rdbtnLobo);

        // Ajouter tous les composants
        add(rdbtnOupi);
        add(lblOupi);
        add(rdbtnElec);
        add(lblElec);
        add(rdbtnGenial);
        add(lblGenial);
        add(rdbtnLobo);
        add(lblLobo);
        add(btnFinirPlacer);
        
        setComponentZOrder(backgroundLabel, getComponentCount() - 1);
    }

    private JRadioButton createStyledRadioButton(String text, int x, int y, int width, int height) {
        JRadioButton button = new JRadioButton(text);
        button.setBounds(x, y, width, height);
        button.setBorderPainted(true);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        
        // Images normale et réduite pour l'effet hover
        ImageIcon normalIcon = new ImageIcon(scaleImage("res/bak/gold_button.png", width, height));
        ImageIcon smallerIcon = new ImageIcon(scaleImage("res/bak/gold_button.png", width-4, height-4));
        
        button.setIcon(normalIcon);
        button.setSelectedIcon(normalIcon);
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setForeground(Color.WHITE);
        
        // Style de bordure pour la sélection
        button.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        button.addChangeListener(e -> {
            if (button.isSelected()) {
                button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            } else {
                button.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            }
        });
        
        // Ajout de l'effet hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setIcon(smallerIcon);
                button.setSelectedIcon(smallerIcon);
                button.setBounds(x+2, y+2, width-4, height-4);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setIcon(normalIcon);
                button.setSelectedIcon(normalIcon);
                button.setBounds(x, y, width, height);
            }
        });
        
        return button;
    }

    public void updateTroupesLabels(int[] troupesDispo) {
        lblOupi.setText(String.valueOf(troupesDispo[0]));
        lblElec.setText(String.valueOf(troupesDispo[1]));
        lblGenial.setText(String.valueOf(troupesDispo[2]));
        lblLobo.setText(String.valueOf(troupesDispo[3]));
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
    
 // Ajoutez cette nouvelle méthode à la classe
    private JLabel createStyledLabel(String text, int x, int y, int width, int height) {
        RoundedLabel label = new RoundedLabel(text);
        label.setBounds(x, y, width, height);
        label.setForeground(Color.WHITE);
        label.setOpaque(false); // Important : mettre à false pour les coins arrondis
        label.setBackground(new Color(40, 40, 40, 200));
        
        // Police plus grande et en gras
        label.setFont(new Font("Arial", Font.BOLD, 16));
        
        return label;
    }
    
    // Méthode pour charger et redimensionner une image depuis un chemin
    public Image scaleImage(String path, int width, int height) {
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

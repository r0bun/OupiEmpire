package ecrans_jeu;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import jeu_oupi.ZoneAnimationOupi;
import jeu_oupi.GameManager;

public class PlacementPanel extends JPanel {
    private JButton btnFinirPlacer;
    private JRadioButton rdbtnOupi, rdbtnLobo, rdbtnElec, rdbtnGenial;
    private ButtonGroup buttonGroupTroupe;
    private JLabel backgroundLabel;
    private JLabel lblOupi, lblElec, lblGenial, lblLobo;
    private ZoneAnimationOupi zoneAnimationOupi;

    public PlacementPanel(ZoneAnimationOupi zoneAnimationOupi) {
        this.zoneAnimationOupi = zoneAnimationOupi;
        setLayout(null);
        setOpaque(true);
        
        // Ajouter un JLabel pour l'image de fond
        backgroundLabel = new JLabel();
        backgroundLabel.setBounds(0, 0, 1000, 175); // Taille du panneau Stats
        backgroundLabel.setIcon(new ImageIcon(scaleImage("res/bak/tapisserie_bas_rouge.png", 1000, 175)));
        add(backgroundLabel);
        setBackground(new Color(210, 180, 140));

        btnFinirPlacer = new JButton("Finir placer");
        btnFinirPlacer.setBounds(0, 0, 111, 25);
        btnFinirPlacer.addActionListener(e -> {
        	zoneAnimationOupi.finirPlacer();
        	
        	if(zoneAnimationOupi.getJoueurActuel() == 0) { 
        		 setVisible(false);
        	}

        });
        add(btnFinirPlacer);

        buttonGroupTroupe = new ButtonGroup();

        rdbtnOupi = new JRadioButton("Oupi");
        rdbtnOupi.setBounds(220, 0, 123, 25);
        rdbtnOupi.setSelected(true);
        rdbtnOupi.addActionListener(e -> zoneAnimationOupi.changerType(0));
        buttonGroupTroupe.add(rdbtnOupi);
        add(rdbtnOupi);

        rdbtnElec = new JRadioButton("Electricien");
        rdbtnElec.setBounds(220, 48, 123, 25);
        rdbtnElec.addActionListener(e -> zoneAnimationOupi.changerType(1));
        buttonGroupTroupe.add(rdbtnElec);
        add(rdbtnElec);

        rdbtnGenial = new JRadioButton("Homme genial");
        rdbtnGenial.setBounds(440, 0, 123, 25);
        rdbtnGenial.addActionListener(e -> zoneAnimationOupi.changerType(2));
        buttonGroupTroupe.add(rdbtnGenial);
        add(rdbtnGenial);

        rdbtnLobo = new JRadioButton("Lobotomisateur");
        rdbtnLobo.setBounds(440, 48, 123, 25);
        rdbtnLobo.addActionListener(e -> zoneAnimationOupi.changerType(3));
        buttonGroupTroupe.add(rdbtnLobo);
        add(rdbtnLobo);

        lblOupi = new JLabel("0");
        lblOupi.setBounds(156, 0, 56, 16);
        add(lblOupi);

        lblElec = new JLabel("0");
        lblElec.setBounds(156, 48, 56, 16);
        add(lblElec);

        lblGenial = new JLabel("0");
        lblGenial.setBounds(376, 0, 56, 16);
        add(lblGenial);

        lblLobo = new JLabel("0");
        lblLobo.setBounds(376, 48, 56, 16);
        add(lblLobo);
        
        setComponentZOrder(backgroundLabel, getComponentCount() - 1);
    }

    public void updateTroupesLabels(int[] troupesDispo) {
        lblOupi.setText(String.valueOf(troupesDispo[0]));
        lblElec.setText(String.valueOf(troupesDispo[1]));
        lblGenial.setText(String.valueOf(troupesDispo[2]));
        lblLobo.setText(String.valueOf(troupesDispo[3]));
    }
    
    // Méthode pour charger et redimensionner une image depuis un chemin
    public Image scaleImage(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image image = icon.getImage();
        return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
}

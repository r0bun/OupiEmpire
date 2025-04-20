package ecrans_jeu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import troupe.Troupe;

/**
 * 
 * 
 * @author Loic Simard
 * @author Sacha Burelle
 *
 */
public class Stats extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private int screenWidth, screenHeight;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private Troupe selectedTroupe;
    private JLabel backgroundLabel;
    private JLabel lblHp, lblAtk, lblDef, lblSpd;
    private JButton btnLvlHp, btnLvlAtk, btnLvlDef, btnLvlSpd, btnLvlEnd;
    private JLabel lblNom;

    private int nbrLvl = 0;
    
    
    public Stats(int screenWidth, int screenHeight) {
        setLayout(null);
        
        // Default background
        setBackground(new Color(210, 180, 140)); // Light brown
        
        // Ajouter un JLabel pour l'image de fond
        backgroundLabel = new JLabel();
        backgroundLabel.setBounds(0, 0, 380, 450); // Taille du panneau Stats
        add(backgroundLabel);
        
        
     // Stats labels
        lblHp = createStatLabel(40, 160);
        lblAtk = createStatLabel(40, 235);
        lblDef = createStatLabel(40, 295);
        lblSpd = createStatLabel(40, 360);

        add(lblHp);
        add(lblAtk);
        add(lblDef);
        add(lblSpd);
        
        // Nom Label
        lblNom = new JLabel("");
        lblNom.setFont(new Font("Arial", Font.BOLD, 20));
        lblNom.setForeground(Color.WHITE);
        lblNom.setHorizontalAlignment(SwingConstants.RIGHT);


        // Taille du panneau = 380px, on place à droite
        lblNom.setBounds(180, 30, 180, 30); // x + width = 370, donc à droite
        add(lblNom);
        
        setComponentZOrder(backgroundLabel, getComponentCount() - 1);
        
    }
    
    private JLabel createStatLabel(int x, int y) {
        JLabel label = new JLabel("");
        label.setBounds(x, y, 50, 30);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    /**
     * Updates the stats display with information from the selected troop.
     * 
     * @param troupe The troop to display stats for, null to clear display.
     */
    public void updateTroupe(Troupe troupe) {
        this.selectedTroupe = troupe;

        if (troupe != null) {
            // Update background image
            String playerCardPath = troupe.getPlayerCard();
            if (playerCardPath != null && !playerCardPath.isEmpty()) {
                backgroundLabel.setIcon(new ImageIcon(scaleImage(playerCardPath, 380, 450)));
            } else {
                backgroundLabel.setIcon(null);
            }

            // Update stats
            lblHp.setText("" + troupe.getHP());
            lblAtk.setText("" + troupe.getAttaque());
            lblDef.setText("" + troupe.getDefense());
            lblSpd.setText("" + troupe.getVitesse());
            lblNom.setText("" + troupe.getNom());
        } else {
            // Reset to default state
            backgroundLabel.setIcon(null);
            lblHp.setText("");
            lblAtk.setText("");
            lblDef.setText("");
            lblSpd.setText("");
            lblNom.setText("");
        }

        repaint();
    }
    
    
 // Méthode pour charger et redimensionner une image depuis un chemin
    public Image scaleImage(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image image = icon.getImage();
        return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
    
  
    
    public void levelUp(Troupe troupe, int nombre) {
    	btnLvlHp.setVisible(true);
    	btnLvlAtk.setVisible(true);
    	btnLvlDef.setVisible(true);
    	btnLvlSpd.setVisible(true);
    	btnLvlEnd.setVisible(true);
    	updateTroupe(troupe);
    }
    
    public void statIncrease(int id) {
    	nbrLvl--;
    	
    	if (nbrLvl <= 0) {
    		btnLvlHp.setVisible(false);
        	btnLvlAtk.setVisible(false);
        	btnLvlDef.setVisible(false);
        	btnLvlSpd.setVisible(false);
        	btnLvlEnd.setVisible(false);
    	}
    	
    	pcs.firePropertyChange("stat", selectedTroupe, id);
    	updateTroupe(selectedTroupe);
    }
}

package ecrans_jeu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JLabel;
import javax.swing.JPanel;

import troupe.Troupe;

/**
 * 
 * 
 * @author Loic Simard
 *
 */
public class Stats extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private int screenWidth, screenHeight;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private Troupe selectedTroupe;
    private JLabel imageLabel;
    
    
    public Stats(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        setLayout(null);
        
        setBackground(new Color(220, 220, 220, 200)); 
        
        imageLabel = new JLabel();
        imageLabel.setBounds(250, 150, 100, 100);
        add(imageLabel);
        
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    /**
     * Updates the stats display with information from the selected troop
     * 
     * @param troupe The troupe to display stats for, null to clear display
     */
    public void updateTroupe(Troupe troupe) {
        this.selectedTroupe = troupe;
        
        if (troupe != null) {
            if (troupe.getImage() != null) {
                BufferedImage scaledImage = scaleImage(troupe.getImage(), 100, 100);
                imageLabel.setIcon(new javax.swing.ImageIcon(scaledImage));
            } else {
                imageLabel.setIcon(null);
            }
            
            
        } else {
            imageLabel.setIcon(null);
        }
        
        repaint();
    }
    
    /**
     * Scales a BufferedImage to the specified width and height
     */
    private BufferedImage scaleImage(BufferedImage original, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaledImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, width, height, null);
        g.dispose();
        return scaledImage;
    }
    
    /**
     * Dessine le panneau.
     * 
     * @param g l'objet {@link Graphics} utilisé pour dessiner
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (selectedTroupe != null) {
            // Draw troop statistics
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Franklin Gothic Book", Font.PLAIN, 50));
            g2d.drawString(selectedTroupe.getClass().getSimpleName(), 10, (int)screenHeight/20);
            g2d.setFont(new Font("Franklin Gothic Book", Font.PLAIN, 20));
            g2d.drawString("HP: " + selectedTroupe.getHP(), 10, (int)2*screenHeight/20);
            g2d.drawString("Attaque: " + selectedTroupe.getAttaque(), 10, (int) (3*screenHeight/20));
            g2d.drawString("Defense: " + selectedTroupe.getDefense(), 10, (int) (4*screenHeight/20));
            g2d.drawString("Vitesse: " + selectedTroupe.getVitesse(), 10, (int) (5*screenHeight/20));
            g2d.drawString("Endurance: " + selectedTroupe.getEndurance(), 10, (int) (6*screenHeight/20));
        }
    }
}

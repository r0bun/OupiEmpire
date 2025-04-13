package ecrans_jeu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JButton;
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
    private JButton btnLvlHp, btnLvlAtk, btnLvlDef, btnLvlSpd, btnLvlEnd;
    private int nbrLvl = 0;
    
    
    public Stats(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        setLayout(null);
        
        setBackground(new Color(220, 220, 220, 200)); 
        
        imageLabel = new JLabel();
        imageLabel.setBounds(250, 150, 100, 100);
        add(imageLabel);
        
        btnLvlHp = new JButton("+");
		btnLvlHp.setBounds(150, (int)2*screenHeight/20-18, 20, 20);
		btnLvlHp.setMargin(new Insets(0,0,0,0));
		btnLvlHp.setFont(new Font("Arial", Font.PLAIN, 10));
		btnLvlHp.setVisible(false);
		btnLvlHp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				statIncrease(0);
			}
		});
		add(btnLvlHp);
		
		btnLvlAtk = new JButton("+");
		btnLvlAtk.setBounds(150, (int)3*screenHeight/20-18, 20, 20);
		btnLvlAtk.setMargin(new Insets(0,0,0,0));
		btnLvlAtk.setFont(new Font("Arial", Font.PLAIN, 10));
		btnLvlAtk.setVisible(false);
		btnLvlAtk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				statIncrease(1);
			}
		});
		add(btnLvlAtk);
		
		btnLvlDef = new JButton("+");
		btnLvlDef.setBounds(150, (int)4*screenHeight/20-18, 20, 20);
		btnLvlDef.setMargin(new Insets(0,0,0,0));
		btnLvlDef.setFont(new Font("Arial", Font.PLAIN, 10));
		btnLvlDef.setVisible(false);
		btnLvlDef.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				statIncrease(2);
			}
		});
		add(btnLvlDef);
		
		btnLvlSpd = new JButton("+");
		btnLvlSpd.setBounds(150, (int)5*screenHeight/20-18, 20, 20);
		btnLvlSpd.setMargin(new Insets(0,0,0,0));
		btnLvlSpd.setFont(new Font("Arial", Font.PLAIN, 10));
		btnLvlSpd.setVisible(false);
		btnLvlSpd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				statIncrease(3);
			}
		});
		add(btnLvlSpd);
		
		btnLvlEnd = new JButton("+");
		btnLvlEnd.setBounds(150, (int)6*screenHeight/20-18, 20, 20);
		btnLvlEnd.setMargin(new Insets(0,0,0,0));
		btnLvlEnd.setFont(new Font("Arial", Font.PLAIN, 10));
		btnLvlEnd.setVisible(false);
		btnLvlEnd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				statIncrease(4);
			}
		});
		add(btnLvlEnd);
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

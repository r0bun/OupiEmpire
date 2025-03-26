package ecrans_jeu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JPanel;

import troupe.Troupe;

public class Stats extends JPanel{
	
	private int screenWidth, screenHeight;
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private Troupe troupe = new Troupe(0,0);

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	
	public Stats(int screenWidth, int screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}

	/**
	 * Dessine le panneau.
	 * 
	 * @param g l'objet {@link Graphics} utilisé pour dessiner
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setColor(Color.BLACK);
		g2d.setFont(new Font("Franklin Gothic Book", Font.PLAIN, 50));
		g2d.drawString(troupe.getClass().toString(), 100, (int)screenHeight/20);
		g2d.setFont(new Font("Franklin Gothic Book", Font.PLAIN, 20));
		g2d.drawString("HP: "+troupe.getHP(), 100, (int)2*screenHeight/20);
		g2d.drawString("Attaque: "+troupe.getAttaque(), 100, (int) (3*screenHeight/20));
		g2d.drawString("Defense: "+troupe.getDefense(), 100, (int) (4*screenHeight/20));
		g2d.drawString("Vitesse: "+troupe.getVitesse(), 100, (int) (5*screenHeight/20));
		g2d.drawString("Endurance: "+troupe.getEndurance(), 100, (int) (6*screenHeight/20));
	}
	
	public void updateTroupe(Troupe newTroupe) {
		troupe = newTroupe;
		repaint();
	}
}

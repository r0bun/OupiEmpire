package ecrans_jeu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JPanel;

/**
 * TODO
 * 
 * @author Loic Simard
 */
public class Fin extends JPanel{
	
	private int screenWidth, screenHeight;
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private boolean win = true;

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	
	public Fin(int screenWidth, int screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				
			}
		});
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
		g2d.drawString("Cliquez pour commencer la partie", 100, (int) screenHeight/3);
	}
	
	
}

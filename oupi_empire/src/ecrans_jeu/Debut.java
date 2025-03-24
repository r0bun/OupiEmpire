package ecrans_jeu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
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
public class Debut extends JPanel {

	private int screenWidth, screenHeight;
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	
	/* Voir les fonts disponibles
	public static void main(String[] args)
    {
        System.out.println("To Know the available font family names");
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        System.out.println("Getting the font family names");

        // Array of all the fonts available in AWT
        String fonts[] = ge.getAvailableFontFamilyNames();

        // Getting the font family names
        for (String i : fonts) {
            System.out.println(i + " ");
        }
    }*/

	public Debut(int screenWidth, int screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				pcs.firePropertyChange("Start", 0, -1);
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

package jeu_oupi;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JPanel;

public class ZoneAnimationOupi extends JPanel implements Runnable{

	private static final long serialVersionUID = 1L;
	
	private JeuxOupi jeuxOupi;
	

	//ajouter le support pour lancer des evenements de type PropertyChange
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

		/**
		 * voici la methode qui permettra de s'ajouter en tant qu'ecouteur
		 */
	public  void addPropertyChangeListener(PropertyChangeListener listener) {
			this.pcs.addPropertyChangeListener(listener);
	}

		 public ZoneAnimationOupi(int screenWidth, int screenHeight) {
		        jeuxOupi = new JeuxOupi(screenWidth, screenHeight);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Dessine le panneau.
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		
		jeuxOupi.dessiner(g2d);
		
	}


	

}

package jeu_oupi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JPanel;

import plateau.Tuile;
import troupe.Troupe;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ZoneAnimationOupi extends JPanel implements Runnable{

	private static final long serialVersionUID = 1L;
	
	private JeuxOupi jeuxOupi;
	
	final int FPS = 27;

	Thread threadJeu;
	
	//ajouter le support pour lancer des evenements de type PropertyChange
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

		/**
		 * voici la methode qui permettra de s'ajouter en tant qu'ecouteur
		 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
			this.pcs.addPropertyChangeListener(listener);
	}

	public ZoneAnimationOupi(int screenWidth, int screenHeight) {
			 
			jeuxOupi = new JeuxOupi(screenWidth, screenHeight);
			 
		 	addMouseListener(new MouseAdapter() {
		 		@Override
		 		public void mousePressed(MouseEvent e) {
		 			int x = e.getX();
	                int y = e.getY();

	                // Essayer de sélectionner une troupe d'abord
	                Troupe cliquee = jeuxOupi.getTroupeA(x, y);
	                if (cliquee != null) {
	                    jeuxOupi.selectionnerTroupe(cliquee);
	                    System.out.println("Troupe sélectionnée à : (" + cliquee.getCol() + "," + cliquee.getLig() + ")");
	                    return;
	                }

	                // Si aucune troupe n'a été cliquée, gérer le clic sur une tuile
	                int ligne = y / JeuxOupi.tailleTuile;
	                int colonne = x / JeuxOupi.tailleTuile;

	                // Vérifier si le clic est dans les limites du plateau
	                if (ligne >= 0 && ligne < JeuxOupi.getNbTuiles() && colonne >= 0 && colonne < JeuxOupi.getNbTuiles()) {
	                    Tuile tuileCliquee = jeuxOupi.getPlateau().getTuile(ligne, colonne);
	                    System.out.println("Tuile cliquée : Ligne " + (ligne+1) + ", Colonne " + (colonne+1));

	                    tuileCliquee.setCouleur(Color.blue);
	                }
		 		}
		 		});
		 	
		 	// Ajouter un écouteur de clavier pour le déplacement avec WASD
		 	setFocusable(true);
		 	addKeyListener(new KeyAdapter() {
		 		@Override
		 		public void keyPressed(KeyEvent e) {
		 			switch (e.getKeyCode()) {
		 				case KeyEvent.VK_W: // W 
		 					jeuxOupi.deplacerTroupeSelectionneeHaut();
		 					break;
		 				case KeyEvent.VK_A: // A 
		 					jeuxOupi.deplacerTroupeSelectionneeGauche();
		 					break;
		 				case KeyEvent.VK_S: // S 
		 					jeuxOupi.deplacerTroupeSelectionneeBas();
		 					break;
		 				case KeyEvent.VK_D: // D
		 					jeuxOupi.deplacerTroupeSelectionneeDroite();
		 					break;
		 			}
		 			repaint();
		 		}
		 	});
		        
	}

	@Override
	public void run() {
		
		double intervaleDessin = 1000000000/FPS;
		double delta = 0;
		long  tempsAv = System.nanoTime();
		long tempsAct;
		
		while(threadJeu != null) {
			
			tempsAct = System.nanoTime();
			
			delta += (tempsAct - tempsAv)/intervaleDessin;
			tempsAv = tempsAct;
			
			if(delta>=1) {
				miseAJour();
				repaint();
				delta--;
			}
		}
		
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
	
	public void miseAJour() {
		
	}
	
	public void demarer() {
		threadJeu = new Thread(this);
		threadJeu.start();
		requestFocus(); // Demander le focus pour garantir que les entrées du clavier fonctionnent
	}
}

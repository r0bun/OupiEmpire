package jeu_oupi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import javax.swing.JPanel;

import plateau.Tuile;
import troupe.Troupe;

/**
 * La classe {@code ZoneAnimationOupi} représente la zone d'animation pour le
 * jeu Oupi. Elle étend {@link JPanel} et implémente {@link Runnable} pour gérer
 * l'animation du jeu.
 * 
 * @author Badr Rifki
 * @author Loic Simard
 */
public class ZoneAnimationOupi extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;

	private JeuxOupi jeuxOupi;

	final int FPS = 27;

	Thread threadJeu;

	private boolean enCours = false;

	private int joueurActuel = 0;

	// Zoom properties
	private double zoomFactor = 1.0;
	private final double ZOOM_STEP = 0.1;
	private final double MAX_ZOOM = 3.0;
	private final double MIN_ZOOM = 0.5;

	// Panning properties
	private int translateX = 0;
	private int translateY = 0;
	private int dragStartX;
	private int dragStartY;
	private boolean isDragging = false;

	// Attack mode state
	private boolean modeAttaque = false;

	// Ajouter le support pour lancer des événements de type PropertyChange
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/**
	 * Constructeur de la classe {@code ZoneAnimationOupi}.
	 * 
	 * @param screenWidth  la largeur de l'écran
	 * @param screenHeight la hauteur de l'écran
	 */
	public ZoneAnimationOupi(int screenWidth, int screenHeight) {
		jeuxOupi = new JeuxOupi(screenWidth, screenHeight);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (enCours) {
					dragStartX = e.getX();
					dragStartY = e.getY();

					// Applique le zoom inverse pour obtenir les coordonnees du jeu
					int gameX = (int) ((e.getX() - translateX) / zoomFactor);
					int gameY = (int) ((e.getY() - translateY) / zoomFactor);

					// En mode attaque, essayer de sélectionner une troupe à attaquer
					if (modeAttaque && jeuxOupi.getTroupeSelectionnee() != null) {
						Troupe troupeCible = jeuxOupi.getTroupeA(gameX, gameY);
						if (troupeCible != null) {
							System.out.println("⚔️ Tentative d'attaque sur " + troupeCible.getClass().getSimpleName());
							// Attaquer la troupe ciblée
							boolean attaqueReussie = jeuxOupi.attaquerTroupe(troupeCible);
							if (attaqueReussie) {
								System.out.println("✅ Attaque réussie!");
								jeuxOupi.getTroupeSelectionnee().setEpuisee(true);
								jeuxOupi.deselectionnerTroupeAct();
								checkFinTour();
							}
							// Désactiver le mode attaque après une tentative
							modeAttaque = false;
							return;
						} else {
							System.out.println("❌ Pas de troupe à attaquer ici! Cliquez sur une troupe ennemie.");
							return;
						}
					}

					// Essayer de sélectionner une troupe d'abord
					Troupe cliquee = jeuxOupi.getTroupeA(gameX, gameY);
					if (cliquee != null && jeuxOupi.getTroupeSelectionnee() == null) {
						jeuxOupi.selectionnerTroupe(cliquee);
						System.out
								.println("Troupe sélectionnée à : (" + cliquee.getCol() + "," + cliquee.getLig() + ")");
						pcs.firePropertyChange("troupe", "", jeuxOupi.getTroupeSelectionnee());
						return;
					}

					// Si aucune troupe n'a été cliquée, gérer le clic sur une tuile
					int ligne = gameY / jeuxOupi.getTailleTuile();
					int colonne = gameX / jeuxOupi.getTailleTuile();

					// Vérifier si le clic est dans les limites du plateau
					if (ligne >= 0 && ligne < jeuxOupi.getTailleTuile() && colonne >= 0
							&& colonne < jeuxOupi.getTailleTuile()) {
						Tuile tuileCliquee = jeuxOupi.getPlateau().getTuile(ligne, colonne);
						System.out.println("Tuile cliquée : Ligne " + (ligne + 1) + ", Colonne " + (colonne + 1));
					}

					isDragging = true;
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				isDragging = false;
			}

			@Override
			public void mouseClicked(MouseEvent e) {

				int gameX = (int) ((e.getX() - translateX) / zoomFactor);
				int gameY = (int) ((e.getY() - translateY) / zoomFactor);
				Troupe cliquee = jeuxOupi.getTroupeA(gameX, gameY);

				if (cliquee == null && jeuxOupi.getTroupeSelectionnee() != null) {
					pcs.firePropertyChange("troupe", "", null);
					jeuxOupi.deselectionnerTroupe(cliquee);
				} else if(!cliquee.equals(jeuxOupi.getTroupeSelectionnee())) {
					pcs.firePropertyChange("troupe", "", cliquee);
					jeuxOupi.selectionnerTroupe(cliquee);
				}
			}
		});

		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (isDragging) {
					translateX += e.getX() - dragStartX;
					translateY += e.getY() - dragStartY;
					dragStartX = e.getX();
					dragStartY = e.getY();
					repaint();
				}
			}
		});

		// Add mouse wheel listener for zoom
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				// Get the mouse position for zoom centering
				int mouseX = e.getX();
				int mouseY = e.getY();

				// Calculate game position before zoom
				double oldGameX = (mouseX - translateX) / zoomFactor;
				double oldGameY = (mouseY - translateY) / zoomFactor;

				// Adjust zoom based on wheel rotation
				if (e.getPreciseWheelRotation() < 0) {
					// Zoom in
					zoomFactor = Math.min(MAX_ZOOM, zoomFactor + ZOOM_STEP);
				} else {
					// Zoom out
					zoomFactor = Math.max(MIN_ZOOM, zoomFactor - ZOOM_STEP);
				}

				// Calculate new screen position to keep the same game position under cursor
				double newGameX = (mouseX - translateX) / zoomFactor;
				double newGameY = (mouseY - translateY) / zoomFactor;

				// Adjust translation to keep mouse position over same game coordinates
				translateX -= (oldGameX - newGameX) * zoomFactor;
				translateY -= (oldGameY - newGameY) * zoomFactor;

				repaint();
			}
		});

		// Ajouter un écouteur de clavier pour le déplacement avec WASD
		setFocusable(true);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				Troupe troupe = jeuxOupi.getTroupeSelectionnee();
				
				// Si la touche Escape est pressée et qu'une troupe est sélectionnée
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					if (troupe != null) {
						System.out.println("📋 Désélection de la troupe avec touche Echap");
						pcs.firePropertyChange("troupe", "", null);
						jeuxOupi.deselectionnerTroupe(troupe);
						modeAttaque = false;  // Désactiver le mode attaque si actif
					}
					return;
				}
				
				if (troupe != null) {
					if (troupe.getEquipe() == joueurActuel) {
						switch (e.getKeyCode()) {
						case KeyEvent.VK_W:
						case KeyEvent.VK_UP:
							jeuxOupi.deplacerTroupeSelectionneeHaut();
							break;
						case KeyEvent.VK_A:
						case KeyEvent.VK_LEFT:
							jeuxOupi.deplacerTroupeSelectionneeGauche();
							break;
						case KeyEvent.VK_S:
						case KeyEvent.VK_DOWN:
							jeuxOupi.deplacerTroupeSelectionneeBas();
							break;
						case KeyEvent.VK_D:
						case KeyEvent.VK_RIGHT:
							jeuxOupi.deplacerTroupeSelectionneeDroite();
							break;
						case KeyEvent.VK_R: // R
							jeuxOupi.resetTroupeAct();
							break;
						case KeyEvent.VK_C:
							jeuxOupi.confirm();
							pcs.firePropertyChange("troupe", "", null);
							checkFinTour();
							break;
						case KeyEvent.VK_F: // F pour "Fire" ou attaquer
						case KeyEvent.VK_X: // X pour attaquer (comme dans Fire Emblem)
							activerModeAttaque();
							break;
						}
					} else {
						System.out.println("⚠️ Cette troupe appartient à l'autre équipe.");
						System.out.println("ℹ️ Appuyez sur la touche ECHAP pour la désélectionner.");
					}
				} else if (e.getKeyCode() == KeyEvent.VK_F || e.getKeyCode() == KeyEvent.VK_X) {
					System.out.println("⚠️ Sélectionnez d'abord une troupe pour attaquer.");
				}
				repaint();
			}

		});
	}

	/**
	 * Active le mode attaque, permettant de sélectionner une troupe à attaquer.
	 */
	private void activerModeAttaque() {
		if (jeuxOupi.getTroupeSelectionnee() != null) {
			Troupe troupe = jeuxOupi.getTroupeSelectionnee();
			if (troupe.getEquipe() != joueurActuel) {
				System.out.println("⚠️ Impossible d'attaquer avec une troupe ennemie.");
				System.out.println("ℹ️ Appuyez sur la touche ECHAP pour désélectionner cette troupe.");
				return;
			}
			
			modeAttaque = !modeAttaque; // Toggle attack mode
			if (modeAttaque) {
				System.out.println("🔴 MODE ATTAQUE ACTIVÉ! Cliquez sur une troupe ennemie à attaquer.");
				System.out.println("   - L'ennemi doit être adjacent (distance 1)");
				System.out.println("   - Appuyez sur F ou X à nouveau pour annuler");
				System.out.println("   - Appuyez sur ECHAP pour désélectionner la troupe");
			} else {
				System.out.println("🟢 Mode attaque désactivé.");
			}
		} else {
			System.out.println("⚠️ Veuillez d'abord sélectionner une troupe pour attaquer.");
		}
	}

	/**
	 * Méthode pour ajouter un écouteur de changement de propriété.
	 * 
	 * @param listener l'écouteur de changement de propriété
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Méthode exécutée par le thread de jeu pour gérer l'animation.
	 */
	@Override
	public void run() {
		double intervaleDessin = 1000000000 / FPS;
		double delta = 0;
		long tempsAv = System.nanoTime();
		long tempsAct;

		while (!threadJeu.isInterrupted()) {
			tempsAct = System.nanoTime();

			delta += (tempsAct - tempsAv) / intervaleDessin;
			tempsAv = tempsAct;

			if (delta >= 1) {
				miseAJour();
				repaint();
				delta--;
			}
		}
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

		// Save the original transform
		AffineTransform oldTransform = g2d.getTransform();

		// Apply zoom and translation
		g2d.translate(translateX, translateY);
		g2d.scale(zoomFactor, zoomFactor);

		jeuxOupi.dessiner(g2d);

		// Restore the original transform
		g2d.setTransform(oldTransform);

		// Draw zoom level indicator
		g2d.setColor(Color.WHITE);
		g2d.fillRect(10, 10, 120, 30);
		g2d.setColor(Color.BLACK);
		g2d.drawString("Zoom: " + String.format("%.1f", zoomFactor) + "x", 20, 30);
	}

	/**
	 * Met à jour l'état du jeu.
	 */
	public void miseAJour() {
		// Implémenter la logique de mise à jour du jeu ici
	}

	/**
	 * Démarre le thread de jeu.
	 */
	public void demarrer() {
		System.out.println("start");
		threadJeu = new Thread(this);
		threadJeu.start();
		enCours = true;
		requestFocus(); // Demander le focus pour garantir que les entrées du clavier fonctionnent
	}

	/**
	 * Gagne la partie
	 * 
	 * @return une string contenant le resultat victoire si le thread a ete
	 *         interrompu correctement
	 */
	public String win() {
		threadJeu.interrupt();
		if (enCours) {
			enCours = false;
			pcs.firePropertyChange("Fin", 10, 0);
			return "win";
		}
		return null;
	}

	/**
	 * Perds la partie
	 * 
	 * @return une string contenant le resultat defaite si le thread a ete
	 *         interrompu correctement
	 */
	public String lose() {
		threadJeu.interrupt();
		if (enCours) {
			enCours = false;
			pcs.firePropertyChange("Fin", 10, 1);
			return "lose";
		}
		return null;
	}

	/**
	 * Change le joueur qui agit
	 */
	public void toggleJoueur() {
		// Désélectionner la troupe actuelle si elle existe
		if (jeuxOupi.getTroupeSelectionnee() != null) {
			Troupe troupe = jeuxOupi.getTroupeSelectionnee();
			pcs.firePropertyChange("troupe", "", null);
			jeuxOupi.deselectionnerTroupe(troupe);
		}
		
		ArrayList<Troupe> troupes = jeuxOupi.getTroupes();

		if (joueurActuel == 0) {
			joueurActuel = 1;
		} else {
			joueurActuel = 0;
		}
		System.out.println("\n🔄 Changement de joueur - C'est maintenant au tour de l'équipe " + joueurActuel);
		
		for(int i = 0; i < troupes.size(); i++) {
			troupes.get(i).setEpuisee(false);
		}

		Troupe.toggleEquipeActuelle();

		// Notification du changement d'équipe
		pcs.firePropertyChange("equipeActuelle", -1, joueurActuel);
	}

	/**
	 * Regardes si toutes les {@link Troupe} de l'utilisateur sont epuisees
	 */
	public void checkFinTour() {
		System.out.println("check fin");
		ArrayList<Troupe> troupes = jeuxOupi.getTroupes();
		int nbActionnable = 0;

		checkFinPartie();

		for (int i = 0; i < troupes.size(); i++) {
			if (!troupes.get(i).isEpuisee() && troupes.get(i).getEquipe() == joueurActuel) {
				nbActionnable += 1;
			}
		}

		System.out.println(nbActionnable);
		if (nbActionnable == 0) {
			toggleJoueur();
		}
	}

	/**
	 * Regardes si l'armee d'un des joueurs est vide, si c'est le cas, la partie est
	 * terminee donc soit gagnee ou perdue
	 */
	public void checkFinPartie() {
		ArrayList<Troupe> troupes = jeuxOupi.getTroupes();
		int nbEq1 = 0, nbEq2 = 0;

		for (int i = 0; i < troupes.size(); i++) {
			if (troupes.get(i).getEquipe() == 0) {
				nbEq1 += 1;
			} else {
				nbEq2 += 1;
			}
		}

		if (nbEq1 == 0 && nbEq2 == 0) {
			pcs.firePropertyChange("Fin", 10, -1);
		}

		if (nbEq1 == 0) {
			pcs.firePropertyChange("Fin", 10, 0);
		} else if (nbEq2 == 0) {
			pcs.firePropertyChange("Fin", 10, 1);
		}
	}
}

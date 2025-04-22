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
import troupe.Electricien;
import troupe.Genial;
import troupe.Lobotomisateur;
import troupe.Nexus;
import troupe.Oupi;
import troupe.Troupe;
import tuiles.Eau;

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

	private boolean placer = true;
	private Troupe troupePlacer;
	private int[] troupesDispo = { 2, 1, 0, 2 };
	private String[] nomTroupes = {"Oupi", "Lobotomisateur", "Electricien", "Homme Genial"};
	private int type = 0;

	// Ajouter le support pour lancer des événements de type PropertyChange
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	// Liste temporaire pour stocker les messages de combat
	private ArrayList<String> tempCombatMessages = new ArrayList<>();

	/**
	 * Constructeur de la classe {@code ZoneAnimationOupi}.
	 * 
	 * @param screenWidth  la largeur de l'écran
	 * @param screenHeight la hauteur de l'écran
	 */
	public ZoneAnimationOupi(int screenWidth, int screenHeight) {
		jeuxOupi = new JeuxOupi(screenWidth, screenHeight);

		troupePlacer = new Oupi(0, 0, 0, jeuxOupi);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (enCours) {
					dragStartX = e.getX();
					dragStartY = e.getY();

					// Applique le zoom inverse pour obtenir les coordonnees du jeu
					int gameX = (int) ((e.getX() - translateX) / zoomFactor);
					int gameY = (int) ((e.getY() - translateY) / zoomFactor);

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
				
				// Si aucune troupe n'a été cliquée, gérer le clic sur une tuile
				int ligne = gameY / jeuxOupi.getTailleTuile();
				int colonne = gameX / jeuxOupi.getTailleTuile();
				
				// Vérifier si le clic est dans les limites du plateau
				if (ligne >= 0 && ligne < jeuxOupi.getTailleTuile() && colonne >= 0
						&& colonne < jeuxOupi.getTailleTuile()) {
					
					Tuile tuileCliquee = jeuxOupi.getPlateau().getTuile(ligne, colonne);
					System.out.println("Tuile cliquée : Ligne " + (ligne + 1) + ", Colonne " + (colonne + 1));
					System.out.println(troupesDispo[type]);
					
					if (placer && jeuxOupi.getTroupeSelectionnee() == null) {
						if (troupesDispo[type] > 0) {
							if (tuileCliquee instanceof tuiles.Eau) {
								String message = "⚠️ Impossible de placer une troupe sur l'eau!";
								System.out.println(message);
								pcs.firePropertyChange("combatMessage", null, new ArrayList<String>() {{ add(message); }});
								return;
							}
							
							if (!tuileCliquee.estOccupee()) {
								if(jeuxOupi.isInZone(tuileCliquee, jeuxOupi.getPlateau().getTuile(0, 0))) {
									
									// Cas spécial pour le Nexus (qui occupe 4 tuiles)
									if (type == 4) { // Si c'est un Nexus
										// Créer temporairement un Nexus pour vérifier si les cases sont disponibles
										Nexus nexusTemp = new Nexus(colonne, ligne, 0, jeuxOupi);
										
										if (nexusTemp.peutEtrePlacé()) {
											nouvelleTroupe(type);
											troupePlacer.setCol(colonne);
											troupePlacer.setLig(ligne);
											
											// Occuper toutes les tuiles du Nexus
											if (troupePlacer instanceof Nexus) {
												((Nexus) troupePlacer).occuperTuiles();
											} else {
												tuileCliquee.setOccupee(true);
											}
											
											troupesDispo[type]--;
											System.out.println("Il vous reste "+ troupesDispo[type]+ " troupes de ce type");
											pcs.firePropertyChange("troupes restantes",0,troupesDispo);
											jeuxOupi.addTroupe(troupePlacer);
											System.out.println("Nexus placé et occupe 2x2 cases");
										} else {
											String message = "⚠️ Impossible de placer le Nexus ici - besoin de 4 cases libres (2x2)!";
											System.out.println(message);
											pcs.firePropertyChange("combatMessage", null, new ArrayList<String>() {{ add(message); }});
										}
									} else {
										// Cas normal pour les autres troupes (qui occupent 1 tuile)
										nouvelleTroupe(type);
										troupePlacer.setCol(colonne);
										troupePlacer.setLig(ligne);
										
										tuileCliquee.setOccupee(true);
										troupesDispo[type]--;
										System.out.println("Il vous reste "+ troupesDispo[type]+ " troupes de ce type");
										pcs.firePropertyChange("troupes restantes",0,troupesDispo);
										jeuxOupi.addTroupe(troupePlacer);
										System.out.println("Troupe placee");
									}
								
									repaint();
								} else {
									System.out.println("Veuillez selectionner une tuile dans la zone de placement");
								}
							} else {
								System.out.println("Tuile occupee");
							}
						} else {
							System.out.println("Vous n'avez plus de " + nomTroupes[0]);
						}
					}
				}
				
				// En mode attaque, essayer de sélectionner une troupe à attaquer
				if (modeAttaque && jeuxOupi.getTroupeSelectionnee() != null && !placer) {
					tempCombatMessages.clear(); // Effacer les messages précédents
					
					Troupe troupeCible = jeuxOupi.getTroupeA(gameX, gameY);
					if (troupeCible != null) {
						String msg = "⚔️ Tentative d'attaque sur " + troupeCible.getClass().getSimpleName();
						System.out.println(msg);
						tempCombatMessages.add(msg);
						
						// Attaquer la troupe ciblée
						boolean attaqueReussie = jeuxOupi.attaquerTroupe(troupeCible);
						if (attaqueReussie) {
							msg = "✅ Attaque réussie!";
							System.out.println(msg);
							tempCombatMessages.add(msg);
							
							if(jeuxOupi.getTroupeSelectionnee() != null){
								int lvlUp = jeuxOupi.getTroupeSelectionnee().levelUp();
								
								jeuxOupi.getTroupeSelectionnee().setEpuisee(true);
								if(lvlUp > 0) {
									pcs.firePropertyChange("level", 0, lvlUp);
								} else {
									jeuxOupi.deselectionnerTroupeAct();
								}
							}
							checkFinTour();
						}
						// Désactiver le mode attaque après une tentative
						modeAttaque = false;
						sendCombatMessages();
						return;
					} else {
						String msg = "❌ Pas de troupe à attaquer ici! Cliquez sur une troupe ennemie.";
						System.out.println(msg);
						tempCombatMessages.add(msg);
						sendCombatMessages();
						return;
					}
				}
				

				if (cliquee == null && jeuxOupi.getTroupeSelectionnee() != null) {
					pcs.firePropertyChange("troupe", "", null);
					jeuxOupi.deselectionnerTroupeAct();
				} else if (cliquee != null && !cliquee.equals(jeuxOupi.getTroupeSelectionnee())) {
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
						tempCombatMessages.clear();
						String msg = "📋 Désélection de la troupe avec touche Echap";
						System.out.println(msg);
						tempCombatMessages.add(msg);
						
						pcs.firePropertyChange("troupe", "", null);
						jeuxOupi.deselectionnerTroupeAct();
						modeAttaque = false; // Désactiver le mode attaque si actif
						
						sendCombatMessages();
					}
					return;
				}

				if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					if (placer) {
						int id = jeuxOupi.delTroupe(troupe);
						Tuile tuileCliquee = jeuxOupi.getPlateau().getTuile(troupe.getLig(), troupe.getCol());
						tuileCliquee.setOccupee(false);
						troupesDispo[id]++;
						System.out.println("Troupes de ce type dispo : " + troupesDispo[id]);
						pcs.firePropertyChange("troupes restantes",0,troupesDispo);
					}
				}

				if (troupe != null && !placer) {
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
		tempCombatMessages.clear(); // Effacer les messages précédents
		
		if (jeuxOupi.getTroupeSelectionnee() != null) {
			Troupe troupe = jeuxOupi.getTroupeSelectionnee();
			if (troupe.getEquipe() != joueurActuel) {
				String msg = "⚠️ Impossible d'attaquer avec une troupe ennemie.";
				System.out.println(msg);
				tempCombatMessages.add(msg);
				
				msg = "ℹ️ Appuyez sur la touche ECHAP pour désélectionner cette troupe.";
				System.out.println(msg);
				tempCombatMessages.add(msg);
				sendCombatMessages();
				return;
			}

			modeAttaque = !modeAttaque; // Toggle attack mode
			if (modeAttaque) {
				String msg = "🔴 MODE ATTAQUE ACTIVÉ! Cliquez sur une troupe ennemie à attaquer.";
				System.out.println(msg);
				tempCombatMessages.add(msg);
				
				msg = "   - L'ennemi doit être adjacent (distance 1)";
				System.out.println(msg);
				tempCombatMessages.add(msg);
				
				msg = "   - Appuyez sur F ou X à nouveau pour annuler";
				System.out.println(msg);
				tempCombatMessages.add(msg);
				
				msg = "   - Appuyez sur ECHAP pour désélectionner la troupe";
				System.out.println(msg);
				tempCombatMessages.add(msg);
			} else {
				String msg = "🟢 Mode attaque désactivé.";
				System.out.println(msg);
				tempCombatMessages.add(msg);
			}
		} else {
			String msg = "⚠️ Veuillez d'abord sélectionner une troupe pour attaquer.";
			System.out.println(msg);
			tempCombatMessages.add(msg);
		}
		
		sendCombatMessages();
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
			pcs.firePropertyChange("troupe", "", null);
			jeuxOupi.deselectionnerTroupeAct();
		}

		ArrayList<Troupe> troupes = jeuxOupi.getTroupes();

		if (joueurActuel == 0) {
			joueurActuel = 1;
		} else {
			joueurActuel = 0;
		}
		System.out.println("\n🔄 Changement de joueur - C'est maintenant au tour de l'équipe " + joueurActuel);

		for (int i = 0; i < troupes.size(); i++) {
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
	

	/**
	 * Change le mode de jeu pour permettre de placer les unitees
	 */
	public void finirPlacer() {
		placer = false;
		System.out.println("Mode placement = " + placer);
		jeuxOupi.finirPlacer();
		requestFocus();
	}

	/**
	 * Change le type de troupe qui sera ajoute
	 * 
	 * @param type Le nouveau type de troupe
	 */
	public void changerType(int type) {
		this.type = type;
	}

	/**
	 * Cree une nouvelle troupe du type fournit
	 * 
	 * @param type Le type de la nouvelle troupe
	 */
	public void nouvelleTroupe(int type) {
		switch (type) {
		case 0:
			troupePlacer = new Oupi(0, 0, 0, jeuxOupi);
			System.out.println("Type change Oupi " + type);
			break;
		case 1:
			troupePlacer = new Electricien(0, 0, 0, jeuxOupi);
			System.out.println("Type change Elec " + type);
			break;
		case 2:
			troupePlacer = new Genial(0, 0, 0, jeuxOupi);
			System.out.println("Type change Genial " + type);
			break;
		case 3:
			troupePlacer = new Lobotomisateur(0, 0, 0, jeuxOupi);
			System.out.println("Type change Lobo " + type);
			break;
		case 4:
			troupePlacer = new Nexus(0, 0, 0, jeuxOupi);
			System.out.println("Type change Nexus " + type);
			break;
		}
	}
	
	public void setVisible(boolean aFlag) {
		super.setVisible(aFlag);

		pcs.firePropertyChange("troupes restantes",0,troupesDispo);
	}
	
	/**
	 * Envoie les messages de combat à l'interface utilisateur.
	 */
	public void sendCombatMessages() {
		// Ajouter les messages temporaires à la liste principale
		ArrayList<String> messagesToSend = new ArrayList<>(tempCombatMessages);
		if (!messagesToSend.isEmpty()) {
			pcs.firePropertyChange("combatMessages", null, messagesToSend);
			tempCombatMessages.clear(); // Vider la liste temporaire après envoi
		}
		
		// Envoyer aussi les messages générés par la classe JeuxOupi
		ArrayList<String> jeuxOupiMessages = jeuxOupi.getCombatMessages();
		if (!jeuxOupiMessages.isEmpty()) {
			pcs.firePropertyChange("combatMessages", null, jeuxOupiMessages);
			jeuxOupi.clearCombatMessages();
		}
		
		// Envoyer aussi les messages générés par la classe Troupe
		ArrayList<String> troupeMessages = Troupe.getCombatMessages();
		if (!troupeMessages.isEmpty()) {
			pcs.firePropertyChange("combatMessages", null, troupeMessages);
			Troupe.clearCombatMessages();
		}
	}

	public JeuxOupi getJeuxOupi() {
		return jeuxOupi;
	}
}

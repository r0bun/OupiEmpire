package jeu_oupi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import plateau.Plateau;
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
	private Image backgroundImage; // Background image for areas without tiles

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
	public Plateau plateau;
	private int translateX = 0;
	private int translateY = 0;
	private int dragStartX;
	private int dragStartY;
	private boolean isDragging = false;

	// Camera boundary constants - margin beyond plateau edge
	private final int CAMERA_MARGIN = 200; // pixels beyond the plateau edge
	private final double BACKGROUND_SCALE = 0.5; // Scale factor for background tiles (smaller)

	// Attack mode state
	private boolean modeAttaque = false;

	private boolean placer = true;
	private Troupe troupePlacer;
	private int equipeQuiPlace = 0; // Nouvelle variable pour suivre l'équipe qui place
	private int[] troupesDispoEquipe0 = { 2, 1, 0, 2 };
	private int[] troupesDispoEquipe1 = { 2, 1, 0, 2 }; // Troupes pour équipe 1
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
		
		// Load the background image
		try {
			backgroundImage = new ImageIcon("res/bak/map_background.jpg").getImage();
			if (backgroundImage.getWidth(null) <= 0) {
				System.err.println("Could not load background image. Using fallback.");
				backgroundImage = new ImageIcon("res/bak/map_background.png").getImage();
			}
		} catch (Exception e) {
			System.err.println("Error loading background image: " + e.getMessage());
			// Create a fallback pattern if image loading fails
			backgroundImage = createFallbackBackground();
		}

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
					//System.out.println(troupesDispo[type]);
					
					if (placer && jeuxOupi.getTroupeSelectionnee() == null) {
						
						//Change les troupes dispos en fonction de l'équipe qui place.
						int[] troupesDispo = (equipeQuiPlace == 0) ? troupesDispoEquipe0 : troupesDispoEquipe1;
						
						if (troupesDispo[type] > 0) {
							if (tuileCliquee instanceof tuiles.Eau) {
								String message = "⚠️ Impossible de placer une troupe sur l'eau!";
								System.out.println(message);
								getPcs().firePropertyChange("combatMessage", null, new ArrayList<String>() {{ add(message); }});
								return;
							}
							
							if (!tuileCliquee.estOccupee()) {
								
								// Vérifie si la tuile est dans la zone de placage.
								if(jeuxOupi.isInZone(tuileCliquee, jeuxOupi.getPlateau().getTuile(0, 0), equipeQuiPlace)) {
									
									// Début Cas spécial pour le Nexus (qui occupe 4 tuiles)
									if (type == 4) { // Si c'est un Nexus
										// Créer temporairement un Nexus pour vérifier si les cases sont disponibles
										Nexus nexusTemp = new Nexus(colonne, ligne, 0, jeuxOupi);
										
										if (nexusTemp.peutEtrePlacé()) {
											nouvelleTroupe(type, equipeQuiPlace);
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
											getPcs().firePropertyChange("troupes restantes",0,troupesDispo);
											jeuxOupi.addTroupe(troupePlacer);
											System.out.println("Nexus placé et occupe 2x2 cases");
										} else {
											String message = "⚠️ Impossible de placer le Nexus ici - besoin de 4 cases libres (2x2)!";
											System.out.println(message);
											getPcs().firePropertyChange("combatMessage", null, new ArrayList<String>() {{ add(message); }});
										}
										// Fin du cas Nexus
										
										
									} else {
										// Cas normal pour les autres troupes (qui occupent 1 tuile)
										nouvelleTroupe(type, equipeQuiPlace);
										troupePlacer.setCol(colonne);
										troupePlacer.setLig(ligne);
										
										tuileCliquee.setOccupee(true);
										troupesDispo[type]--;
										System.out.println("Il vous reste "+ troupesDispo[type]+ " troupes de ce type");
										getPcs().firePropertyChange("troupes restantes",0,troupesDispo);
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
									getPcs().firePropertyChange("level", 0, lvlUp);
								} else {
									jeuxOupi.deselectionnerTroupeAct();
								}
							}
							checkFinTour();
						}
						// Désactiver le mode attaque après une tentative
						modeAttaque = false;
						jeuxOupi.setModeAttaque(modeAttaque);
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
				
				// Request focus after any mouse click
				requestFocusInWindow();

				if (cliquee == null && jeuxOupi.getTroupeSelectionnee() != null) {
					getPcs().firePropertyChange("troupe", "", null);
					jeuxOupi.deselectionnerTroupeAct();
				} else if (cliquee != null && !cliquee.equals(jeuxOupi.getTroupeSelectionnee())) {
					System.out.println("click de sélection effectuer");
					getPcs().firePropertyChange("troupe", "", cliquee);
					jeuxOupi.selectionnerTroupe(cliquee);
				}
			}
		});

		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (isDragging) {
					int newTranslateX = translateX + e.getX() - dragStartX;
					int newTranslateY = translateY + e.getY() - dragStartY;
					
					// Calculate camera bounds
					int plateauWidthPx = jeuxOupi.getTailleTuile() * jeuxOupi.getPlateau().getColonnes();
					int plateauHeightPx = jeuxOupi.getTailleTuile() * jeuxOupi.getPlateau().getLignes();
					
					// Min bounds (right/bottom edge of plateau) - allow scrolling right/down to see whole plateau
					int minTranslateX = getWidth() - (int)(plateauWidthPx * zoomFactor) - CAMERA_MARGIN;
					int minTranslateY = getHeight() - (int)(plateauHeightPx * zoomFactor) - CAMERA_MARGIN;
					
					// Max bounds (left/top edge of plateau + margin)
					int maxTranslateX = CAMERA_MARGIN;
					int maxTranslateY = CAMERA_MARGIN;
					
					// Apply constraints
					translateX = Math.min(maxTranslateX, Math.max(minTranslateX, newTranslateX));
					translateY = Math.min(maxTranslateY, Math.max(minTranslateY, newTranslateY));
					
					dragStartX = e.getX();
					dragStartY = e.getY();
					repaint();
				}
			}
			
			 @Override
			    public void mouseClicked(MouseEvent e) {
			        System.out.println("\n=== Mouse Click Event ===");
			        System.out.println("Click coordinates: " + e.getX() + ", " + e.getY());
			        
			        // ...existing troop selection code...
			        
			        System.out.println("Selected troupe after click: " + jeuxOupi.getTroupeSelectionnee());
			        if (jeuxOupi.getTroupeSelectionnee() != null) {
			            System.out.println("Selected troupe position: ");
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

				 // Store old zoom factor for boundary calculations
				double oldZoomFactor = zoomFactor;

				// Adjust zoom based on wheel rotation
				if (e.getPreciseWheelRotation() < 0) {
					// Zoom in
					zoomFactor = Math.min(MAX_ZOOM, zoomFactor + ZOOM_STEP);
				} else {
					// Zoom out
					zoomFactor = Math.max(MIN_ZOOM, zoomFactor - ZOOM_STEP);
				}

				// Calculate new translation to keep the same game position under cursor
				int newTranslateX = (int)(mouseX - oldGameX * zoomFactor);
				int newTranslateY = (int)(mouseY - oldGameY * zoomFactor);

				// Apply the same boundary constraints as in mouseDragged
				plateau = jeuxOupi.getPlateau();
				int plateauWidthPx = jeuxOupi.getTailleTuile() * plateau.getColonnes();
				int plateauHeightPx = jeuxOupi.getTailleTuile() * plateau.getLignes();
				
				// Min bounds (right/bottom edge of plateau)
				int minTranslateX = getWidth() - (int)(plateauWidthPx * zoomFactor) - CAMERA_MARGIN;
				int minTranslateY = getHeight() - (int)(plateauHeightPx * zoomFactor) - CAMERA_MARGIN;
				
				// Max bounds (left/top edge of plateau + margin)
				int maxTranslateX = CAMERA_MARGIN;
				int maxTranslateY = CAMERA_MARGIN;
				
				// Apply constraints
				translateX = Math.min(maxTranslateX, Math.max(minTranslateX, newTranslateX));
				translateY = Math.min(maxTranslateY, Math.max(minTranslateY, newTranslateY));
				
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
						
						getPcs().firePropertyChange("troupe", "", null);
						jeuxOupi.deselectionnerTroupeAct();
						modeAttaque = false; // Désactiver le mode attaque si actif
						jeuxOupi.setModeAttaque(modeAttaque);
						
						sendCombatMessages();
					}
					return;
				}

				if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					if (placer && troupe != null) {
						// Check if troupe is Nexus - prevent deletion
						if (troupe.getIsNexus()) {
							String message = "⚠️ Impossible de supprimer le Nexus!";
							System.out.println(message);
							getPcs().firePropertyChange("combatMessage", null, new ArrayList<String>() {{ add(message); }});
							return;
						}

						if(troupe.getEquipe() != joueurActuel) {
							String message = "⚠️ Impossible de supprimer les troupes adverses!";
							System.out.println(message);
							getPcs().firePropertyChange("combatMessage", null, new ArrayList<String>() {{ add(message); }});
							return;
						}
						
						int[] troupesDispo = (equipeQuiPlace == 0) ? troupesDispoEquipe0 : troupesDispoEquipe1;
						// Identify the type of troupe
						int id = 0;
						if (troupe instanceof Oupi) id = 0;
						else if (troupe instanceof Lobotomisateur) id = 3;
						else if (troupe instanceof Electricien) id = 1;
						else if (troupe instanceof Genial) id = 2;
						else id = 4; // For Nexus or other types
						
						// Delete the troupe and free the tile
						jeuxOupi.delTroupe(troupe);
						Tuile tuileCliquee = jeuxOupi.getPlateau().getTuile(troupe.getLig(), troupe.getCol());
						tuileCliquee.setOccupee(false);
						
						// Increment available troops counter
						troupesDispo[id]++;
						System.out.println("Troupes de ce type dispo : " + troupesDispo[id]);
						getPcs().firePropertyChange("troupes restantes", 0, troupesDispo);
						getPcs().firePropertyChange("troupe", "", null);
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
							getPcs().firePropertyChange("troupe", "", null);
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
	 * Creates a fallback background pattern if the image can't be loaded
	 */
	private Image createFallbackBackground() {
		// Create a simple checkered pattern as fallback
		BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setColor(new Color(240, 230, 200)); // Sandy color
		g.fillRect(0, 0, 64, 64);
		g.setColor(new Color(230, 220, 190)); // Slightly darker
		g.fillRect(0, 0, 32, 32);
		g.fillRect(32, 32, 32, 32);
		g.dispose();
		return img;
	}

	/**
	 * Active le mode attaque, permettant de sélectionner une troupe à attaquer.
	 */
	public void activerModeAttaque() {
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
			jeuxOupi.setModeAttaque(modeAttaque);
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
		this.getPcs().addPropertyChangeListener(listener);
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
		
		// Draw background pattern
		if (backgroundImage != null) {
			// Get plateau dimensions
			int tileSize = jeuxOupi.getTailleTuile();
			int plateauWidth = jeuxOupi.getPlateau().getColonnes() * tileSize;
			int plateauHeight = jeuxOupi.getPlateau().getLignes() * tileSize;
			
			// Extend the background area beyond the plateau by CAMERA_MARGIN * 2
			int extendedMargin = CAMERA_MARGIN * 2;
			int bgStartX = -extendedMargin;
			int bgStartY = -extendedMargin;
			int bgWidth = plateauWidth + (extendedMargin * 2);
			int bgHeight = plateauHeight + (extendedMargin * 2);
			
			// Calculate scaled dimensions for smaller tiles
			int imgWidth = backgroundImage.getWidth(null);
			int imgHeight = backgroundImage.getHeight(null);
			
			if (imgWidth > 0 && imgHeight > 0) {
				// Calculate scaled dimensions
				int scaledWidth = (int)(imgWidth * BACKGROUND_SCALE);
				int scaledHeight = (int)(imgHeight * BACKGROUND_SCALE);
				
				// Add different overlaps to prevent horizontal and vertical gaps
				int horizontalOverlap = 4; // Increased horizontal overlap to fix horizontal gaps
				int verticalOverlap = 2;   // Keep the same vertical overlap
				
				// Draw smaller tiles to create a denser pattern
				for (int x = bgStartX; x < bgStartX + bgWidth; x += scaledWidth) {
					for (int y = bgStartY; y < bgStartY + bgHeight; y += scaledHeight) {
						g2d.drawImage(backgroundImage, 
							x, y, x + scaledWidth + horizontalOverlap, y + scaledHeight + verticalOverlap,
							0, 0, imgWidth, imgHeight, 
							null);
					}
				}
			}
		}

		jeuxOupi.dessiner(g2d);

		// Restore the original transform
		g2d.setTransform(oldTransform);
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
			getPcs().firePropertyChange("Fin", 10, 0);
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
			getPcs().firePropertyChange("Fin", 10, 1);
			return "lose";
		}
		return null;
	}

	/**
	 * Change le joueur qui agit
	 */
	public void toggleJoueur() {
		checkFinPartie();
		// Désélectionner la troupe actuelle si elle existe
		if (jeuxOupi.getTroupeSelectionnee() != null) {
			getPcs().firePropertyChange("troupe", "", null);
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
		getPcs().firePropertyChange("equipeActuelle", -1, joueurActuel);
		
		// Centrer la caméra sur le Nexus de l'équipe active
	    Nexus nexusActif = jeuxOupi.getNexusEquipe(joueurActuel);
	    if (nexusActif != null) {
	        // Utiliser les coordonnées centrales du Nexus (2x2 tuiles)
	        centrerCameraSur(
	            nexusActif.getCol() + 1,  // +1 pour être au centre du Nexus
	            nexusActif.getLig() + 1
	        );
	    }
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
		

		if (nbEq1 == 0 || nbEq2 == 0 || !containsNexus(troupes)) {
			getPcs().firePropertyChange("Fin", 10, -1);
		}
	}
	
	public ArrayList<Troupe> getPlayerTroupes (int player){
		
		ArrayList<Troupe> troupes = jeuxOupi.getTroupes();
		ArrayList<Troupe> troupesJoueur = new ArrayList<>();
		
		for (int i = 0; i < troupes.size(); i++) {
			if(troupes.get(i).getEquipe() == player) {
				troupesJoueur.add(troupes.get(i));
			}
		}
		return troupesJoueur;
	}
	
	public boolean containsNexus (ArrayList<Troupe> liste) {
		for(Troupe troupe : liste) {
			if(troupe.getIsNexus() == true) {
				return true;
			}
		}
		return false;
	}
	

	/**
	 * Change le mode de jeu pour permettre de placer les unitees
	 */
	public void finirPlacer() {
		
		if (equipeQuiPlace == 0) {
	        // Passer à l'équipe 1
	        equipeQuiPlace = 1;
	        toggleJoueur();
	        getPcs().firePropertyChange("equipeActuelle", 0, 1);
	        int[] troupesDispo = (equipeQuiPlace == 0) ? troupesDispoEquipe0 : troupesDispoEquipe1;
	        getPcs().firePropertyChange("troupes restantes", null, troupesDispo);
	        System.out.println("Phase de placement : Au tour de l'équipe " + equipeQuiPlace);
	        
	        // Réinitialiser la zone de placement pour l'équipe 1
	        jeuxOupi.initZonePlacementEquipe1();
	        
	     // Centrer la caméra sur le Nexus de l'équipe 0 pour commencer la partie
	        Nexus nexusEquipe1 = jeuxOupi.getNexusEquipe(1);
	        if (nexusEquipe1 != null) {
	            centrerCameraSur(nexusEquipe1);
	        }
	    } else {
	        // Fin de la phase de placement
	        placer = false;
	        System.out.println("Fin de la phase de placement");
	        jeuxOupi.finirPlacer();
	        toggleJoueur();
	        
	     // Centrer la caméra sur le Nexus de l'équipe 0 pour commencer la partie
	        Nexus nexusEquipe0 = jeuxOupi.getNexusEquipe(0);
	        if (nexusEquipe0 != null) {
	            centrerCameraSur(nexusEquipe0);
	        }
	        requestFocus();
	    }
		
		if (!placer) {
			// Afficher ActionPanel une fois que le placement est terminé
			getPcs().firePropertyChange("showActionPanel", false, true);
		}
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
	public void nouvelleTroupe(int type, int equipe) {
		switch (type) {
		case 0:
			troupePlacer = new Oupi(0, 0, equipe, jeuxOupi);
			System.out.println("Type change Oupi " + type);
			break;
		case 1:
			troupePlacer = new Electricien(0, 0, equipe, jeuxOupi);
			System.out.println("Type change Elec " + type);
			break;
		case 2:
			troupePlacer = new Genial(0, 0, equipe, jeuxOupi);
			System.out.println("Type change Genial " + type);
			break;
		case 3:
			troupePlacer = new Lobotomisateur(0, 0, equipe, jeuxOupi);
			System.out.println("Type change Lobo " + type);
			break;
		case 4:
			troupePlacer = new Nexus(0, 0, equipe, jeuxOupi);
			System.out.println("Type change Nexus " + type);
			break;
		}
	}
	
	public void setVisible(boolean aFlag) {
		super.setVisible(aFlag);

		int[] troupesDispo = (equipeQuiPlace == 0) ? troupesDispoEquipe0 : troupesDispoEquipe1;
		
		getPcs().firePropertyChange("troupes restantes",0,troupesDispo);
	}
	
	/**
	 * Envoie les messages de combat à l'interface utilisateur.
	 */
	public void sendCombatMessages() {
		// Ajouter les messages temporaires à la liste principale
		ArrayList<String> messagesToSend = new ArrayList<>(tempCombatMessages);
		if (!messagesToSend.isEmpty()) {
			getPcs().firePropertyChange("combatMessages", null, messagesToSend);
			tempCombatMessages.clear(); // Vider la liste temporaire après envoi
		}
		
		// Envoyer aussi les messages générés par la classe JeuxOupi
		ArrayList<String> jeuxOupiMessages = jeuxOupi.getCombatMessages();
		if (!jeuxOupiMessages.isEmpty()) {
			getPcs().firePropertyChange("combatMessages", null, jeuxOupiMessages);
			jeuxOupi.clearCombatMessages();
		}
		
		// Envoyer aussi les messages générés par la classe Troupe
		ArrayList<String> troupeMessages = Troupe.getCombatMessages();
		if (!troupeMessages.isEmpty()) {
			getPcs().firePropertyChange("combatMessages", null, troupeMessages);
			Troupe.clearCombatMessages();
		}
	}

	public JeuxOupi getJeuxOupi() {
		return jeuxOupi;
	}
	
	/**
	 * Centre la caméra sur les coordonnées spécifiées avec les limites de mouvement
	 */
	public void centrerCameraSur(int x, int y) {
		// Calcule la position centrale de l'écran
		int screenCenterX = getWidth() / 2;
		int screenCenterY = getHeight() / 2;
		
		plateau = jeuxOupi.getPlateau();
		int plateauWidthPx = jeuxOupi.getTailleTuile() * plateau.getColonnes();
		int plateauHeightPx = jeuxOupi.getTailleTuile() * plateau.getLignes();
		
		// Calcule le décalage initial pour centrer la position
		int newTranslateX = screenCenterX - (int)(x * jeuxOupi.tailleTuile * zoomFactor);
		int newTranslateY = screenCenterY - (int)(y * jeuxOupi.tailleTuile * zoomFactor);
		
		// Contraintes comme dans mouseDragged
		int minTranslateX = getWidth() - (int)(plateauWidthPx * zoomFactor) - CAMERA_MARGIN;
		int minTranslateY = getHeight() - (int)(plateauHeightPx * zoomFactor) - CAMERA_MARGIN;
		int maxTranslateX = CAMERA_MARGIN;
		int maxTranslateY = CAMERA_MARGIN;
		
		// Applique les contraintes
		translateX = Math.min(maxTranslateX, Math.max(minTranslateX, newTranslateX));
		translateY = Math.min(maxTranslateY, Math.max(minTranslateY, newTranslateY));
		
		repaint();
	}

	// Surcharge pour centrer sur une troupe
	public void centrerCameraSur(Troupe troupe) {
	    if (troupe != null) {
	        centrerCameraSur(troupe.getCol(), troupe.getLig());
	    }
	}
	
	public int getJoueurActuel() {
		return joueurActuel;
	}

	public PropertyChangeSupport getPcs() {
		return pcs;
	}
}

package application;

import java.awt.*;
import javax.swing.*;

import jeu_oupi.GameManager;
import jeu_oupi.ZoneAnimationOupi;
import troupe.Nexus;
import troupe.Troupe;
import ecrans_jeu.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import animations.ChangePlayerAnimation;
import animations.EndGameAnimation;

/**
 * La classe {@code appLaunch} représente la fenêtre principale de l'application
 * pour l'animation Oupi. Cette classe étend {@link JFrame} et configure le
 * cadre principal et le panneau de contenu pour l'application. Elle initialise
 * le composant {@link ZoneAnimationOupi} et configure la fenêtre pour être en
 * plein écran.
 * 
 * @author Badr Rifki
 * 
 */
public class appLaunch extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;

	private ZoneAnimationOupi zoneAnimationOupi, minimap;

	private Debut ecranDebut;
	
	private Stats stats;
	
	private CadreInfo cadreInfo;
	
	private ActionPanel actionPanel;
	
	private Fin fin;
	
	private JLabel lblEtat;
	
	private JTextArea textAreaAttaque;
	
	private JLabel lblAnimChangeTurn;
	
	private PartieTerminee pagePartieTerminee = new PartieTerminee();
	
	private PlacementPanel placementPanel;
	
	// Pour suivre l'équipe actuelle (0 ou 1)
	private int equipeActuelle = 0;
	
	// Couleurs pour chaque équipe
	private final Color COULEUR_EQUIPE_0 = new Color(230, 255, 230); // Vert pâle
	private final Color COULEUR_EQUIPE_1 = new Color(255, 230, 230); // Rouge pâle

	
	/**
	 * Lance l'application.
	 * 
	 * @param args les arguments de la ligne de commande
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				appLaunch frame = new appLaunch();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Crée le cadre.
	 */
	public appLaunch() {
		// Régler sans décoration pour le mode plein écran (supprime la barre de titre)
		// setUndecorated(true);

		// Frame setup
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setExtendedState(JFrame.MAXIMIZED_BOTH);
	    setUndecorated(true);
	    setAlwaysOnTop(true);

	    // Get screen dimensions
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		DisplayMode dm = gd.getDisplayMode();
		int screenWidth = dm.getWidth();
		int screenHeight = dm.getHeight();
		
		/*minimap = new ZoneAnimationOupi(screenWidth/5, screenHeight/5);
		minimap.setBounds(1020, 331, 482, 485);
		contentPane.add(minimap);*/
		
	    // === JLayeredPane to hold everything ===
	    JLayeredPane layeredPane = new JLayeredPane();
	    layeredPane.setLayout(null); // Absolute positioning
	    layeredPane.setPreferredSize(new Dimension(screenWidth, screenHeight));
	    setContentPane(layeredPane);
	    
	    // === Add background image ===
	    ImageIcon backgroundIcon = new ImageIcon("res/bak/background2_jeu.png"); // Remplace par ton chemin
	    JLabel backgroundLabel = new JLabel(backgroundIcon);
	    backgroundLabel.setBounds(0, 0, screenWidth, screenHeight);
	    layeredPane.add(backgroundLabel, Integer.valueOf(-1)); // tout au fond
	    
	    // === Add border image above the animation zone ===
	    JPanel borderPanel = new JPanel();
	    borderPanel.setLayout(null); // Absolute positioning
	    borderPanel.setOpaque(false); // Make the panel transparent
	    borderPanel.setBounds(-125, -120, (int) screenWidth, screenHeight ); // Same size as zoneAnimationOupi
	    
	    // === Add border image above the animation zone ===
	    JPanel borderPanel2 = new JPanel();
	    borderPanel2.setLayout(null); // Absolute positioning
	    borderPanel2.setOpaque(false); // Make the panel transparent
	    borderPanel2.setBounds(-364, 425, (int) screenWidth, screenHeight ); // Same size as zoneAnimationOupi

	    // Add the border image to the panel
	    ImageIcon borderIcon = new ImageIcon("res/bak/cadre_jeu.png"); // Replace with your border image path
	    JLabel borderLabel = new JLabel(borderIcon);
	    borderLabel.setBounds(0, 0, borderPanel.getWidth(), borderPanel.getHeight()); // Fill the panel
	    borderPanel.add(borderLabel);
	    
	    // Add the border image to the panel
	    ImageIcon borderIcon2 = new ImageIcon("res/bak/cadre_bas.png"); // Replace with your border image path
	    JLabel borderLabel2 = new JLabel(borderIcon2);
	    borderLabel2.setBounds(0, 0, borderPanel.getWidth(), borderPanel.getHeight()); // Fill the panel
	    borderPanel2.add(borderLabel2);

	    // Add the border panel to the layered pane above the animation zone
	    layeredPane.add(borderPanel, Integer.valueOf(2)); // Add above the animation zone
	    
	 // Add the border panel2 to the layered pane above the animation zone
	    layeredPane.add(borderPanel2, Integer.valueOf(2)); // Add above the animation zone
	    
	    // === Main content panel ===
	    contentPane = new JPanel();
	    contentPane.setLayout(null);
	    contentPane.setBounds(0, 0, screenWidth, screenHeight);
	    //contentPane.setBackground(COULEUR_EQUIPE_0); // Your team color background
	    contentPane.setOpaque(false); // Pour que l'image soit visible à travers
	    layeredPane.add(contentPane, Integer.valueOf(0));
		
		// Add Nexus label
		//JLabel lblNexus = new JLabel("1");
		//lblNexus.setBounds(600, 936, 56, 16);
		//contentPane.add(lblNexus);
		
		ecranDebut = new Debut(screenWidth, screenHeight);
		ecranDebut.setBounds(50, 30, (int) (screenWidth / 2), (int) (screenHeight*0.725));
		contentPane.add(ecranDebut);
		
		// === animation panel (on top) ===
	    String pathToFrames = "res\\png_animations\\Change_Player";
	    ChangePlayerAnimation animationPanel = new ChangePlayerAnimation();
	    animationPanel.setBounds(0, 0, screenWidth, screenHeight);
	    animationPanel.setOpaque(false); // Important!
	    animationPanel.setVisible(false);
	    layeredPane.add(animationPanel, Integer.valueOf(3)); // On top of game content  // Add the animation panel to the JFrame
	    
	    //=== End game animation ===
	    String animationFolderPath = "res\\png_animations\\Fin_Partie";
	    EndGameAnimation endAnimationPanel = new EndGameAnimation(animationFolderPath);
	    endAnimationPanel.setBounds(0, 0, screenWidth, screenHeight);
	    endAnimationPanel.setOpaque(false); // Important!
	    endAnimationPanel.setVisible(false);
	    layeredPane.add(endAnimationPanel, Integer.valueOf(4)); // On top of game content  // Add the animation panel to the JFrame
	    
	    // Définis ce qu’il faut faire quand l’animation se termine
	    endAnimationPanel.setAnimationEndListener(() -> {
	        System.out.println("Animation terminée !");
	        pagePartieTerminee.setVisible(true);
            dispose();
	    });

	    //Zone d'animation ou se passe le jeu
	    zoneAnimationOupi = GameManager.getInstance().getZoneAnimationOupi();
	    if (zoneAnimationOupi != null) {
	        zoneAnimationOupi.setBounds(50, 30, (int) (screenWidth / 2), (int) (screenHeight * 0.725));
	        zoneAnimationOupi.setVisible(false);
	        contentPane.add(zoneAnimationOupi);
	    } else {
	        System.err.println("Erreur : ZoneAnimationOupi n'a pas été initialisée !");
	    }
	    
	    // Action Panel
	    actionPanel = new ActionPanel(zoneAnimationOupi);
	    actionPanel.setBounds(30, 840, 1000, 175); // Utilisez les mêmes dimensions que placementPanel
	    actionPanel.setVisible(false); // Caché au début
	    contentPane.add(actionPanel);
	    
	    zoneAnimationOupi.addPropertyChangeListener(new PropertyChangeListener() {
	        @Override
	        public void propertyChange(PropertyChangeEvent evt) {
	            // ...existing listeners...
	            
	            if (evt.getPropertyName().equals("showActionPanel")) {
	                boolean show = (boolean) evt.getNewValue();
	                placementPanel.setVisible(!show);
	                actionPanel.setVisible(show);
	            }
	        }
	    });
	    
		
		// Écouteur pour réagir aux différents évènements générés par ZoneAnimationOupi.
		zoneAnimationOupi.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals("Fin")) {
					
					endAnimationPanel.setVisible(true);
					endAnimationPanel.startAnimation();
					//zoneAnimationOupi.setVisible(false);
				}
				
				// Lorsqu'une troupe est sélectionnée ou mise a jour, update les stats affichées dans player card.
				if(evt.getPropertyName().equals("troupe")) {
					stats.updateTroupe((Troupe) evt.getNewValue(), equipeActuelle);
				}
				
				// Détection du changement d'équipe
				if(evt.getPropertyName().equals("equipeActuelle")) {
					int nouvelleEquipe = (int) evt.getNewValue();
					
					equipeActuelle = nouvelleEquipe;
					animationPanel.setVisible(true);
			        animationPanel.startAnimation(equipeActuelle);
			       	
					cadreInfo.updateCadreInfo(GameManager.getInstance().getZoneAnimationOupi().getJeuxOupi().getTroupePlayer(equipeActuelle), equipeActuelle);
					stats.updateTroupe( null, equipeActuelle);
					actionPanel.setJoueurActuel(equipeActuelle);
					
				
				}
				
				// Quand des troupes sont ajoutées au tableau, leur quantité change.
				if(evt.getPropertyName().equals("troupes restantes")) {
					int[] troupesDispo = (int[]) evt.getNewValue();
					placementPanel.updateTroupesLabels(troupesDispo);
				}
				
				if(evt.getPropertyName().equals("level")) {
					int nbr = (int) evt.getNewValue();
					//stats.levelUp(zoneAnimationOupi.getJeuxOupi().getTroupeSelectionnee(), nbr);
				}
			}
		});
		
		
		
		//Écran de statistiques
		stats = new Stats(screenWidth, screenHeight);
		stats.setBounds((int) (6*screenWidth/11) + 80 , 350, 380, 450);
		contentPane.add(stats);
		
		//Écran d'informations
		cadreInfo = new CadreInfo(screenWidth, screenHeight);
		cadreInfo.setBounds(1120, 47 , 380, 250);
		contentPane.add(cadreInfo);
		
		// Écran de fin
		fin = new Fin(screenWidth, screenHeight);
		fin.setBounds(50, 30, (int) (screenWidth / 2), (int) (screenHeight*0.725));
		fin.setVisible(false);
		contentPane.add(fin);
		
		//Écran de début
		ecranDebut.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals("Start")) {
					ecranDebut.setVisible(false);
					zoneAnimationOupi.setVisible(true);
					zoneAnimationOupi.demarrer();
					cadreInfo.updateCadreInfo(GameManager.getInstance().getZoneAnimationOupi().getJeuxOupi().getTroupePlayer(equipeActuelle), equipeActuelle);
					
					/*
					// Centrer la caméra sur le Nexus de l'équipe 0 pour commencer la partie
			        Nexus nexusEquipe0 = zoneAnimationOupi.getJeuxOupi().getNexusEquipe(0);
			        if (nexusEquipe0 != null) {
			            zoneAnimationOupi.centrerCameraSur(nexusEquipe0);
			        }
			        */
				}
			}
		});
		
		zoneAnimationOupi.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals("Fin")) {
					fin.setVisible(true);
					zoneAnimationOupi.setVisible(false);
				}
				
				if(evt.getPropertyName().equals("troupe")) {
					stats.updateTroupe((Troupe) evt.getNewValue(), equipeActuelle);
				}
				
				// Détection du changement d'équipe
				if(evt.getPropertyName().equals("equipeActuelle")) {
					int nouvelleEquipe = (int) evt.getNewValue();
					equipeActuelle = nouvelleEquipe;
					updateBackgroundColor();
				}
				
				if(evt.getPropertyName().equals("troupes restantes")) {
					int[] troupesDispo = (int[]) evt.getNewValue();
					placementPanel.updateTroupesLabels(troupesDispo);
					}
				
				if(evt.getPropertyName().equals("combatMessages")) {
					@SuppressWarnings("unchecked")
					ArrayList<String> messages = (ArrayList<String>) evt.getNewValue();
					if (messages != null && !messages.isEmpty()) {
						StringBuilder sb = new StringBuilder();
						sb.append("----- COMBAT LOG -----\n");
						for (String message : messages) {
							sb.append(message).append("\n");
						}
						sb.append("---------------------\n\n");
						
						textAreaAttaque.append(sb.toString());
						textAreaAttaque.setCaretPosition(textAreaAttaque.getDocument().getLength());
					}
				}
			}
		});
		
		stats.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals("stat")) {
					Troupe troupeLvlUp = (Troupe) evt.getOldValue();
					String msg = "";
					StringBuilder sb = new StringBuilder();
					sb.append("----- TROUPE AMELIOREE -----\n");
					switch((int) evt.getNewValue()){
					case 0:
						troupeLvlUp.setHP(troupeLvlUp.getHP() + 25);
						sb.append("Vous avez augment\u00E9 les points de vie de "+troupeLvlUp.getClass().getSimpleName()+" de 25\n");
						sb.append("La nouvelle valeur est de "+troupeLvlUp.getHP());
						break;
					case 1:
						troupeLvlUp.setAttaque(troupeLvlUp.getAttaque()+10);
						sb.append("Vous avez augment\u00E9 l'attaque de "+troupeLvlUp.getClass().getSimpleName()+" de 10\n");
						sb.append("La nouvelle valeur est de "+troupeLvlUp.getAttaque());
						break;
					case 2:
						troupeLvlUp.setDefense(troupeLvlUp.getDefense()+10);
						sb.append("Vous avez augment\u00E9 la d\u00E9fense de "+troupeLvlUp.getClass().getSimpleName()+" de 10\n");
						sb.append("La nouvelle valeur est de "+troupeLvlUp.getDefense());
						break;
					case 3:
						troupeLvlUp.setVitesse(troupeLvlUp.getVitesse()+5);
						sb.append("Vous avez augment\u00E9 la vitesse de "+troupeLvlUp.getClass().getSimpleName()+" de 5\n");
						sb.append("La nouvelle valeur est de "+troupeLvlUp.getVitesse());
						break;
					case 4:
						troupeLvlUp.setEndurance(troupeLvlUp.getEndurance()+5);
						sb.append("Vous avez augment\u00E9 l'endurance de "+troupeLvlUp.getClass().getSimpleName()+" de 5\n");
						sb.append("La nouvelle valeur est de "+troupeLvlUp.getEndurance());
						break;
					}
					
					sb.append("\n---------------------\n\n");
					textAreaAttaque.append(sb.toString());
					
					zoneAnimationOupi.requestFocus();
				}
			}
		});

		lblEtat = new JLabel("");
		lblEtat.setHorizontalAlignment(SwingConstants.CENTER);
		lblEtat.setBounds(50, 30 + (int)(screenHeight*0.725) + 90, 400, 30);
		contentPane.add(lblEtat);
		
		// Créer et ajouter le panneau de placement
        placementPanel = new PlacementPanel(zoneAnimationOupi, actionPanel);
        placementPanel.setBounds(30, 840, 1000, 175);
        contentPane.add(placementPanel);

		textAreaAttaque = new JTextArea();
		textAreaAttaque.setBounds(1561, 30, 333, 783);
		textAreaAttaque.setEditable(false);
		textAreaAttaque.setLineWrap(true);
		textAreaAttaque.setWrapStyleWord(true);
		// Add scrolling capability to the text area
		JScrollPane scrollPane = new JScrollPane(textAreaAttaque);
		scrollPane.setBounds(1561, 30, 333, 783);
		contentPane.add(scrollPane);

		setBounds(0, 0, screenWidth, screenHeight);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
	    
	    // === 6. Final setup ===
	    pack();               // Resize frame to preferred size
	    setVisible(true);     // Show everything
        
	}
	
	/**
	 * Met à jour la couleur d'arrière-plan en fonction de l'équipe actuelle
	 */
	private void updateBackgroundColor() {
		if (equipeActuelle == 0) {
			contentPane.setBackground(COULEUR_EQUIPE_0);
		} else {
			contentPane.setBackground(COULEUR_EQUIPE_1);
		}
		contentPane.repaint();
	}
}

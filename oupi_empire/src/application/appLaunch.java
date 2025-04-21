package application;

import java.awt.*;
import javax.swing.*;
import jeu_oupi.ZoneAnimationOupi;
import troupe.Troupe;
import ecrans_jeu.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import animations.ChangePlayerAnimation;

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
	
	private Fin fin;
	
	private JButton btnStart, btnToggle, btnWin, btnLose;

	private JLabel lblEtat;
	
	private JRadioButton rdbtnOupi, rdbtnLobo, rdbtnElec, rdbtnGenial;
	
	private final ButtonGroup buttonGroupTroupe = new ButtonGroup();
	
	private JLabel lblOupi, lblElec, lblGenial, lblLobo;
	
	private JTextArea textAreaAttaque;
	
	private JLabel lblAnimChangeTurn;
	
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
	    ImageIcon backgroundIcon = new ImageIcon("res/bak/background_jeu.png"); // Remplace par ton chemin
	    JLabel backgroundLabel = new JLabel(backgroundIcon);
	    backgroundLabel.setBounds(0, 0, screenWidth, screenHeight);
	    layeredPane.add(backgroundLabel, Integer.valueOf(-1)); // tout au fond
	    
	    // === Add border image above the animation zone ===
	    JPanel borderPanel = new JPanel();
	    borderPanel.setLayout(null); // Absolute positioning
	    borderPanel.setOpaque(false); // Make the panel transparent
	    borderPanel.setBounds(-125, -120, (int) screenWidth, screenHeight ); // Same size as zoneAnimationOupi

	    // Add the border image to the panel
	    ImageIcon borderIcon = new ImageIcon("res/bak/cadre_jeu.png"); // Replace with your border image path
	    JLabel borderLabel = new JLabel(borderIcon);
	    borderLabel.setBounds(0, 0, borderPanel.getWidth(), borderPanel.getHeight()); // Fill the panel
	    borderPanel.add(borderLabel);

	    // Add the border panel to the layered pane above the animation zone
	    layeredPane.add(borderPanel, Integer.valueOf(2)); // Add above the animation zone
	    
	    // === Main content panel ===
	    contentPane = new JPanel();
	    contentPane.setLayout(null);
	    contentPane.setBounds(0, 0, screenWidth, screenHeight);
	    //contentPane.setBackground(COULEUR_EQUIPE_0); // Your team color background
	    contentPane.setOpaque(false); // Pour que l'image soit visible à travers
	    layeredPane.add(contentPane, Integer.valueOf(0));
		
		lblOupi = new JLabel("New label");
		lblOupi.setBounds(206, 936, 56, 16);
		contentPane.add(lblOupi);
		
		lblElec = new JLabel("New label");
		lblElec.setBounds(206, 984, 56, 16);
		contentPane.add(lblElec);
		
		lblGenial = new JLabel("New label");
		lblGenial.setBounds(426, 936, 56, 16);
		contentPane.add(lblGenial);
		
		lblLobo = new JLabel("New label");
		lblLobo.setBounds(426, 984, 56, 16);
		contentPane.add(lblLobo);
		
		// Add Nexus label
		JLabel lblNexus = new JLabel("1");
		lblNexus.setBounds(600, 936, 56, 16);
		contentPane.add(lblNexus);
		
		ecranDebut = new Debut(screenWidth, screenHeight);
		ecranDebut.setBounds(50, 30, (int) (screenWidth / 2), (int) (screenHeight*0.725));
		contentPane.add(ecranDebut);
		
		// === animation panel (on top) ===
	    String pathToFrames = "C:\\Users\\sacha\\git\\OupiEmpire4\\oupi_empire\\res\\png_animations\\Change_Player";
	    ChangePlayerAnimation animationPanel = new ChangePlayerAnimation(pathToFrames);
	    animationPanel.setBounds(0, 0, screenWidth, screenHeight);
	    animationPanel.setOpaque(false); // Important!
	    animationPanel.setVisible(false);
	    layeredPane.add(animationPanel, Integer.valueOf(3)); // On top of game content  // Add the animation panel to the JFrame
		
		zoneAnimationOupi = new ZoneAnimationOupi(screenWidth, screenHeight);
		zoneAnimationOupi.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals("Fin")) {
					fin.setVisible(true);
					zoneAnimationOupi.setVisible(false);
				}
				
				if(evt.getPropertyName().equals("troupe")) {
					stats.updateTroupe((Troupe) evt.getNewValue());
				}
				
				// Détection du changement d'équipe
				if(evt.getPropertyName().equals("equipeActuelle")) {
					int nouvelleEquipe = (int) evt.getNewValue();
					equipeActuelle = nouvelleEquipe;
					updateBackgroundColor();
				}
				
				if(evt.getPropertyName().equals("troupes restantes")) {
					int[] troupesDispo = (int[]) evt.getNewValue();
					lblOupi.setText(troupesDispo[0]+"");
					lblElec.setText(troupesDispo[1]+"");
					lblGenial.setText(troupesDispo[2]+"");
					lblLobo.setText(troupesDispo[3]+"");
				}
				
				if(evt.getPropertyName().equals("level")) {
					int nbr = (int) evt.getNewValue();
					stats.levelUp(zoneAnimationOupi.getJeuxOupi().getTroupeSelectionnee(), nbr);
				}
			}
		});
		zoneAnimationOupi.setBounds(50, 30, (int) (screenWidth / 2), (int) (screenHeight*0.725));
		zoneAnimationOupi.setVisible(false);
		contentPane.add(zoneAnimationOupi);
		
		stats = new Stats(screenWidth, screenHeight);
		stats.setBounds((int) (6*screenWidth/11) + 80 , 350, 380, 450);
		contentPane.add(stats);
		
		fin = new Fin(screenWidth, screenHeight);
		fin.setBounds(50, 30, (int) (screenWidth / 2), (int) (screenHeight*0.725));
		fin.setVisible(false);
		contentPane.add(fin);
		
		ecranDebut.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals("Start")) {
					ecranDebut.setVisible(false);
					zoneAnimationOupi.setVisible(true);
					zoneAnimationOupi.demarrer();
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
					stats.updateTroupe((Troupe) evt.getNewValue());
				}
				
				// Détection du changement d'équipe
				if(evt.getPropertyName().equals("equipeActuelle")) {
					int nouvelleEquipe = (int) evt.getNewValue();
					equipeActuelle = nouvelleEquipe;
					updateBackgroundColor();
				}
				
				if(evt.getPropertyName().equals("troupes restantes")) {
					int[] troupesDispo = (int[]) evt.getNewValue();
					lblOupi.setText(troupesDispo[0]+"");
					lblElec.setText(troupesDispo[1]+"");
					lblGenial.setText(troupesDispo[2]+"");
					lblLobo.setText(troupesDispo[3]+"");
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

		// Calcul de la position pour les boutons sous la zone d'animation
		int buttonY = 30 + (int)(screenHeight*0.725) + 20; // 20px de marge après la zone d'animation
		int buttonWidth = 200;
		int buttonHeight = 50;
		int buttonSpacing = 20;
		int buttonsStartX = 50; // Même X que la zone d'animation

		// Bouton Commencer partie
		btnStart = new JButton("Commencer partie");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Partie start");
				zoneAnimationOupi.demarrer();
				// Demander le focus pour l'entrée du clavier
				zoneAnimationOupi.requestFocusInWindow();
			}
		});
		btnStart.setBounds(buttonsStartX, buttonY, buttonWidth, buttonHeight);
		contentPane.add(btnStart);

		// Bouton Toggle team
		btnToggle = new JButton("Toggle team");
		btnToggle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Changement joueur");
				zoneAnimationOupi.toggleJoueur();
				// Changer la couleur de fond quand on change d'équipe
				equipeActuelle = (equipeActuelle == 0) ? 1 : 0;
				updateBackgroundColor();
				zoneAnimationOupi.requestFocusInWindow();
			}
		});
		btnToggle.setBounds(buttonsStartX + buttonWidth + buttonSpacing, buttonY, buttonWidth, buttonHeight);
		contentPane.add(btnToggle);

		// Bouton Gagner partie
		btnWin = new JButton("Gagner partie");
		btnWin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Partie win");
				String a = zoneAnimationOupi.win();
				if (a != null) {
					lblEtat.setText("Vous avez " + a);
				}
			}
		});
		btnWin.setBounds(buttonsStartX + (buttonWidth + buttonSpacing) * 2, buttonY, buttonWidth, buttonHeight);
		contentPane.add(btnWin);

		// Bouton Perdre partie
		btnLose = new JButton("Perdre partie");
		btnLose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Partie lose");
				String a = zoneAnimationOupi.lose();
				if (a != null) {
					lblEtat.setText("Vous avez " + a);
				}
			}
		});
		btnLose.setBounds(buttonsStartX + (buttonWidth + buttonSpacing) * 3, buttonY, buttonWidth, buttonHeight);
		contentPane.add(btnLose);

		lblEtat = new JLabel("");
		lblEtat.setHorizontalAlignment(SwingConstants.CENTER);
		lblEtat.setBounds(buttonsStartX, buttonY + buttonHeight + 10, buttonWidth * 2, 30);
		contentPane.add(lblEtat);
		
		JButton chboxPlacer = new JButton("Finir placer");
		chboxPlacer.setBounds(50, 939, 111, 25);
		chboxPlacer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoneAnimationOupi.finirPlacer();
			}
		});
		contentPane.add(chboxPlacer);
		
		rdbtnOupi = new JRadioButton("Oupi");
		rdbtnOupi.setBounds(270, 932, 123, 25);
		rdbtnOupi.setSelected(true);
		rdbtnOupi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoneAnimationOupi.changerType(0);
			}
		});
		buttonGroupTroupe.add(rdbtnOupi);
		contentPane.add(rdbtnOupi);
		
		rdbtnElec = new JRadioButton("Electricien");
		rdbtnElec.setBounds(270, 980, 123, 25);
		rdbtnElec.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoneAnimationOupi.changerType(1);
			}
		});
		buttonGroupTroupe.add(rdbtnElec);
		contentPane.add(rdbtnElec);
		
		rdbtnGenial = new JRadioButton("Homme genial");
		rdbtnGenial.setBounds(490, 932, 123, 25);
		rdbtnGenial.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoneAnimationOupi.changerType(2);
			}
		});
		buttonGroupTroupe.add(rdbtnGenial);
		contentPane.add(rdbtnGenial);
		
		rdbtnLobo = new JRadioButton("Lobotomisateur");
		rdbtnLobo.setBounds(490, 980, 123, 25);
		rdbtnLobo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoneAnimationOupi.changerType(3);
			}
		});
		buttonGroupTroupe.add(rdbtnLobo);
		contentPane.add(rdbtnLobo);
		
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
		
		// === 5. Button to trigger the animation ===
	    JButton changePlayerButton = new JButton("Change Player");
	    changePlayerButton.setBounds(700, 980, 150, 40);
	    contentPane.add(changePlayerButton);

	    changePlayerButton.addActionListener(e -> {
	        animationPanel.setVisible(true);
	        animationPanel.startAnimation();
	    });
	    
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

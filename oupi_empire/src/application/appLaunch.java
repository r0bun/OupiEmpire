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
		setBackground(new Color(0, 0, 0));
		// Régler sans décoration pour le mode plein écran (supprime la barre de titre)
		// setUndecorated(true);

		contentPane = new JPanel();
		contentPane.setBorder(BorderFactory.createEmptyBorder());
		contentPane.setLayout(null);
		contentPane.setBackground(COULEUR_EQUIPE_0); // Couleur initiale pour l'équipe 0
		setContentPane(contentPane);

		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setAlwaysOnTop(true);

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		DisplayMode dm = gd.getDisplayMode();
		int screenWidth = dm.getWidth();
		int screenHeight = dm.getHeight();
		
		/*minimap = new ZoneAnimationOupi(screenWidth/5, screenHeight/5);
		minimap.setBounds(1020, 331, 482, 485);
		contentPane.add(minimap);*/
		
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
		
		ecranDebut = new Debut(screenWidth, screenHeight);
		ecranDebut.setBounds(50, 30, (int) (screenWidth / 2), (int) (screenHeight*0.725));
		contentPane.add(ecranDebut);
		
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
			}
		});
		zoneAnimationOupi.setBounds(50, 30, (int) (screenWidth / 2), (int) (screenHeight*0.725));
		zoneAnimationOupi.setVisible(false);
		contentPane.add(zoneAnimationOupi);
		
		stats = new Stats(screenWidth, screenHeight);
		stats.setBounds((int) (6*screenWidth/11) , 30, (int) (screenWidth/4), (int) (screenHeight*0.725));
		contentPane.add(stats);
		
		fin = new Fin(screenWidth, screenHeight);
		fin.setBounds(50, 30, (int) (screenWidth / 2), (int) (screenHeight*0.725));
		fin.setVisible(false);
		contentPane.add(fin);

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

		setBounds(0, 0, screenWidth, screenHeight);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);

		ecranDebut.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals("Start")) {
					ecranDebut.setVisible(false);
					zoneAnimationOupi.setVisible(true);
					zoneAnimationOupi.demarrer();
				}
			}
		});
		
		
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

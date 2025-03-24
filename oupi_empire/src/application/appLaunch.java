package application;

import java.awt.*;
import javax.swing.*;
import jeu_oupi.ZoneAnimationOupi;
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
	
	private JButton btnStart, btnToggle, btnWin, btnLose;

	private JLabel lblEtat;

	
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
		
		ecranDebut = new Debut(screenWidth, screenHeight);
		ecranDebut.setBounds(50, 30, (int) (screenWidth / 2), (int) (screenHeight*0.725));
		contentPane.add(ecranDebut);
		
		zoneAnimationOupi = new ZoneAnimationOupi(screenWidth, screenHeight);
		zoneAnimationOupi.setBounds(50, 30, (int) (screenWidth / 2), (int) (screenHeight*0.725));
		zoneAnimationOupi.setVisible(false);
		contentPane.add(zoneAnimationOupi);

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
					ecranDebut.setVisible(false);
					zoneAnimationOupi.setVisible(true);
					zoneAnimationOupi.demarrer();
				}
			}
		});

		btnStart = new JButton("Commencer partie");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Partie start");
				zoneAnimationOupi.demarrer();
				// Demander le focus pour l'entrée du clavier
				zoneAnimationOupi.requestFocusInWindow();
			}
		});
		btnStart.setBounds(1049, 30, 200, 60);
		contentPane.add(btnStart);

		btnToggle = new JButton("Toggle team");
		btnToggle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Changement joueur");
				zoneAnimationOupi.toggleJoueur();
				zoneAnimationOupi.requestFocusInWindow();
			}
		});
		btnToggle.setBounds(1049, 108, 200, 60);
		contentPane.add(btnToggle);

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
		btnWin.setBounds(1049, 178, 200, 60);
		contentPane.add(btnWin);

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
		btnLose.setBounds(1049, 248, 200, 60);
		contentPane.add(btnLose);

		lblEtat = new JLabel("");
		lblEtat.setHorizontalAlignment(SwingConstants.CENTER);
		lblEtat.setBounds(1049, 318, 200, 60);
		contentPane.add(lblEtat);

		setBounds(0, 0, screenWidth, screenHeight);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);
	}
}

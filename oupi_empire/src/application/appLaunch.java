package application;

import java.awt.*;
import javax.swing.*;
import jeu_oupi.ZoneAnimationOupi;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class appLaunch extends JFrame {

    private static final long serialVersionUID = 1L;
    
    private JPanel contentPane;
    
    private ZoneAnimationOupi zoneAnimationOupi;

    /**
     * Lance l'application.
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
     * Créer le cadre.
     */
    public appLaunch() {
    	setBackground(new Color(0, 0, 0));
        // Régler sans décoration pour le mode plein écran (supprime la barre de titre)
        //setUndecorated(true);

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
        
        zoneAnimationOupi = new ZoneAnimationOupi(screenWidth, screenHeight);
        zoneAnimationOupi.setBounds(50, 30, (int) (screenWidth/2), (int)(screenWidth/2));
        contentPane.add(zoneAnimationOupi);
        
        zoneAnimationOupi.demarer();
        // Demander le focus pour l'entrée du clavier
        zoneAnimationOupi.requestFocusInWindow();
        
        setBounds(0, 0, screenWidth, screenHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }
}


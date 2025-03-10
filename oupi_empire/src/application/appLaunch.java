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
     * Launch the application.
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
     * Create the frame.
     */
    public appLaunch() {
        // Set undecorated for true fullscreen mode (removes title bar)
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
        
        

        setBounds(0, 0, screenWidth, screenHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        

    }
}


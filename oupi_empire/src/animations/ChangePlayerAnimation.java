package animations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChangePlayerAnimation extends JPanel {
    private List<ImageIcon> frames;  // List to hold the PNG frames
    private List<ImageIcon> frames2;  // List to hold the PNG frames
    private List<ImageIcon> activeFrames; // Currently active animation frames
    private JLabel animationLabel;    // Label to display the current frame
    private int currentFrame = 0;     // Track the current frame being displayed
    private Timer timer;

    public ChangePlayerAnimation() {

    	frames = loadPNGFrames("res\\png_animations\\Change_Player");
    	frames2 = loadPNGFrames("res\\png_animations\\Change_Player2");
       
    	// Set up the Panel
        setLayout(null);
        
        // Create and configure the label to show the frames
        animationLabel = new JLabel();
        animationLabel.setBounds(0, 0, 1920, 1080);  // Match the size of the window
        animationLabel.setOpaque(false);  // Ensure the label is transparent
        add(animationLabel);
    }

    public void startAnimation(int team) {
        // Select animation based on team number
        activeFrames = (team == 1) ? frames2 : frames;
        
        currentFrame = 0;

        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        timer = new Timer(1000 / 30, e -> {
            if (currentFrame >= activeFrames.size()) {
                ((Timer) e.getSource()).stop();
                setVisible(false);
                return;
            }

            animationLabel.setIcon(activeFrames.get(currentFrame));
            currentFrame++;
        });

        setVisible(true);
        timer.start();
    }
        
    private List<ImageIcon> loadPNGFrames(String folderPath) {
        List<ImageIcon> imageList = new ArrayList<>();
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".png"));

        // Sort the files to ensure they are in correct order
        if (files != null) {
            java.util.Arrays.sort(files);
            for (File file : files) {
            	
                imageList.add(new ImageIcon(file.getAbsolutePath()));
            }
        }
        return imageList;
    }
    

    public static void main(String[] args) {
        // Create the main application window
        JFrame frame = new JFrame("Change Player Animation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1920, 1080);
        frame.setLayout(null); // Allows absolute positioning

        // Create the animation panel
        ChangePlayerAnimation animationPanel = new ChangePlayerAnimation(
            
        );
        animationPanel.setBounds(0, 0, 1920, 1080); // Make sure the panel fits the frame

        // Add the panel to the frame
        frame.add(animationPanel);
        frame.setVisible(true);

        // Start the animation
        animationPanel.startAnimation(1);
    }

}

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
    private JLabel animationLabel;    // Label to display the current frame
    private int currentFrame = 0;     // Track the current frame being displayed
    private Timer timer;

    public ChangePlayerAnimation(String imageFolderPath) {

    	frames = loadPNGFrames("C:\\Users\\sacha\\git\\OupiEmpire4\\oupi_empire\\res\\png_animations\\Change_Player");
       
    	// Set up the Panel
        setLayout(null);
        
        // Create and configure the label to show the frames
        animationLabel = new JLabel();
        animationLabel.setBounds(0, 0, 1920, 1080);  // Match the size of the window
        animationLabel.setOpaque(false);  // Ensure the label is transparent
        add(animationLabel);
    }

    public void startAnimation() {
        // Reset to the first frame if the animation was already running
        currentFrame = 0;

        // Stop any existing timer
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        // Start the animation
        timer = new Timer(1000 / 30, e -> {
            if (currentFrame >= frames.size()) {
                ((Timer) e.getSource()).stop(); // Stop the animation when it finishes
                setVisible(false); // Hide the animation panel after finishing
                return;
            }

            animationLabel.setIcon(frames.get(currentFrame));  // Display the next frame
            currentFrame++;
        });

        setVisible(true);  // Ensure the panel is visible when the animation starts
        timer.start();    // Start the timer for the animation
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
            "C:\\Users\\sacha\\git\\OupiEmpire4\\oupi_empire\\res\\png_animations\\Change_Player"
        );
        animationPanel.setBounds(0, 0, 1920, 1080); // Make sure the panel fits the frame

        // Add the panel to the frame
        frame.add(animationPanel);
        frame.setVisible(true);

        // Start the animation
        animationPanel.startAnimation();
    }

}

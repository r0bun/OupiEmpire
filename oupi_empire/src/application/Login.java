package application;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Login extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private String username;
    private String password;

    public Login() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setLocationRelativeTo(null);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        DisplayMode dm = gd.getDisplayMode();
        int screenWidth = dm.getWidth();
        int screenHeight = dm.getHeight();

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(screenWidth, screenHeight));
        setContentPane(layeredPane);

        ImageIcon gifIcon = new ImageIcon(getClass().getResource("/bak/mainMenue.gif"));
        Image gifImage = gifIcon.getImage().getScaledInstance(screenWidth, screenHeight, Image.SCALE_DEFAULT);
        ImageIcon scaledGif = new ImageIcon(gifImage);

        JLabel backgroundLabel = new JLabel(scaledGif);
        backgroundLabel.setBounds(0, 0, screenWidth, screenHeight);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        contentPane = new JPanel();
        contentPane.setOpaque(false);
        contentPane.setBounds(0, 0, screenWidth, screenHeight);
        layeredPane.add(contentPane, JLayeredPane.PALETTE_LAYER);
        contentPane.setLayout(null);

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(0, 139, 139)); 
        titleLabel.setBounds(screenWidth / 2 - 100, screenHeight / 4 - 50, 200, 50);
        contentPane.add(titleLabel);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        usernameLabel.setForeground(new Color(0, 139, 139)); 
        usernameLabel.setBounds(screenWidth / 2 - 150, screenHeight / 3, 100, 30);
        contentPane.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(screenWidth / 2 - 50, screenHeight / 3, 200, 30);
        contentPane.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        passwordLabel.setForeground(new Color(0, 139, 139)); 
        passwordLabel.setBounds(screenWidth / 2 - 150, screenHeight / 3 + 50, 100, 30);
        contentPane.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(screenWidth / 2 - 50, screenHeight / 3 + 50, 200, 30);
        contentPane.add(passwordField);

        JButton confirmButton = new JButton("Confirm");
        confirmButton.setBounds(screenWidth / 2 - 50, screenHeight / 3 + 100, 100, 30);
        contentPane.add(confirmButton);

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = usernameField.getText();
                password = new String(passwordField.getPassword());
                System.out.println("Username: " + username);
                System.out.println("Password: " + password);
            }
        });
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Login frame = new Login();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

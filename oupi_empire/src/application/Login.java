package application;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;

import javax.swing.*;

import database.LoginManager;

public class Login extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private String username;
    private String password;
    
    private MainMenu pageMenu = new MainMenu();

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
        
        // === Custom Font ===
        Font customFont = null;

            try {
            	customFont = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("res/fonts/UncialAntiqua-Regular.ttf"))
            		    .deriveFont(Font.BOLD, 36f);
			} catch (FontFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            GraphicsEnvironment ge1 = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge1.registerFont(customFont);
  
        // === FORM PANEL STYLÉ ===
        JPanel formPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Couleur gris-noir translucide
                g2.setColor(new Color(30, 30, 30, 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // Bordure blanche
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);

                g2.dispose();
            }
        };
        formPanel.setOpaque(false);
        formPanel.setBounds(screenWidth / 2 - 200, screenHeight / 3 - 125, 400, 350);  // Ajustement de la hauteur du formulaire
        formPanel.setLayout(null);
        contentPane.add(formPanel);
        
        int fieldWidth = 240;
        int fieldHeight = 30;
        
        // === TITLE ===
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(customFont.deriveFont(Font.BOLD, 36f));
        titleLabel.setForeground(new Color(0, 139, 139));
        titleLabel.setBounds(0, 20, formPanel.getWidth(), 40);

        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(titleLabel);

        // === Username ===
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        usernameLabel.setForeground(new Color(0, 139, 139));
        usernameLabel.setBounds((formPanel.getWidth() - fieldWidth) / 2, 80, fieldWidth, fieldHeight);
        formPanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds((formPanel.getWidth() - fieldWidth) / 2, 110, fieldWidth, fieldHeight); // Centré avec la largeur du panel
        formPanel.add(usernameField);

        // === Password ===
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        passwordLabel.setForeground(new Color(0, 139, 139));
        passwordLabel.setBounds((formPanel.getWidth() - fieldWidth) / 2, 160, fieldWidth, fieldHeight);  // Aligné à gauche du champ password
        formPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds((formPanel.getWidth() - fieldWidth) / 2, 190, fieldWidth, fieldHeight); // Centré avec la largeur du panel
        formPanel.add(passwordField);

        // === Confirm Button ===
        JButton confirmButton = new JButton("Connexion");
        confirmButton.setFont(customFont.deriveFont(Font.PLAIN, 18f));
        confirmButton.setBounds((formPanel.getWidth() - fieldWidth) / 2, 250, fieldWidth, fieldHeight + 10);
        formPanel.add(confirmButton);
        
        // === Create account Button ===
        JLabel linkLabel = new JLabel("Créer un compte");
        linkLabel.setFont(customFont.deriveFont(Font.PLAIN, 14f));
        linkLabel.setForeground(new Color(0, 139, 139)); 
        linkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkLabel.setHorizontalAlignment(SwingConstants.CENTER);
        linkLabel.setBounds(0, 300, formPanel.getWidth(), 30);
        formPanel.add(linkLabel);
        
        
        linkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("http://127.0.0.1:3000/index.html"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                linkLabel.setText("<html><u>Créer un compte</u></html>"); // effet au survol
            }

            @Override
            public void mouseExited(MouseEvent e) {
                linkLabel.setText("Créer un compte"); // on enlève le soulignement
            }
        });

        
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = usernameField.getText();
                password = new String(passwordField.getPassword());
                System.out.println("Username: " + username);
                System.out.println("Password: " + password);
                
                LoginManager login = new LoginManager();
                Boolean userValid = login.verifiyLogin(username, password);
                
                if(userValid) {
                System.out.println("Bouton 'Jouer' cliqué !");
                System.out.println("Utilisateur valide !");
                pageMenu.setVisible(true);
                dispose();
                } else {
                System.out.println("Bouton 'Jouer' cliqué !");
                System.out.println("Utilisateur invalide!");
                }
                
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

package application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;

public class TutorialIntro extends JFrame {
	private JPanel contentPane;
	private JLabel characterIcon;
    private JTextArea dialogText;
    private Timer typingTimer;
    private JButton nextButton;
    private String fullText = "Salutations à toi jeune padawan! Bienvenue à Lakaka Land!";
    private String[] dialogLines = {
    		"Salutations à toi jeune padawan! Bienvenue à Lakaka Land!",
    	    "Je me présente, je suis Oupi Goupi, ancien empreur de Lakaka Land. Il n'y a de cela bien longtemps, je reignais sur ces terres"
    	    + " paisiblement, et mes loyaux sujets étaient heureux. Mais, comme toute bonne chose dans la vie, cette quiétude ne pouvait perdurer...",
    	    "Il y maintenant quelques semaines, je fus victime d'un vicieux coup d'état! L'homme génial et son acolyte l'électricien m'usurpèrent le pouvoir, et règne depuis sur"
    	    + " Lakaka Land en Tyrans.",
    	    "Tu t'en douteras bien, c'est ici que tu intervient jeune padawan! Je m'apprête à partir à la reconquête de mon royaume avec mon armée de loyalistes."
    	    + " Cependant, je ne peux pas être sur tous les fronts, et j'aurai besoin de ton expertise en stratégie militaire pour mener a bien ma campagne! "
    	    + "Laisse moi te faire visiter les installations."
    	};
    private int currentLineIndex = 0;
    private int charIndex = 0;
    
    public TutorialIntro() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setLocationRelativeTo(null);
        setUndecorated(true);
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        DisplayMode dm = gd.getDisplayMode();
        int screenWidth = dm.getWidth();
        int screenHeight = dm.getHeight();
        
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(screenWidth, screenHeight));
        setContentPane(layeredPane);
        
        //=== Background Pane ===
        ImageIcon gifIcon = new ImageIcon(getClass().getResource("/bak/gif_prairie.gif"));
        Image gifImage = gifIcon.getImage().getScaledInstance(screenWidth, screenHeight, Image.SCALE_DEFAULT);
        ImageIcon scaledGif = new ImageIcon(gifImage);

        JLabel backgroundLabel = new JLabel(scaledGif);
        backgroundLabel.setBounds(0, 0, screenWidth, screenHeight);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);
        
        //=== Content pane ===
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
        
        //=== Panel Narrator ===
        JPanel narratorPanel = new JPanel() {
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
        narratorPanel.setOpaque(false);
        narratorPanel.setBounds(20, 20, screenWidth - 40, screenHeight/5 - 20);  // Ajustement de la hauteur du formulaire
        narratorPanel.setLayout(null); // un peu de padding horizontal
        contentPane.add(narratorPanel);

        // Character icon
        characterIcon = new JLabel(new ImageIcon("res/troupes/oupi.jpg")); // Only works if run from project root
        characterIcon.setBounds(20, 20, 120, 120); // (x, y, largeur, hauteur)
        narratorPanel.add(characterIcon);

        // Dialog text area
        dialogText = new JTextArea();
        dialogText.setBounds(160, 20, narratorPanel.getWidth() - 180, 120);
        dialogText.setFont(customFont.deriveFont(Font.PLAIN, 26f));
        dialogText.setOpaque(false); // <-- Rend le JTextArea transparent
        dialogText.setForeground(Color.WHITE); // Pour que le texte soit visible sur fond sombre
        dialogText.setEditable(false);
        dialogText.setLineWrap(true);
        dialogText.setWrapStyleWord(true);
        narratorPanel.add(dialogText);
        
        //=== Boutton Next Dialogue ===
        nextButton = new JButton("Next");
        nextButton.setFont(customFont.deriveFont(20f)); // Si tu veux appliquer ta police
        nextButton.setFocusPainted(false);
        nextButton.setBounds(narratorPanel.getWidth() - 150, narratorPanel.getHeight() - 60, 120, 40); // Ajuste la position si nécessaire
        narratorPanel.add(nextButton);
        
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (typingTimer.isRunning()) {
                    // Si le texte est encore en train d'être tapé → compléter instantanément
                    typingTimer.stop();
                    dialogText.setText(dialogLines[currentLineIndex]);
                } else {
                    // Aller à la ligne suivante
                    currentLineIndex++;
                    if (currentLineIndex < dialogLines.length) {
                        startTypingEffect(dialogLines[currentLineIndex]);
                    } else {
                        // Fin du dialogue
                        nextButton.setEnabled(false);
                        
                    }
                }
            }
        });

        // Start typing effect
        startTypingEffect(dialogLines[currentLineIndex]);

        setVisible(true);
    }

    private void startTypingEffect(String text) {
        fullText = text;
        charIndex = 0;
        dialogText.setText("");
        nextButton.setEnabled(false); // désactive pendant l’écriture

        typingTimer = new Timer(25, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (charIndex < fullText.length()) {
                    dialogText.append(String.valueOf(fullText.charAt(charIndex)));
                    charIndex++;
                } else {
                    typingTimer.stop();
                    nextButton.setEnabled(true); // réactive le bouton quand c’est fini
                }
            }
        });
        typingTimer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TutorialIntro());
    }
}


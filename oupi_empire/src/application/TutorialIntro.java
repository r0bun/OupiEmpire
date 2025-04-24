package application;

import javax.swing.*;

import animations.ChangePlayerAnimation;
import animations.EndGameAnimation;
import jeu_oupi.GameManager;

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
    private String[] dialogLinesWithImages = {
            "Voici le plateau de jeu, c'est ici que tu donneras des ordres à tes troupes. L'action se déroule ici de A à Z. Ton objectif, anéentir l'armée ennemie et détruire le nexus.",
            "Voici ton centre de commande. Il te permet de contrôler tes unités. Tu pourras d'abord les déployer lors de ton premier tour de jeu, et ensuite tu pourras les déplacer et les faire attaquer."
            + "La distance d'attaque d'une troupe varie en fonction de sa statistique de portée, que tu pourras apercevoir sur sa carte.",
            "Dans cette section, tu retrouveras les informations sur ton armée en entier. Ton nombre d'unités en vie, ainsi que leur points de vie, prend garde à la vie de ton nexus!",
            "Dans cette section maintenant, tu retrouveras la carte de l'unité que tu as sélectionner. Son nom se trouve dans le coin droit en haut, et ses statistiques dans la colonne gauche.",
            "Finalment voici les logs de combat. Ils te permettent de suivre le déroulement des affrontements, en te donnant des informations importantes comme le nombre de dégats entre autres choses.",
            "Voilà qui fait le tour des choses, je compte sur toi jeune padawan, rend moi fier et libère mon royaume, et tu seras couvert d'or!"
        };
    private String[] imagePaths = {
        "res/bak/tuto_plateauJeu.png",
        "res/bak/tuto_controlPanel.png",
        "res/bak/tuto_cadreInfo.png",
        "res/bak/tuto_cadrePK.png",
        "res/bak/tuto_combat_logs.png"
    };
    private int currentLineIndex = 0;
    private int charIndex = 0;
    private int currentImageIndex = -1;
    private JLabel tutorialImageLabel;
    private JPanel narratorPanel;
    private appLaunch afficherFrame;

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

        // Background Pane
        ImageIcon gifIcon = new ImageIcon(getClass().getResource("/bak/gif_prairie.gif"));
        Image gifImage = gifIcon.getImage().getScaledInstance(screenWidth, screenHeight, Image.SCALE_DEFAULT);
        ImageIcon scaledGif = new ImageIcon(gifImage);

        JLabel backgroundLabel = new JLabel(scaledGif);
        backgroundLabel.setBounds(0, 0, screenWidth, screenHeight);
        layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

        // Content pane
        contentPane = new JPanel();
        contentPane.setOpaque(false);
        contentPane.setBounds(0, 0, screenWidth, screenHeight);
        layeredPane.add(contentPane, JLayeredPane.PALETTE_LAYER);
        contentPane.setLayout(null);

        // Custom Font
        Font customFont = null;
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("res/fonts/UncialAntiqua-Regular.ttf"))
                .deriveFont(Font.BOLD, 36f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(customFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            customFont = new Font("Arial", Font.BOLD, 36); // Fallback font
        }

        // Panel Narrator
        narratorPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 30, 30, 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
            }
        };
        narratorPanel.setOpaque(false);
        narratorPanel.setBounds(20, 20, screenWidth - 40, screenHeight/5 - 20);
        narratorPanel.setLayout(null);
        contentPane.add(narratorPanel);

        // Character icon
        characterIcon = new JLabel(new ImageIcon("res/troupes/oupi.jpg"));
        characterIcon.setBounds(20, 20, 120, 120);
        narratorPanel.add(characterIcon);

        // Dialog text area
        dialogText = new JTextArea();
        dialogText.setBounds(160, 20, narratorPanel.getWidth() - 180, 120);
        dialogText.setFont(customFont.deriveFont(Font.PLAIN, 26f));
        dialogText.setOpaque(false);
        dialogText.setForeground(Color.WHITE);
        dialogText.setEditable(false);
        dialogText.setLineWrap(true);
        dialogText.setWrapStyleWord(true);
        narratorPanel.add(dialogText);

        // Next Button
        nextButton = new JButton("Next");
        nextButton.setFont(customFont.deriveFont(20f));
        nextButton.setFocusPainted(false);
        nextButton.setBounds(narratorPanel.getWidth() - 150, narratorPanel.getHeight() - 60, 120, 40);
        narratorPanel.add(nextButton);

        // Tutorial Image Label
        tutorialImageLabel = new JLabel();
        tutorialImageLabel.setBounds(0, 0, screenWidth, screenHeight);
        tutorialImageLabel.setVisible(false);
        contentPane.add(tutorialImageLabel);

     // Modifier l'ActionListener du nextButton
        nextButton.addActionListener(e -> {
            if (currentImageIndex == -1) {
                if (typingTimer != null && typingTimer.isRunning()) {
                    typingTimer.stop();
                    dialogText.setText(dialogLines[currentLineIndex]);
                } else {
                    currentLineIndex++;
                    if (currentLineIndex < dialogLines.length) {
                        startTypingEffect(dialogLines[currentLineIndex]);
                    } else {
                        // Début de l'affichage des images avec nouveaux dialogues
                        currentImageIndex = 0;
                        showNextImage();
                        startTypingEffect(dialogLinesWithImages[currentImageIndex]);
                    }
                }
            } else {
                currentImageIndex++;
                if (currentImageIndex < imagePaths.length) {
                    showNextImage();
                    startTypingEffect(dialogLinesWithImages[currentImageIndex]);
                } else {
                	LoadingScreen loading = new LoadingScreen();
                    loading.startLoading(
                        () -> {
                            // Load game assets, levels, sprites
                        	new ChangePlayerAnimation();
                            new EndGameAnimation("res/png_animations/End_Game");
                            
                            GameManager.getInstance().startNewGame(1920, 1080);
                        },
                        () -> {
                        	// When loading is done, show the game window
                            afficherFrame = GameManager.getInstance().getGameWindow();
                            afficherFrame.setVisible(true);
                        }
                    );
                    dispose();
                }
            }
        });

        // Start first dialog
        startTypingEffect(dialogLines[0]);

        setVisible(true);
    }

    private void startTypingEffect(String text) {
        fullText = text;
        charIndex = 0;
        dialogText.setText("");
        nextButton.setEnabled(false);

        typingTimer = new Timer(25, e -> {
            if (charIndex < fullText.length()) {
                dialogText.append(String.valueOf(fullText.charAt(charIndex)));
                charIndex++;
            } else {
                typingTimer.stop();
                nextButton.setEnabled(true);
            }
        });
        typingTimer.start();
    }

    private void showNextImage() {
        try {
            ImageIcon imageIcon = new ImageIcon(imagePaths[currentImageIndex]);
            Image image = imageIcon.getImage();
            
            int originalWidth = imageIcon.getIconWidth();
            int originalHeight = imageIcon.getIconHeight();
            double ratio = Math.min(
                (double) tutorialImageLabel.getWidth() / originalWidth,
                (double) tutorialImageLabel.getHeight() / originalHeight
            );
            
            int newWidth = (int) (originalWidth * ratio);
            int newHeight = (int) (originalHeight * ratio);
            
            Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            tutorialImageLabel.setIcon(new ImageIcon(scaledImage));
            tutorialImageLabel.setVisible(true);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + imagePaths[currentImageIndex]);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new TutorialIntro();
            } catch (Exception e) {
                System.err.println("Erreur au lancement: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
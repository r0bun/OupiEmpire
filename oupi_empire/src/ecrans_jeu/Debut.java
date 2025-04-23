package ecrans_jeu;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Ecran de debut de jeu, pour empecher l'interraction avec le jeu avant le commencement
 * 
 * @author Loic Simard
 */
public class Debut extends JPanel {

	private int screenWidth, screenHeight;
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private Image backgroundImage;
	private JLabel startLabel;
	private Font customFont;

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Constructeur de l'ecran affiche avant la partie
	 * 
	 * @param screenWidth La largeur de l'ecran
	 * @param screenHeight La hauteur de l'ecran
	 */
	public Debut(int screenWidth, int screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		setLayout(null); // Pour positionner le label manuellement
		
		backgroundImage = scaleBackground("res/bak/Debut_bk.png", 980, 800);
		
		// === Custom Font ===
        customFont = null;

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
            
            //customFont.deriveFont(Font.PLAIN, 40f);
		
		// Créer et styliser le label
        startLabel = new JLabel("Cliquez pour commencer la partie", SwingConstants.CENTER);
        startLabel.setFont(customFont.deriveFont(Font.PLAIN, 50f));
        startLabel.setForeground(Color.WHITE);
        startLabel.setOpaque(true);
        startLabel.setBackground(new Color(40, 40, 40, 200));
        startLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Dimensionner et positionner le label
        startLabel.setBounds(100, 400, 600, 100);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				pcs.firePropertyChange("Start", 0, -1);
			}
		});
		
	}
	
	/**
	 * Dessine le panneau.
	 * 
	 * @param g l'objet {@link Graphics} utilisé pour dessiner
	 */
	@Override
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Graphics2D g2d = (Graphics2D) g;
	    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	    // L'image de fond
	    if (backgroundImage != null) {
	        g2d.drawImage(backgroundImage, 0, 0, this);
	    }

	    // Configuration du texte
	    String text = "Cliquez pour commencer la partie";
	    Font font = customFont.deriveFont(Font.PLAIN, 40f);
	    g2d.setFont(font);
	    FontMetrics metrics = g2d.getFontMetrics(font);
	    int textWidth = metrics.stringWidth(text);
	    int textHeight = metrics.getHeight();

	    // Position du texte - centré horizontalement dans l'image de 980px
	    int textX = (980 - textWidth) / 2;
	    int textY = 500; // ~60% de 800px

	    // Fond arrondi avec padding
	    int padding = 20;
	    int arcSize = 15;
	    g2d.setColor(new Color(40, 40, 40, 200));
	    g2d.fillRoundRect(textX - padding, 
	                      textY - textHeight + metrics.getDescent() - padding,
	                      textWidth + (padding * 2), 
	                      textHeight + (padding * 2),
	                      arcSize, arcSize);

	    // Texte centré
	    g2d.setColor(Color.WHITE);
	    g2d.drawString(text, textX, textY);
	}
	
    // Méthode pour charger et redimensionner une image depuis un chemin
    public Image scaleBackground(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image image = icon.getImage();
        return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
}

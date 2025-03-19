package styles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {

    private static final long serialVersionUID = 1L;
    private int radius;
    private Color defaultColor = new Color(50, 50, 50, 180);  // Couleur par défaut
    private Color hoverColor = new Color(70, 70, 70, 200);    // Couleur au survol
    private Color clickColor = new Color(30, 30, 30, 220);    // Couleur au clic
    private Color currentColor; // Stocke la couleur actuelle du bouton

    public RoundedButton(String text, int radius) {
        super(text);
        this.radius = radius;
        this.currentColor = defaultColor; // Initialiser la couleur

        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(new Font("Arial", Font.BOLD, 18));
        setForeground(Color.WHITE);
        setBackground(defaultColor); // Assigner la couleur de fond initiale

        // Gérer l'effet hover et clic
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                currentColor = hoverColor;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                currentColor = defaultColor;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                currentColor = clickColor;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                currentColor = hoverColor;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dessiner le fond arrondi du bouton avec la bonne couleur
        g2.setColor(currentColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dessiner la bordure blanche arrondie
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

        g2.dispose();
    }
}

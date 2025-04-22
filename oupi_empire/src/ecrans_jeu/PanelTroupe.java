package ecrans_jeu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import troupe.Troupe;

class PanelTroupe extends JPanel {
    private final Troupe troupe;
    private final ImageIcon icon;

    public PanelTroupe(Troupe troupe, ImageIcon icon) {
        this.troupe = troupe;
        this.icon = icon;
        setLayout(null);
        setPreferredSize(new Dimension(50, 50));
        setBackground(new Color(222, 184, 135)); // brun pâle
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Dessiner la barre de vie
        int hpMax = troupe.getHPMax();
        int hp = troupe.getHP();
        float ratio = (float) hp / hpMax;

        int barreWidth = 50;
        int barreHeight = 4;
        int yBarre = 2;

        g2.setColor(Color.RED);
        g2.fillRect(0, yBarre, barreWidth, barreHeight);

        g2.setColor(Color.GREEN);
        g2.fillRect(0, yBarre, (int) (barreWidth * ratio), barreHeight);

        g2.setColor(Color.BLACK);
        g2.drawRect(0, yBarre, barreWidth, barreHeight);

        // Dessiner l'image
        icon.paintIcon(this, g, 1, 10);
    }
}

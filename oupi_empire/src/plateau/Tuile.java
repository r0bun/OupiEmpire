package plateau;

import java.awt.Color;
import java.awt.Graphics2D;

public class Tuile {
    private int x, y; // Position de la tuile sur le plateau
    private int taille; // Taille de la tuile
    private boolean occupee; // Indique si la tuile est occupée
    private Color couleur; // Couleur de la tuile

    public Tuile(int x, int y, int taille, Color couleur) {
        this.x = x;
        this.y = y;
        this.taille = taille;
        this.couleur = couleur;
        this.occupee = false;
    }

    public void dessiner(Graphics2D g2d) {
        Graphics2D g2dPrive = (Graphics2D) g2d.create();
        g2dPrive.setColor(couleur);
        g2dPrive.fillRect(x, y, taille, taille);
        g2dPrive.setColor(Color.BLACK); // Contour de la tuile
        g2dPrive.drawRect(x, y, taille, taille);
    }

    public boolean estOccupee() {
        return occupee;
    }

    public void setOccupee(boolean occupee) {
        this.occupee = occupee;
    }

    public void setCouleur(Color couleur) {
        this.couleur = couleur;
    }
}



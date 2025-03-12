package plateau;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * La classe {@code Tuile} représente une tuile sur le plateau de jeu.
 * Elle contient des informations sur la position, la taille, l'état d'occupation et la couleur de la tuile.
 * 
 * @author Badr Rifki
 */
public class Tuile {
    private int x, y; // Position de la tuile sur le plateau
    private int taille; // Taille de la tuile
    private boolean occupee; // Indique si la tuile est occupée
    private Color couleur; // Couleur de la tuile

    /**
     * Constructeur de la classe {@code Tuile}.
     * 
     * @param x la position x de la tuile
     * @param y la position y de la tuile
     * @param taille la taille de la tuile
     * @param couleur la couleur de la tuile
     */
    public Tuile(int x, int y, int taille, Color couleur) {
        this.x = x;
        this.y = y;
        this.taille = taille;
        this.couleur = couleur;
        this.occupee = false;
    }

    /**
     * Dessine la tuile.
     * 
     * @param g2d l'objet {@link Graphics2D} utilisé pour dessiner
     */
    public void dessiner(Graphics2D g2d) {
        Graphics2D g2dPrive = (Graphics2D) g2d.create();
        g2dPrive.setColor(couleur);
        g2dPrive.fillRect(x, y, taille, taille);
        g2dPrive.setColor(Color.BLACK); // Contour de la tuile
        g2dPrive.drawRect(x, y, taille, taille);
    }

    /**
     * Vérifie si la tuile est occupée.
     * 
     * @return {@code true} si la tuile est occupée, {@code false} sinon
     */
    public boolean estOccupee() {
        return occupee;
    }

    /**
     * Définit l'état d'occupation de la tuile.
     * 
     * @param occupee l'état d'occupation de la tuile
     */
    public void setOccupee(boolean occupee) {
        this.occupee = occupee;
    }

    /**
     * Définit la couleur de la tuile.
     * 
     * @param couleur la nouvelle couleur de la tuile
     */
    public void setCouleur(Color couleur) {
        this.couleur = couleur;
    }
}

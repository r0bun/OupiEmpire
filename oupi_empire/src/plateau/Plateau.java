package plateau;

import java.awt.Color;
import java.awt.Graphics2D;

import interfaces.Dessinable;

/**
 * La classe {@code Plateau} représente le plateau de jeu composé de tuiles.
 * Elle implémente l'interface {@link Dessinable} pour permettre le dessin du plateau.
 *
 *@author Badr Rifki
 */
public class Plateau implements Dessinable {
    private int lignes, colonnes;
    private int tailleTuile;
    private Tuile[][] tuiles; 

    /**
     * Constructeur de la classe {@code Plateau}.
     * 
     * @param lignes le nombre de lignes du plateau
     * @param colonnes le nombre de colonnes du plateau
     * @param tailleTuile la taille de chaque tuile
     */
    public Plateau(int lignes, int colonnes, int tailleTuile) {
        this.lignes = lignes;
        this.colonnes = colonnes;
        this.tailleTuile = tailleTuile;
        initialiserTuiles();
    }

    /**
     * Initialise les tuiles du plateau avec des couleurs alternées pour un effet damier.
     */
    private void initialiserTuiles() {
        tuiles = new Tuile[lignes][colonnes];
        for (int ligne = 0; ligne < lignes; ligne++) {
            for (int colonne = 0; colonne < colonnes; colonne++) {
                // Alterner les couleurs pour un effet damier
                Color couleur = (ligne + colonne) % 2 == 0 ? Color.LIGHT_GRAY : Color.DARK_GRAY;
                tuiles[ligne][colonne] = new Tuile(colonne * tailleTuile, ligne * tailleTuile, tailleTuile, couleur);
            }
        }
    }

    /**
     * Dessine le plateau de jeu.
     * 
     * @param g2d l'objet {@link Graphics2D} utilisé pour dessiner
     */
    @Override
    public void dessiner(Graphics2D g2d) {
        Graphics2D g2dPrive = (Graphics2D) g2d.create();
        for (int ligne = 0; ligne < lignes; ligne++) {
            for (int colonne = 0; colonne < colonnes; colonne++) {
                tuiles[ligne][colonne].dessiner(g2dPrive);
            }
        }
    }

    /**
     * Retourne la tuile à la position spécifiée.
     * 
     * @param ligne la ligne de la tuile
     * @param colonne la colonne de la tuile
     * @return la tuile à la position spécifiée
     */
    public Tuile getTuile(int ligne, int colonne) {
        return tuiles[ligne][colonne];
    }
    
}


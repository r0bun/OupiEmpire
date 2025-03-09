package plateau;

import java.awt.Color;
import java.awt.Graphics2D;

public class Plateau {
    private int lignes, colonnes;
    private int tailleTuile;
    private Tuile[][] tuiles; 

    public Plateau(int lignes, int colonnes, int tailleTuile) {
        this.lignes = lignes;
        this.colonnes = colonnes;
        this.tailleTuile = tailleTuile;
        initialiserTuiles();
    }

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

    public void dessiner(Graphics2D g2d) {
        for (int ligne = 0; ligne < lignes; ligne++) {
            for (int colonne = 0; colonne < colonnes; colonne++) {
                tuiles[ligne][colonne].dessiner(g2d);
            }
        }
    }

    public Tuile getTuile(int ligne, int colonne) {
        return tuiles[ligne][colonne];
    }
}


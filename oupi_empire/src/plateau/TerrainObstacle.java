package plateau;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TerrainObstacle {

    protected BufferedImage texture;
    protected int x, y; // Position de la tuile sur le plateau
    protected int taille; // Taille de la tuile
    protected int lig, col; // Position de la tuile sur le plateau
    protected String nomTuile; // Utilisé pour trouver le fichier image
    protected final String PATH_TUILE = "/tuilesTexture/";

    public TerrainObstacle(int x, int y, int taille, int lig, int col) {
        this.x = x;
        this.y = y;
        this.taille = taille;
        this.lig = lig;
        this.col = col;

    }

    public void dessiner(Graphics2D g2dPrive, int x, int y, int taille) {
        g2dPrive.drawImage(texture, x, y, taille, taille, null);
    }

    public void setNomTuile(String nomTuile) {
        this.nomTuile = nomTuile;
    }

    protected void setTexture(String imagePath) {
        // Utilisation de TextureManager pour charger la texture
        texture = TextureManager.getInstance().getTexture(imagePath);
    }
}

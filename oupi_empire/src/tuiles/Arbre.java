package tuiles;

import java.awt.Color;
import java.awt.Graphics2D;

import plateau.TerrainObstacle;
import plateau.Tuile;

public class Arbre extends TerrainObstacle {

	public Arbre(int x, int y, int taille, int lig, int col) {
		super(x, y, taille, lig, col);
		nomTuile = "arbre";
		String texturePath = PATH_TUILE + nomTuile + ".png";
		setTexture(texturePath);
	}
}
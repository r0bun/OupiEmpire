package tuiles;

import java.awt.Color;
import java.awt.Graphics2D;

import plateau.TerrainObstacle;
import plateau.Tuile;

public class Buisson extends TerrainObstacle {

	public Buisson(int x, int y, int taille, int lig, int col) {
		super(x, y, taille, lig, col);
		nomTuile = "buisson";
		String texturePath = PATH_TUILE + nomTuile + ".png";
		setTexture(texturePath);
	}
}
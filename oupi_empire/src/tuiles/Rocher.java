package tuiles;

import java.awt.Color;
import java.awt.Graphics2D;

import plateau.TerrainObstacle;
import plateau.Tuile;

public class Rocher extends TerrainObstacle {

	public Rocher(int x, int y, int taille, int lig, int col) {
		super(x, y, taille, lig, col);
		nomTuile = "rocher";
		String texturePath = PATH_TUILE + nomTuile + ".png";
		setTexture(texturePath);
	}
}
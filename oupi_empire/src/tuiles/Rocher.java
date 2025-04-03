package tuiles;

import java.awt.Color;

import plateau.TerrainObstacle;
import plateau.Tuile;

public class Rocher extends TerrainObstacle{

	public Rocher(int x, int y, int taille,int lig,int col) {
		super(x, y, taille,lig,col);
		nomTuile = "rocher";
		setTexture(PATH_TUILE + nomTuile + ".png");
	}

}

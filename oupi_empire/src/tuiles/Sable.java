package tuiles;

import java.awt.Color;
import java.awt.image.BufferedImage;
import plateau.Tuile;

public class Sable extends Tuile{

	public Sable(int x, int y, int taille, Color couleur,int lig,int col) {
		super(x, y, taille, couleur,lig,col);
		nomTuile = "sable";
		setTexture(PATH_TUILE + nomTuile + ".png");
	}

}

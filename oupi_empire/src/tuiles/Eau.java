package tuiles;

import java.awt.Color;

import plateau.Tuile;

public class Eau extends Tuile{

	public Eau(int x, int y, int taille, Color couleur,int lig,int col) {
		super(x, y, taille, couleur,lig,col);
		nomTuile = "eau";
		setTexture(PATH_TUILE + nomTuile + ".png");
	}

}

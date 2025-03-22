package tuiles;

import java.awt.Color;

import plateau.Tuile;

public class Gravier extends Tuile{

	public Gravier(int x, int y, int taille, Color couleur,int lig,int col) {
		super(x, y, taille, couleur,lig,col);
		nomTuile = "gravier";
		setTexture(PATH_TUILE + nomTuile + ".png");
	}

}

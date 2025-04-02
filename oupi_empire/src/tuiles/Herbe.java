package tuiles;

import java.awt.Color;

import plateau.Tuile;

public class Herbe extends Tuile{

	public Herbe(int x, int y, int taille, Color couleur, int lig, int col) {
		super(x, y, taille, couleur, lig, col);
		nomTuile = "herbe";
		setTexture(PATH_TUILE + nomTuile + ".png");
	}
}

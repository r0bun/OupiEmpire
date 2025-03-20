package plateau.tuiles;

import java.awt.Color;

import plateau.Tuile;

public class Eau extends Tuile{

	public Eau(int x, int y, int taille, Color couleur,int lig,int col) {
		super(x, y, taille, couleur,lig,col);
		setTexture("/tuiles/eau.png");
	}

}

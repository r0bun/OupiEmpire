package plateau.tuiles;

import java.awt.Color;

import plateau.Tuile;

public class Eau extends Tuile{

	public Eau(int x, int y, int taille, Color couleur) {
		super(x, y, taille, couleur);
		setTexture("/tuiles/eau.png");
	}

}

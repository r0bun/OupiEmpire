package plateau.tuiles;

import java.awt.Color;
import java.awt.image.BufferedImage;

import plateau.Tuile;

public class Sable extends Tuile{

	public Sable(int x, int y, int taille, Color couleur) {
		super(x, y, taille, couleur);
		setTexture("/tuiles/sable.png");
	}

}

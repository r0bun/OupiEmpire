package jeu_oupi;

import java.awt.Graphics2D;

import gestion_couleur.Couleur;
import interfaces.Dessinable;
import plateau.Plateau;

public class JeuxOupi implements Dessinable {

	private Plateau plateau;
	private double largeurPlat = 400;
	
	/** la position de la couleur du plateau */
	private int couleurPlateau = 4;
	/** la position de la couleur des bords du plateau */
	private int couleurBordPlateau =7; 
	
	private double screenWidth;
	private double screenHeight;
	
	public JeuxOupi(int screenWidth, int screenHeight) {
	    this.screenWidth = screenWidth;
	    this.screenHeight = screenHeight;
		
		plateau = new Plateau(new Couleur (couleurPlateau),new Couleur (couleurBordPlateau), 0, 0, screenWidth/1.5,screenWidth/1.5 );
	}
	
	@Override
	public void dessiner(Graphics2D g2d) {
		
		plateau.dessiner(g2d);
		
	}

	

}

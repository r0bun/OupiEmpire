package jeu_oupi;

import java.awt.Graphics2D;
import java.util.ArrayList;

import gestion_couleur.Couleur;
import interfaces.Dessinable;
import plateau.Plateau;
import troupe.Oupi;
import troupe.Troupe;

public class JeuxOupi implements Dessinable {
																		//INFO GEN
	
	private int screenWidth;
	//private int screenHeight;

																		//PLATEAU
	private Plateau plateau;
	private static int nbTuiles = 8;
	public static int tailleTuile;
	
																		//TROUPES
	private ArrayList<Troupe> troupes = new ArrayList<>(); // liste pour annuler une decision faite par un joueur
	private ArrayList<Troupe> simTroupes = new ArrayList<>();
	private Troupe troupeSelectionnee = null;
	
	
	public JeuxOupi(int screenWidth, int screenHeight) {
	    this.screenWidth = screenWidth;
	    //this.screenHeight = screenHeight;
	    
	    creeTailleTuile();
	    setTroupes();
	    copyTroupes(troupes, simTroupes);
		
	    plateau = new Plateau(nbTuiles, nbTuiles, tailleTuile);
	}
	
	public void setTroupes() {
		
		troupes.add(new Oupi(0,0));
	}
	
	private void copyTroupes(ArrayList<Troupe> source, ArrayList<Troupe> dest) {
		
		dest.clear();
		for(int i=0; i<source.size(); i++) {
			dest.add(source.get(i));
		}
	}
	
	@Override
	public void dessiner(Graphics2D g2d) {
		
		Graphics2D g2dPrive = (Graphics2D) g2d.create();
		plateau.dessiner(g2dPrive);
		
		for(Troupe p : simTroupes) {
			p.dessiner(g2dPrive);
		}
	}

	public Plateau getPlateau() {
		return plateau;
	}
	
	public void creeTailleTuile() {
		//taille = taille du composant c-a-d screenWidth/2 diviser par le nombre de tuilles
		tailleTuile = (screenWidth/2)/nbTuiles;
	}
	
	public static int getNbTuiles() {
		return nbTuiles;
	}
	
	public Troupe getTroupeA(int x, int y) {
		for (Troupe troupe : simTroupes) {
			if (troupe.estA(x, y)) {
				return troupe;
			}
		}
		return null;
	}
	
	public void selectionnerTroupe(Troupe troupe) {
		// Désélectionner la troupe précédemment sélectionnée
		if (troupeSelectionnee != null) {
			troupeSelectionnee.setSelectionne(false);
		}
		
		// Sélectionner la nouvelle troupe
		troupeSelectionnee = troupe;
		if (troupeSelectionnee != null) {
			troupeSelectionnee.setSelectionne(true);
		}
	}
	
	public Troupe getTroupeSelectionnee() {
		return troupeSelectionnee;
	}
	
	public void deplacerTroupeSelectionneeHaut() {
		if (troupeSelectionnee != null) {
			troupeSelectionnee.deplacerHaut();
		}
	}
	
	public void deplacerTroupeSelectionneeBas() {
		if (troupeSelectionnee != null) {
			troupeSelectionnee.deplacerBas();
		}
	}
	
	public void deplacerTroupeSelectionneeGauche() {
		if (troupeSelectionnee != null) {
			troupeSelectionnee.deplacerGauche();
		}
	}
	
	public void deplacerTroupeSelectionneeDroite() {
		if (troupeSelectionnee != null) {
			troupeSelectionnee.deplacerDroite();
		}
	}
}

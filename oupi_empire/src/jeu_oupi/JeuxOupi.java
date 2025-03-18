package jeu_oupi;

import java.awt.Graphics2D;
import java.util.ArrayList;

import gestion_couleur.Couleur;
import interfaces.Dessinable;
import plateau.Plateau;
import plateau.Tuile;
import troupe.Oupi;
import troupe.Troupe;

/**
 * La classe {@code JeuxOupi} représente le jeu Oupi, gérant le plateau de jeu et les troupes.
 * Elle implémente l'interface {@link Dessinable} pour permettre le dessin du plateau et des troupes.
 * 
 * @author Badr Rifki
 * 
 */
public class JeuxOupi implements Dessinable {
    
    // INFO GEN
    private int screenWidth;
    //private int screenHeight;

    // PLATEAU 
    public static Plateau plateau;
    private static int nbTuiles = 20;
    public static int tailleTuile;
    public Tuile tuileSelectionnee;

    // TROUPES
    private ArrayList<Troupe> troupes = new ArrayList<>(); // liste pour annuler une decision faite par un joueur
    private ArrayList<Troupe> simTroupes = new ArrayList<>();
    private Troupe troupeSelectionnee = null;
    private int colO = 0;
    private int ligO = 0;

    /**
     * Constructeur de la classe {@code JeuxOupi}.
     * 
     * @param screenWidth la largeur de l'écran
     * @param screenHeight la hauteur de l'écran
     */
    public JeuxOupi(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        //this.screenHeight = screenHeight;
        
        
        creeTailleTuile();
        setTroupes();
        copyTroupes(troupes, simTroupes);
        
        plateau = new Plateau(nbTuiles, nbTuiles, tailleTuile);
        
        setPosTroupes();
        
        
    }

    /**
     * Initialise les troupes du jeu.
     */
    public void setTroupes() {
        troupes.add(new Oupi(10, 10));
        troupes.add(new Oupi(1, 0));
    }
    
    // je ne peux pas mettre ca dans la methode juste au dessus pour eviter les bugs
    public void setPosTroupes() {
    	plateau.getTuile(10, 10).setOccupee(true);
    	plateau.getTuile(1, 0).setOccupee(true);
    	
    }

    /**
     * Copie les troupes de la source vers la destination.
     * 
     * @param source la liste source des troupes
     * @param dest la liste destination des troupes
     */
    private void copyTroupes(ArrayList<Troupe> source, ArrayList<Troupe> dest) {
        dest.clear();
        for (int i = 0; i < source.size(); i++) {
            dest.add(source.get(i));
        }
    }

    /**
     * Dessine le plateau et les troupes.
     * 
     * @param g2d l'objet {@link Graphics2D} utilisé pour dessiner
     */
    @Override
    public void dessiner(Graphics2D g2d) {
        Graphics2D g2dPrive = (Graphics2D) g2d.create();
        plateau.dessiner(g2dPrive);
        
        for (Troupe p : simTroupes) {
            p.dessiner(g2dPrive);
        }
    }

    /**
     * Retourne le plateau de jeu.
     * 
     * @return le plateau de jeu
     */
    public Plateau getPlateau() {
        return plateau;
    }

    /**
     * Crée la taille des tuiles en fonction de la largeur de l'écran.
     */
    public void creeTailleTuile() {
        // taille = taille du composant c-a-d screenWidth/2 diviser par le nombre de tuiles
        tailleTuile = (screenWidth / 2) / nbTuiles;
    }

    /**
     * Retourne le nombre de tuiles.
     * 
     * @return le nombre de tuiles
     */
    public static int getNbTuiles() {
        return nbTuiles;
    }

    /**
     * Retourne la troupe à la position spécifiée.
     * 
     * @param x la position x
     * @param y la position y
     * @return la troupe à la position spécifiée, ou {@code null} si aucune troupe n'est trouvée
     */
    public Troupe getTroupeA(int x, int y) {
        for (int i = 0; i < troupes.size(); i++) {
            if (simTroupes.get(i).estA(x, y)) {
                return simTroupes.get(i);
            }
        }
        return null;
    }

    /**
     * Déplace la troupe à la position spécifiée vers une nouvelle position.
     * 
     * @param x la position x actuelle
     * @param y la position y actuelle
     * @param newCol la nouvelle colonne
     * @param newLig la nouvelle ligne
     */
    public void setTroupeA(int x, int y, int newCol, int newLig) {
        for (int i = 0; i < troupes.size(); i++) {
            if (simTroupes.get(i).estA(x, y)) {
                simTroupes.get(i).setLig(newLig);
                simTroupes.get(i).setCol(newCol);
            }
        }
    }

    /**
     * Sélectionne une troupe.
     * 
     * @param troupe la troupe à sélectionner 
     */
    public void selectionnerTroupe(Troupe troupe) {
        // Désélectionner la troupe précédemment sélectionnée
        if (troupeSelectionnee != null) {
            troupeSelectionnee.setDistanceParcourable(troupeSelectionnee.getBakDistParc());
            troupeSelectionnee.setSelectionne(false);
        }
        
        // Sélectionner la nouvelle troupe
        troupeSelectionnee = troupe;
        if (troupeSelectionnee != null) {
            troupeSelectionnee.setSelectionne(true);
            
            // Obtenir la position originelle
            ligO = troupeSelectionnee.getLig();
            colO = troupeSelectionnee.getCol();
            
        }
    }

    /**
     * Déplace la troupe sélectionnée vers le haut.
     */
    public void deplacerTroupeSelectionneeHaut() {
        if (troupeSelectionnee != null && troupeSelectionnee.getDistanceParcourable() >= 0) {
        	int col = troupeSelectionnee.getCol();
        	int lig = troupeSelectionnee.getLig();
        	plateau.getTuile(lig, col).setOccupee(false);
            troupeSelectionnee.deplacerHaut(); 
            plateau.getTuile(troupeSelectionnee.getLig(), col).setOccupee(true);
        }
    }

	/**
     * Déplace la troupe sélectionnée vers le bas.
     */
    public void deplacerTroupeSelectionneeBas() {
        if (troupeSelectionnee != null && troupeSelectionnee.getDistanceParcourable() >= 0) {
        	int col = troupeSelectionnee.getCol();
        	int lig = troupeSelectionnee.getLig();
        	plateau.getTuile(lig, col).setOccupee(false);
            troupeSelectionnee.deplacerBas();
            plateau.getTuile(troupeSelectionnee.getLig(), col).setOccupee(true);
        }
    }

    /**
     * Déplace la troupe sélectionnée vers la gauche.
     */
    public void deplacerTroupeSelectionneeGauche() {
        if (troupeSelectionnee != null && troupeSelectionnee.getDistanceParcourable() >= 0) {
        	int col = troupeSelectionnee.getCol();
        	int lig = troupeSelectionnee.getLig();
        	plateau.getTuile(lig, col).setOccupee(false);
            troupeSelectionnee.deplacerGauche();
            plateau.getTuile(lig, troupeSelectionnee.getCol()).setOccupee(true);
        }
    }

    /**
     * Déplace la troupe sélectionnée vers la droite.
     */
    public void deplacerTroupeSelectionneeDroite() {
        if (troupeSelectionnee != null && troupeSelectionnee.getDistanceParcourable() >= 0) {
        	int col = troupeSelectionnee.getCol();
        	int lig = troupeSelectionnee.getLig();
        	plateau.getTuile(lig, col).setOccupee(false);
            troupeSelectionnee.deplacerDroite();
            plateau.getTuile(lig, troupeSelectionnee.getCol()).setOccupee(true);
        }
    }

    /**
     * Réinitialise la position de la troupe sélectionnée à sa position originelle.
     */
    public void resetTroupeAct() {
    	if (troupeSelectionnee != null) {
            troupeSelectionnee.setLig(ligO);
            troupeSelectionnee.setCol(colO);
            troupeSelectionnee.setDistanceParcourable(troupeSelectionnee.getBakDistParc());
        }
    }
    
    
    public Troupe getTroupeSelectionnee() {
		return troupeSelectionnee;
	}
}
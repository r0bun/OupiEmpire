package jeu_oupi;

import interfaces.Dessinable;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import plateau.Plateau;
import plateau.Tuile;
import troupe.Oupi;
import troupe.Troupe;

/**
 * La classe {@code JeuxOupi} représente le jeu Oupi, gérant le plateau de jeu et les troupes.
 * Elle implémente l'interface {@link Dessinable} pour permettre le dessin du plateau et des troupes.
 * 
 * @author Badr Rifki
 * @author Loic Simard
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
        plateau = new Plateau(1,1,1);
        plateau.loadPlateau("res/cartes/map.txt"); //J'arrive pas a utiliser 
        
        tailleTuile = plateau.getTailleTuile();
        
        
    }

    /**
     * Initialise les troupes du jeu.
     */
    public void setTroupes() {
        troupes.add(new Oupi(10, 10, 1));
        troupes.add(new Oupi(1, 0, 0));
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
            deselectionnerTroupe(troupeSelectionnee);
        }
        
        // Sélectionner la nouvelle troupe
        troupeSelectionnee = troupe;
        if (troupeSelectionnee != null) {
            troupeSelectionnee.setSelectionne(true);
            
            // Obtenir la position originelle
            ligO = troupeSelectionnee.getLig();
            colO = troupeSelectionnee.getCol();
            
            plateau.getTuile(ligO, colO).setPosDep(true);
            plateau.getTuile(ligO, colO).setCouleur(Color.CYAN);
            
        }
    }
    
    public void deselectionnerTroupe(Troupe troupe) {
    	troupeSelectionnee.deselec();
    	troupeSelectionnee = null;
    	
    	plateau.getTuile(ligO, colO).setPosDep(false);
        plateau.getTuile(ligO, colO).setCouleur(Color.RED);
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
    		int col = troupeSelectionnee.getCol();
        	int lig = troupeSelectionnee.getLig();
        	plateau.getTuile(lig, col).setOccupee(false);
        	
            troupeSelectionnee.setLig(ligO);
            troupeSelectionnee.setCol(colO);
            
            int col2 = troupeSelectionnee.getCol();
        	int lig2 = troupeSelectionnee.getLig();
        	plateau.getTuile(lig2, col2).setOccupee(true);
        	
        	deselectionnerTroupe(troupeSelectionnee);
        }
    }
    
    public void comfirm() {
    	deselectionnerTroupe(troupeSelectionnee);
	}
    
    /**
     * Fait attaquer une troupe ciblée par la troupe actuellement sélectionnée.
     * 
     * @param troupeCible la troupe à attaquer
     * @return true si l'attaque a été effectuée avec succès, false sinon
     */
    public boolean attaquerTroupe(Troupe troupeCible) {
        if (troupeSelectionnee == null || troupeCible == null) {
            System.out.println("⚠️ Échec de l'attaque: Aucune troupe sélectionnée ou cible invalide.");
            return false;
        }
        
        if (troupeSelectionnee == troupeCible) {
            System.out.println("⚠️ Échec de l'attaque: Une troupe ne peut pas s'attaquer elle-même.");
            return false;
        }
        
        if (troupeSelectionnee.getEquipe() == troupeCible.getEquipe()) {
            System.out.println("⚠️ Échec de l'attaque: Impossible d'attaquer une troupe alliée.");
            return false;
        }
        
        // Calculer la distance entre les troupes (distance Manhattan)
        int distance = Math.abs(troupeSelectionnee.getCol() - troupeCible.getCol()) + 
                       Math.abs(troupeSelectionnee.getLig() - troupeCible.getLig());
        
        // Vérifier si la cible est à portée d'attaque (distance 1 pour attaque corps à corps)
        if (distance > 1) {
            System.out.println("⚠️ Échec de l'attaque: La cible est trop éloignée (distance " + distance + ")");
            return false;
        }
        
        System.out.println("🗡️ Attaque initiée par " + troupeSelectionnee.getClass().getSimpleName() + 
                          " contre " + troupeCible.getClass().getSimpleName());
        
        // Appel de la méthode d'attaque de la troupe
        troupeSelectionnee.attaquer(troupeCible);
        
        // Vérifier si la troupe cible est morte (HP <= 0)
        if (troupeCible.getHP() <= 0) {
            System.out.println("💀 " + troupeCible.getClass().getSimpleName() + " a été vaincu!");
            // On pourrait ajouter ici la logique pour retirer la troupe du jeu
            simTroupes.remove(troupeCible);
            troupes.remove(troupeCible);
        }
        
        // Vérifier si l'attaquant est mort suite à une contre-attaque
        if (troupeSelectionnee.getHP() <= 0) {
            System.out.println("💀 " + troupeSelectionnee.getClass().getSimpleName() + " a été vaincu!");
            // On pourrait ajouter ici la logique pour retirer la troupe du jeu
            simTroupes.remove(troupeSelectionnee);
            troupes.remove(troupeSelectionnee);
            troupeSelectionnee = null;
        }
        
        return true;
    }
    
    public Troupe getTroupeSelectionnee() {
		return troupeSelectionnee;
	}

	
}
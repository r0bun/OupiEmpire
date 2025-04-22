package jeu_oupi;

import interfaces.Dessinable;
import java.awt.Graphics2D;
import java.util.ArrayList;
import plateau.Plateau;
import plateau.Tuile;
import troupe.*;

/**
 * La classe {@code JeuxOupi} représente le jeu Oupi, gérant le plateau de jeu
 * et les troupes. Elle implémente l'interface {@link Dessinable} pour permettre
 * le dessin du plateau et des troupes.
 *
 * @author Badr Rifki
 * @author Loic Simard
 */
public class JeuxOupi implements Dessinable {
    // Variables d'instance
    // INFO GEN
    private int screenWidth;
    
    // PLATEAU
    public Plateau plateau; 
    private int nbTuiles = 20; 
    public int tailleTuile; 
    public Tuile tuileSelectionnee;

    // TROUPES
    private ArrayList<Troupe> troupes = new ArrayList<>();
    private ArrayList<Troupe> simTroupes = new ArrayList<>();
    private Troupe troupeSelectionnee = null;
    private int colO = 0;
    private int ligO = 0;
    
    private int zonePlacer = 4;

    // Liste pour stocker les messages d'attaque et d'erreur
    private ArrayList<String> combatMessages = new ArrayList<>();

    /**
     * Constructeur de la classe {@code JeuxOupi}.
     *
     * @param screenWidth la largeur de l'écran
     * @param screenHeight la hauteur de l'écran
     */
    public JeuxOupi(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;

        creeTailleTuile();
        setTroupes();
        copyTroupes(troupes, simTroupes);

        // Charger la carte en premier
        plateau = new Plateau(1, 1, 1);
        plateau.loadPlateau("res/cartes/map2.txt");
        
        for(int i = 0; i < zonePlacer; i++) {
        	for(int j = 0 ; j < plateau.getColonnes(); j++) {
        		if(!plateau.getTuile(i, j).estOccupee()) {
        			plateau.getTuile(i, j).setAccessible(true);
        		}
        	}
        }
        

		int zoneBas = plateau.getLignes()-zonePlacer;
        for(int i = 0; i < zonePlacer; i++) {
        	for(int j = 0 ; j < plateau.getColonnes(); j++) {
        		if(!plateau.getTuile(zoneBas+i, j).estOccupee()) {
        			plateau.getTuile(zoneBas+i, j).setPlacable(true);
        		}
        	}
        }

        // Mettre à jour nbTuiles pour correspondre aux dimensions de la carte chargée
        nbTuiles = Math.max(plateau.getLignes(), plateau.getColonnes());
        tailleTuile = plateau.getTailleTuile();

        // Initialiser les positions des troupes
        setPosTroupes();
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
     * Initialise les troupes du jeu.
     */
    public void setTroupes() {
        // Équipe 0 (joueur 1)
        troupes.add(new Oupi(1, 1, 0, this));
        troupes.add(new Lobotomisateur(3, 1, 0, this));

        // Équipe 1 (joueur 2)
        troupes.add(new Genial(12, 8, 1, this));
        troupes.add(new Electricien(14, 8, 1, this));
    }

    /**
     * Initialise les positions des troupes sur le plateau
     */
    public void setPosTroupes() {
        // S'assurer que les tuiles correspondantes sont marquées comme occupées
        for (Troupe troupe : troupes) {
            int lig = troupe.getLig();
            int col = troupe.getCol();
            if (lig >= 0 && lig < nbTuiles && col >= 0 && col < nbTuiles) {
                plateau.getTuile(lig, col).setOccupee(true);
            }
            // Forcer la mise à jour des positions en pixels
            troupe.setCol(col);
            troupe.setLig(lig);
        }
    }

    /**
     * Ajoute une nouvelle troupe à la liste de troupes sur le plateau
     *
     * @param troupe La troupe à ajouter
     */
    public void addTroupe(Troupe troupe) {
        troupes.add(troupe);
        simTroupes.add(troupe);
    }

    /**
     * Retire une troupe de la liste de troupes sur le plateau
     *
     * @param troupe La troupe à retirer
     * @return L'identifiant de la troupe retirée
     */
    public int delTroupe(Troupe troupe) {
        troupes.remove(troupe);
        simTroupes.remove(troupe);
        deselectionnerTroupeAct();
        return troupe.getId();
    }

    /**
     * Crée la taille des tuiles en fonction de la largeur de l'écran.
     */
    public void creeTailleTuile() {
        // taille = taille du composant c-a-d screenWidth/2 diviser par le nombre de tuiles
        tailleTuile = (screenWidth / 2) / nbTuiles;
    }

    /**
     * Sélectionne une troupe.
     *
     * @param troupe la troupe à sélectionner
     */
    public void selectionnerTroupe(Troupe troupe) {
        // Désélectionner la troupe précédemment sélectionnée
        if (troupeSelectionnee != null) {
            deselectionnerTroupeAct();
        }

        // Sélectionner la nouvelle troupe
        troupeSelectionnee = troupe;
        if (troupeSelectionnee != null) {
            troupeSelectionnee.setSelectionne(true);

            // Obtenir la position originelle
            ligO = troupeSelectionnee.getLig();
            colO = troupeSelectionnee.getCol();

            // Marquer comme position de départ sans changer la couleur
            plateau.getTuile(ligO, colO).setPosDep(true);
        }
    }
    
    /**
     * Désélectionne la troupe actuellement sélectionnée.
     */
    public void deselectionnerTroupeAct() {
    	if (troupeSelectionnee != null) {
            troupeSelectionnee.deselec();

            // Réinitialiser le marquage de la position de départ
            plateau.getTuile(ligO, colO).setPosDep(false);

            troupeSelectionnee = null;
        }
    }

    /**
     * Déplace la troupe sélectionnée vers le haut.
     */
    public void deplacerTroupeSelectionneeHaut() {
        if (troupeSelectionnee != null && troupeSelectionnee.getDistanceParcourable() >= 0 && !troupeSelectionnee.isEpuisee()) {
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
        if (troupeSelectionnee != null && troupeSelectionnee.getDistanceParcourable() >= 0
                && !troupeSelectionnee.isEpuisee()) {
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
        if (troupeSelectionnee != null && troupeSelectionnee.getDistanceParcourable() >= 0
                && !troupeSelectionnee.isEpuisee()) {
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
        if (troupeSelectionnee != null && troupeSelectionnee.getDistanceParcourable() >= 0
                && !troupeSelectionnee.isEpuisee()) {
            int col = troupeSelectionnee.getCol();
            int lig = troupeSelectionnee.getLig();
            plateau.getTuile(lig, col).setOccupee(false);
            troupeSelectionnee.deplacerDroite();
            plateau.getTuile(lig, troupeSelectionnee.getCol()).setOccupee(true);
        }
    }

    /**
     * Réinitialise la position de la troupe sélectionnée à sa position
     * originelle.
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

            deselectionnerTroupeAct();
        }
    }

    /**
     * Confirme le mouvement de la troupe sélectionnée et la marque comme épuisée.
     */
    public void confirm() {
        troupeSelectionnee.setEpuisee(true);
        deselectionnerTroupeAct();
    }

    /**
     * Fait attaquer une troupe ciblée par la troupe actuellement sélectionnée.
     *
     * @param troupeCible la troupe à attaquer
     * @return true si l'attaque a été effectuée avec succès, false sinon
     */
    public boolean attaquerTroupe(Troupe troupeCible) {
        // Vider les messages précédents
        combatMessages.clear();
        
        if (troupeSelectionnee == null || troupeCible == null) {
            String msg = "⚠️ Échec de l'attaque: Aucune troupe sélectionnée ou cible invalide.";
            System.out.println(msg);
            combatMessages.add(msg);
            return false;
        }

        if (troupeSelectionnee == troupeCible) {
            String msg = "⚠️ Échec de l'attaque: Une troupe ne peut pas s'attaquer elle-même.";
            System.out.println(msg);
            combatMessages.add(msg);
            return false;
        }

        if (troupeSelectionnee.getEquipe() == troupeCible.getEquipe()) {
            String msg = "⚠️ Échec de l'attaque: Impossible d'attaquer une troupe alliée.";
            System.out.println(msg);
            combatMessages.add(msg);
            return false;
        }

        // Calculer la distance entre les troupes (distance Manhattan)
        int distance = Math.abs(troupeSelectionnee.getCol() - troupeCible.getCol())
                + Math.abs(troupeSelectionnee.getLig() - troupeCible.getLig());

        // Vérifier si la cible est à portée d'attaque selon la distance d'attaque de la troupe
        if (distance > troupeSelectionnee.getDistanceAttaque()) {
            String msg = "⚠️ Échec de l'attaque: La cible est trop éloignée (distance " + distance
                    + ", portée maximale " + troupeSelectionnee.getDistanceAttaque() + ")";
            System.out.println(msg);
            combatMessages.add(msg);
            return false;
        }

        String msg = "🗡️ Attaque initiée par " + troupeSelectionnee.getClass().getSimpleName()
                + " contre " + troupeCible.getClass().getSimpleName();
        System.out.println(msg);
        combatMessages.add(msg);

        // Appel de la méthode d'attaque de la troupe
        troupeSelectionnee.attaquer(troupeCible);
        
        fireAttackEvent();

        // Vérifier si la troupe cible est morte (HP <= 0)
        if (troupeCible.getHP() <= 0) {
        	System.out.println("💀 " + troupeCible.getClass().getSimpleName() + " a été vaincu!");
        	troupeSelectionnee.kill();
        	gererMortTroupe(troupeCible);
        	
        }

        // Vérifier si l'attaquant est mort suite à une contre-attaque
        if (troupeSelectionnee.getHP() <= 0) {
        	System.out.println("💀 " + troupeSelectionnee.getClass().getSimpleName() + " a été vaincu!");
        	gererMortTroupe(troupeSelectionnee);
        	troupeSelectionnee = null;
        }
        
        return true;
    }

    /**
     * Gère la mort d'une troupe en libérant sa tuile et en effaçant les tuiles
     * accessibles.
     *
     * @param troupe La troupe qui meurt
     */
    private void gererMortTroupe(Troupe troupe) {
        if (troupe != null) {
            // Libérer la tuile occupée
            int lig = troupe.getLig();
            int col = troupe.getCol();
            plateau.getTuile(lig, col).setOccupee(false);

            // Retirer la troupe des listes
            troupes.remove(troupe);
            simTroupes.remove(troupe);
        }
    }

    /**
     * Méthode utilisée pour déclencher des événements d'attaque.
     */
    private void fireAttackEvent() {
        // Implémenté par ZoneAnimationOupi
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
     * Récupère les messages de combat générés dans JeuxOupi.
     * 
     * @return liste de messages de combat
     */
    public ArrayList<String> getCombatMessages() {
        return new ArrayList<>(combatMessages);
    }
    
    /**
     * Vide la liste des messages de combat.
     */
    public void clearCombatMessages() {
        combatMessages.clear();
    }

    // --- GETTERS ET SETTERS ---

    /**
     * Retourne le plateau de jeu.
     *
     * @return le plateau de jeu
     */
    public Plateau getPlateau() {
        return plateau;
    }

    /**
     * Retourne le nombre de tuiles.
     *
     * @return le nombre de tuiles
     */
    public int getNbTuiles() {
        return nbTuiles;
    }

    /**
     * Retourne la taille d'une tuile.
     *
     * @return la taille d'une tuile en pixels
     */
    public int getTailleTuile() {
        return tailleTuile;
    }

    /**
     * Retourne la troupe à la position spécifiée.
     *
     * @param x la position x
     * @param y la position y
     * @return la troupe à la position spécifiée, ou {@code null} si aucune
     * troupe n'est trouvée
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
     * Retourne la troupe actuellement sélectionnée.
     *
     * @return la troupe actuellement sélectionnée
     */
    public Troupe getTroupeSelectionnee() {
        return troupeSelectionnee;
    }

    /**
     * Retourne la liste des troupes.
     *
     * @return la liste des troupes
     */
    public ArrayList<Troupe> getTroupes() {
        return troupes;
    }

    /**
     * Retourne la liste des troupes appartenant à un joueur spécifique.
     *
     * @param player l'identifiant du joueur
     * @return la liste des troupes du joueur
     */
    public ArrayList<Troupe> getTroupePlayer(int player) {
        ArrayList<Troupe> troupesP = new ArrayList<>();
        for (int i = 0; i < troupes.size(); i++) {
            if (troupes.get(i).getEquipe() == player) {
                troupesP.add(troupes.get(i));
            }
        }
        return troupesP;
    }

    /**
     * Définit la liste des troupes.
     *
     * @param troupes la liste des troupes à définir
     */
    public void setTroupes(ArrayList<Troupe> troupes) {
        this.troupes = troupes;
    }
    
    public boolean isInZone(Tuile clique, Tuile coin) {
    	if(clique.getLig() <= coin.getLig()+zonePlacer && clique.getLig() >= coin.getLig()) {
    			return true;    	}
    	return false;
    }
    
    public void finirPlacer() {
    	for(int i = 0; i < plateau.getLignes(); i++) {
    		for(int j = 0; j < plateau.getColonnes(); j++) {
    			plateau.getTuile(i, j).setPlacable(false);
    			plateau.getTuile(i, j).setAccessible(false);
    		}
    	}
    }
}

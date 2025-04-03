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

    // INFO GEN
    private int screenWidth;
    // private int screenHeight;

    // PLATEAU
    public Plateau plateau; // Non-statique maintenant
    private int nbTuiles = 20; // Non-statique maintenant
    public int tailleTuile; // Non-statique maintenant
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

        // Load the map first
        plateau = new Plateau(1, 1, 1);
        plateau.loadPlateau("res/cartes/map.txt");

        // Update nbTuiles to match the loaded map dimensions
        nbTuiles = Math.max(plateau.getLignes(), plateau.getColonnes());
        tailleTuile = plateau.getTailleTuile();

        // Initialiser les positions des troupes
        setPosTroupes();
    }

    /**
     * Ajoutes une nouvelle troupe a la liste de troupes sur le plateau
     *
     * @param troupe La troupe a ajouter
     */
    public void addTroupe(Troupe troupe) {
        troupes.add(troupe);
        simTroupes.add(troupe);
    }

    /**
     * Retire une troupe de la liste de troupes sur le plateau
     *
     * @param troupe La troupe a retirer
     * @return L'identifiant de la troupe retiree
     */
    public int delTroupe(Troupe troupe) {
        troupes.remove(troupe);
        simTroupes.remove(troupe);
        deselectionnerTroupeAct();
        return troupe.getId();
    }

    /**
     * Initialise les troupes du jeu.
     */
    public void setTroupes() {
        // Équipe 0 (joueur 1)
        troupes.add(new Oupi(1, 1, 0, this));
        troupes.add(new Lobotomisateur(3, 1, 0, this));

        // Équipe 1 (joueur 2)
        troupes.add(new Genial(15, 15, 1, this));
        troupes.add(new Electricien(17, 15, 1, this));
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

            // Marquer comme position de départ sans changer la couleur
            plateau.getTuile(ligO, colO).setPosDep(true);
        }
    }

    public void deselectionnerTroupe(Troupe troupe) {
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

            deselectionnerTroupe(troupeSelectionnee);
        }
    }

    public void confirm() {
        troupeSelectionnee.setEpuisee(true);
        deselectionnerTroupe(troupeSelectionnee);
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

            // Effacer les tuiles accessibles
            troupe.deselec();

            // Retirer la troupe des listes
            troupes.remove(troupe);
            simTroupes.remove(troupe);
        }
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
        int distance = Math.abs(troupeSelectionnee.getCol() - troupeCible.getCol())
                + Math.abs(troupeSelectionnee.getLig() - troupeCible.getLig());

        // Vérifier si la cible est à portée d'attaque selon la distance d'attaque de la troupe
        if (distance > troupeSelectionnee.getDistanceAttaque()) {
            System.out.println("⚠️ Échec de l'attaque: La cible est trop éloignée (distance " + distance
                    + ", portée maximale " + troupeSelectionnee.getDistanceAttaque() + ")");
            return false;
        }

        System.out.println("🗡️ Attaque initiée par " + troupeSelectionnee.getClass().getSimpleName()
                + " contre " + troupeCible.getClass().getSimpleName());

        // Appel de la méthode d'attaque de la troupe
        troupeSelectionnee.attaquer(troupeCible);

        // Vérifier si la troupe cible est morte (HP <= 0)
        if (troupeCible.getHP() <= 0) {
            System.out.println("💀 " + troupeCible.getClass().getSimpleName() + " a été vaincu!");
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

    public Troupe getTroupeSelectionnee() {
        return troupeSelectionnee;
    }

    public ArrayList<Troupe> getTroupes() {
        return troupes;
    }

    public ArrayList<Troupe> getTroupePlayer(int player) {
        ArrayList<Troupe> troupesP = new ArrayList<>();
        for (int i = 0; i < troupes.size(); i++) {
            if (troupes.get(i).getEquipe() == player) {
                troupesP.add(troupes.get(i));
            }
        }

        return troupesP;
    }

    public void setTroupes(ArrayList<Troupe> troupes) {
        this.troupes = troupes;
    }

    public void deselectionnerTroupeAct() {
        deselectionnerTroupe(troupeSelectionnee);

    }
}

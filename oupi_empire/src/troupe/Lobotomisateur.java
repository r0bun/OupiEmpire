package troupe;

import jeu_oupi.JeuxOupi;

/**
 * Troupe Lobotomisateur - Spécialisé en attaque avec une grande puissance
 * 
 * @author Badr Rifki
 */
public class Lobotomisateur extends Troupe {

    /**
     * Constructeur pour créer un objet de type Lobotomisateur
     * 
     * @param col colonne de la troupe
     * @param lig ligne de la troupe
     * @param equipe l'équipe à laquelle appartient la troupe
     * @param jeu l'instance du jeu associée à cette troupe
     */
    public Lobotomisateur(int col, int lig, int equipe, JeuxOupi jeu) {
        super(lig, col, jeu, "Le Lobotomisateur", "res/bak/lobotomisateur_player_card.png");
        this.setDistanceParcourable(3);
        this.setBakDistParc(3);
        this.setEquipe(equipe);
        image = getImage("/troupes/lobotomisateur.png");
        
        // Corps à corps (distance d'attaque de 1)
        this.setDistanceAttaque(1);
        
        // Statistiques spécifiques - forte attaque mais faible défense
        HP = 80;
        attaque = 35;
        defense = 5;
        vitesse = 25;
        endurance = 20;
        
        id = 3;
    }
}

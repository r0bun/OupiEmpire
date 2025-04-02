package troupe;

import jeu_oupi.JeuxOupi;

/**
 * Troupe Genial - Combattant équilibré avec de bonnes statistiques générales
 * 
 * @author Badr Rifki
 */
public class Genial extends Troupe {

    /**
     * Constructeur pour créer un objet de type Genial
     * 
     * @param col colonne de la troupe
     * @param lig ligne de la troupe
     * @param equipe l'équipe à laquelle appartient la troupe
     * @param jeu l'instance du jeu associée à cette troupe
     */
    public Genial(int col, int lig, int equipe, JeuxOupi jeu) {
        super(lig, col, jeu);
        this.setDistanceParcourable(4);
        this.setBakDistParc(4);
        this.setEquipe(equipe);
        image = getImage("/troupes/genial.png");
        
        // Corps à corps (distance d'attaque de 1)
        this.setDistanceAttaque(1);
        
        // Statistiques équilibrées
        HP = 120;
        attaque = 25;
        defense = 15;
        vitesse = 15;
        endurance = 40;
        
        id = 2;
    }
}

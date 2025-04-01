package troupe;

import jeu_oupi.JeuxOupi;

/**
 * Troupe Electricien - Spécialiste des attaques à distance
 * 
 * @author Badr Rifki
 */
public class Electricien extends Troupe {

    /**
     * Constructeur pour créer un objet de type Electricien
     * 
     * @param col colonne de la troupe
     * @param lig ligne de la troupe
     * @param equipe l'équipe à laquelle appartient la troupe
     * @param jeu l'instance du jeu associée à cette troupe
     */
    public Electricien(int col, int lig, int equipe, JeuxOupi jeu) {
        super(lig, col, jeu);
        this.setDistanceParcourable(3);
        this.setBakDistParc(3);
        this.setEquipe(equipe);
        image = getImage("/troupes/electricien.png");
        
        // Utilisation de la propriété distanceAttaque de la classe parent
        this.setDistanceAttaque(2);
        
        // Statistiques spécifiques - faible en défense mais bon en attaque à distance
        HP = 90;
        attaque = 30;
        defense = 8;
        vitesse = 18;
        endurance = 25;
    }
}

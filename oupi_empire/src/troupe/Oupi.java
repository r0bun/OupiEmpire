package troupe;

import jeu_oupi.JeuxOupi;

/**
 * Troupe Oupi
 * 
 * @author Badr Rifki
 */
public class Oupi extends Troupe {

	/**
	 * classe pour generer un objet de type Oupi
	 * 
	 * @param col colone de la troupe
	 * @param lig ligne de la troupe
	 * @param equipe l'equipe a laquelle appartient la troupe
	 * @param jeu l'instance du jeu associée à cette troupe
	 */
	public Oupi(int col, int lig, int equipe, JeuxOupi jeu) {
		super(col, lig, jeu);
		this.setDistanceParcourable(5);
		this.setBakDistParc(5);
		this.setEquipe(equipe);
		image = getImage("/troupes/oupi.jpg");
		
		// Corps à corps (distance d'attaque de 1, valeur par défaut)
		this.setDistanceAttaque(1);
		
		HP=150;
	}
}

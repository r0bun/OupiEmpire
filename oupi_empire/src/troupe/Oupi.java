package troupe;

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
	 */
	public Oupi(int col, int lig, int equipe) {
		super(col, lig);
		this.setDistanceParcourable(5);
		this.setBakDistParc(5);
		this.setEquipe(equipe);
		image = getImage("/troupes/oupi_processed");
		
		HP=150;
	}
 
}

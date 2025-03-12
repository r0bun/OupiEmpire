package troupe;

public class Oupi extends Troupe {

	public Oupi(int col, int lig) {
		super(col, lig);
		this.setDistanceParcourable(5);
		this.setBakDistParc(5);
		image = getImage("/troupes/oupi_processed");
	}

}

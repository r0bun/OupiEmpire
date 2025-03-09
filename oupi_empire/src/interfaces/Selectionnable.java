package interfaces;

/**
 * Interface qui définit la méthode qu'un objet doit implémenter pour pouvoir
 * être sélectionné 
 *  
 * @author Caroline Houle
 *
 */

public interface Selectionnable {
	
	/**
	 * Retourne vrai si le point passé en paramètre fait partie de l'objet dessinable
	 * sur lequel cette methode sera appelée
	 * 
	 * 
	 * @param xPix Coordonnée en x du point (exprimé en pixels) 
	 * @param yPix Coordonnée en y du point (exprimé en pixels)
	 */
	public boolean contient(double xPix, double yPix);
	
}

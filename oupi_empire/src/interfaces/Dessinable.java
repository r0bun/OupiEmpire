package interfaces;
import java.awt.Graphics2D;

/**
 * Méthode servant à créer des objets dessinables
 * 
 */
public interface Dessinable {
	
	/**
	 * Sert à dessiner l'objet 
	 * @param g2d l'endroit ou sera dessiner l'objet
	 */
	public void dessiner(Graphics2D g2d);
}

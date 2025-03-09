package plateau;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import gestion_couleur.Couleur;
import interfaces.Dessinable;
import interfaces.ObjetCollision;
import vecteurs.Vecteur2D;

public class Plateau extends ObjetCollision{

	private Rectangle2D.Double carrePlat;
	
	private Vecteur2D coinGaucheH, coinDroitH, coinGaucheB, coinDroitB;
	
	/** largeur d'un cote dy carre interieur du plateau*/
	private double largeur;
	
	/** hauteur d'un cote dy carre interieur du plateau*/
	private double hauteur;
	
	private Couleur couleurPlatLignes, couleurPlateauFond;
	
	 /** Support de changement de propriété pour cette classe. */
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Ajoute un écouteur de changement de propriété à cette classe.
     * 
     * @param listener L'écouteur de changement de propriété à ajouter.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }
    
    
    public Plateau (Couleur couleurFond, Couleur couleurLignes, double posX, double posY,  double largeur, double hauteur) {
    	super (new Vecteur2D (posX,posY), 0);
		this.couleurPlatLignes = couleurLignes;
		this.couleurPlateauFond = couleurFond;
		this.largeur = largeur;
		this.hauteur = hauteur;
		
		creerFormes();
		
	}
	
	@Override
	public void dessiner(Graphics2D g2d) {
		Graphics2D g2dPrive = (Graphics2D) g2d.create();
		
		g2dPrive.setColor(couleurPlateauFond.getCouleur());
		g2dPrive.fill(carrePlat);
	}




	@Override
	public void creerFormes() {
		
		carrePlat = new Rectangle2D.Double(getPos().getX(), getPos().getY(), largeur, hauteur);
		
	}
	
	 /**
     * methode qui prend les formes cree dans la methode creer formes et qui retourne un carre propice au collisions
     */
    //Badr Rifki
	/*
    public void creerZone() {
    	Area carrePlateau = new Area(carrePlat);
    	
    }
    */

}

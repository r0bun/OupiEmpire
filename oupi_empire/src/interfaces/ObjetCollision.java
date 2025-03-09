package interfaces;

import java.awt.geom.Area;

import vecteurs.Vecteur2D;


/**
 * 	Classe permettant de créer des objets avec lesquels on peut avoir des collisions ou les déplacer
 * 
 * @author Badr Rifki
 */
public abstract class ObjetCollision implements Dessinable, Selectionnable  {


	/** Position en x et en y du milieu de l'objet */
	protected  Vecteur2D  pos;
	/** position en z de l'objet*/ 
	private double posZ = 0;
	
	
	/** air de l'objet*/
	private Area air;
	
	/** la valeur des pixels par mètres de l'objet*/
	private double pixelsParMetre;

	/**
	 * Sert à créer un objet ayant une position
	 * 
	 * @param pos la position en x et en y
	 * @param posZ la position en z (en hauteur)
	 */
	public ObjetCollision (Vecteur2D pos, double posZ){
		this.pos =pos;
		this.posZ = posZ; 
	}



	/**
	 * Sert à retourner la position en z
	 * 
	 * @return la position en z
	 */
	public double getPosZ() {
		return posZ;
	}


	/**
	 * Sert à changer la position en Z
	 * 
	 * @param posZ la nouvelle position en z
	 */
	public void setPosZ(double posZ) {
		this.posZ = posZ;
	}


	/**
	 * Sert à retourner l'air de l'objet
	 * 
	 * @return L'air totale de l'objet
	 */
	public Area getAir() {
		return air;

	}



	/**
	 * Sert à changer l'air d'un objet 
	 * 
	 * @param air la nouvelle air de l'objet
	 */
	public void setAir(Area air) {
		this.air = air;
	}



	/**
	 * Sert à donner la position du centre de l'objet
	 * 
	 * @return la position de l'objet en vecteur2d
	 */
	public Vecteur2D getPos() {
		return pos;
	}



	/**
	 * Sert à changer la position avec un vecteur2d
	 * 
	 * @param pos la position à changer
	 */
	public void setPos(Vecteur2D pos) {
		this.pos = pos;
		creerFormes();
	}
	
	/** 
	 * Sert à créer les différentes formes d'une pichenotte (à redéfinir)
	 */
	public abstract void creerFormes();
	
	
	
	@Override
	public boolean contient(double xPix, double yPix) {
		return getAir().contains(xPix, yPix);
	}
	
	
	/**
	 * Modifie la facteur mutiplicatif permettant de passer
	 * des mètres aux pixels
	 * 
	 * @param pixelsParMetre facteur mutiplicatif permettant de passer des metres aux pixels
	 */
	public void setPixelsParMetre(double pixelsParMetre) {
		this.pixelsParMetre = pixelsParMetre;
	}
	
	/**
	 * Sert à obtenir la valeur des pixels par mètres de l'objet
	 * 
	 * @return pixelsParMetre facteur mutiplicatif permettant de passer des metres aux pixels
	 */
	public double getPixelsParMetre() {
		return pixelsParMetre;
	}
	
	/**
	 * Méthode peu efficace pour gérer les collisions entre n'importe quel obstacle. 
	 * Probablement à changer dans chaque objet car consomme beaucoup de ressources (new Area)
	 * 
	 * @param objetCollision l'objet avec lequel on veut tester la collision
	 */
	//Badr Rifki
	public boolean testerCollision(ObjetCollision objetCollision) {
		Area airObstacle = objetCollision.getAir();
		Area air2 = new Area (air);
		air2.subtract(airObstacle);
		
		if(air2.equals(air)) {
			return false;
		}
		return true;
	}

	

}

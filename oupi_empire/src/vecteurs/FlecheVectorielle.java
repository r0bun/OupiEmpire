package vecteurs;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import interfaces.Dessinable;

/**
 * Cette classe permet de dessiner une fleche, dans un contexte ou le programmeur
 * d�sire utiliser cette fleche pour illustrer visuellement un vecteur.
 * La position de la tete de la fleche est trouvee grace aux composantes fournies 
 * dans le constructeur.
 * 
 * Notez qu'il est possible de modifier la longueur du trait de tete ainsi
 * que l'angle entre les traits qui forment la tete.
 * 
 * Il est aussi possible de modifier la longueur du corps de la fleche tout en conservant son origine,
 * en specifiant un facteur de redimensionnement. Ceci est parfois necessaire
 * quand le module du vecteur est trop grand/trop petit pour donner un resultat
 * visuel interessant.
 * 
 * @author Caroline Houle
 *
 */
public class FlecheVectorielle implements Dessinable {

	private double x1, y1;
	private double x2, y2;
	private Line2D.Double corps, traitDeTete;   //geometrie necessaire
	private double angleTete = 30;              //en degres, angle par defaut entre les deux segments formant la tete de fleche
	private double longueurTraitDeTete = 1;    //longueur par defaut des segments formant la tete
	private double pixelsParMetre = 1;

	/**
	 * Cree une fleche en specifiant son origine ainsi qu'un vecteur qui indique
	 * ses composantes (longueurs des d�placements en x et en y pour d�terminer ou se
	 * trouve la pointe de la fleche)
	 * @param x1 origine en x
	 * @param y1 origine en y
	 * @param vec vecteur qui specifie les composantes dx et dy du vecteur
	 */
	public FlecheVectorielle (double x1, double y1, Vecteur2D vec) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x1 + vec.getX();
		this.y2 = y1 + vec.getY();
		creerLaGeometrie();
	}

	/**
	 * Cree une fleche en specifiant son origine et la dimension de ses composantes (longueurs
	 * des d�placements en x et en y pour d�terminer ou se trouve la pointe de la fleche)
	 * @param x1 origine en x
	 * @param y1 origine en y
	 * @param dx longueur de la composante en x
	 * @param dy longueur de la composante en y
	 */
	public FlecheVectorielle(double x1, double y1, double x2, double y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		creerLaGeometrie();
	}

	/**
	 * Cree les formes geometriques utiles pour dessiner la fleche
	 */
	private void creerLaGeometrie() {

		//le corps de la fleche
		corps = new Line2D.Double(x1, y1, x2, y2);

		/*
		 En utilisant la theorie des triangles semblables, cr�er un petit trait qui se
		 confond avec le corps de la fl�che. En ajoutant des rotations a ce trait au moment
		 du dessin on obtiendra la positionde la pointe de la fleche.
		 */
		double longueurFleche = Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) );
		double dxPetitTrait = longueurTraitDeTete*(x2-x1)/longueurFleche;
		double dyPetitTrait = longueurTraitDeTete*(y2-y1)/longueurFleche;
		double x3 = x2-dxPetitTrait;
		double y3 = y2-dyPetitTrait;
		traitDeTete = new Line2D.Double( x2, y2, x3, y3);
	}

	/**
	 * Dessiner la fl�che
	 * @param g2d Le contexte graphique
	 */
	@Override
	public void dessiner(Graphics2D g2d) {	
		AffineTransform mat = new AffineTransform();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

		mat.scale(pixelsParMetre, pixelsParMetre);

		g2d.draw( mat.createTransformedShape(corps) );  		//corps de la fleche

		mat.rotate(Math.toRadians(angleTete/2), x2,  y2);
		g2d.draw( mat.createTransformedShape(traitDeTete) );  	//un des deux traits qui formeront la tete de la fleche
		mat.rotate(Math.toRadians(-angleTete), x2,  y2);
		g2d.draw(mat.createTransformedShape(traitDeTete) );  	//l'autre trait qui formera la tete de la fleche


	}// fin

	/**
	 * Sert a donner la direction de la fleche
	 * @return l'orientation de la fleche
	 */
	//Frédéric Houde
	public Vecteur2D getOrientation() {
		return new Vecteur2D(x2-x1,y2-y1);

	}

	/**
	 * Sert a donner la longueur de la fleche
	 * @return la longueur de la fleche
	 */
	public double getLongueur(){
		return Math.pow(Math.pow((x2-x1),2.0)+Math.pow((y2-y1),2.0),.5);
	}
	/**
	 * Permet de modifier la longueur du corps en multipliant le tout par un facteur sp�cifi�.
	 * L'orgine de la fleche demeure la m�me, mais sa deuxieme extremite (positionde la pointe) se 
	 * trouvera modifiee!
	 * @param facteurRedim Facteur multiplicatif pour la  longueur du corps. Un facteur 1 ne changera rien.
	 */
	public void redimensionneCorps(double facteurRedim) {
		this.x2 = x1 + (x2-x1)*facteurRedim;
		this.y2 = y1 + (y2-y1)*facteurRedim;
		creerLaGeometrie();  //esentiel!
	}

	/**
	 * Retourne la valeur de l'angle entre les deux traits formant la tete de la fleche 
	 * @return L'angle entre les deux pointes de la fl�che, en degres
	 */
	public double getAngleTete() {
		return angleTete;
	}

	/**
	 * Modifie l'angle entre les deux traits formant la tete de la fleche 
	 * @param angleTete Angle entre les deux traits formant la tete de la fleche, en degres 
	 */
	public void setAngleTete(double angleTete) {
		this.angleTete = angleTete;
		creerLaGeometrie();
	}

	/**
	 * Retourne la longueur du segment utilis� pour tracer la tete de la fl�che 
	 * @return Longueur du segment
	 */
	public double getLongueurTraitDeTete() {
		return longueurTraitDeTete;
	}

	/**
	 * Modifie la longueur du segment utilis� pour tracer la tete de la fl�che 
	 * @param longueurTete longueur du segment de tete
	 */
	public void setLongueurTraitDeTete(double longueurTete) {
		this.longueurTraitDeTete = longueurTete;
		creerLaGeometrie();
	}

	/**
	 * Modifie de facteur mutiplicatif permettant de passer
	 * des metres aux pixels
	 * @param pixelsParMetre facteur mutiplicatif permettant de passer des metres aux pixels
	 */
	public void setPixelsParMetre(double pixelsParMetre) {
		this.pixelsParMetre = pixelsParMetre;
	}


}

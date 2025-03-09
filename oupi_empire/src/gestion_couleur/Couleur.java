package gestion_couleur;

import java.awt.Color;

/**
 * Classe permettant de gérer les couleurs (toString, .equals, et d'autres petites méthodes
 * La couleur blanche et la noire sont exclues de cette liste car elles ont des propiétées spéciales
 * 0 = bleu, 1 = rouge, 2 = cyan, 3 = darck Gray, 4 = gris, 5 = vert, 6 = magenta, 7 = rose, 8 = jaune, 9 = orange
 * 
 * @author Badr Rifki
 */
public class Couleur {



	/** couleur de base*/
	public final static Color COULEUR_DE_BASE = new Color(138, 134, 93);

	/** les noms des couleurs*/
	public static final String[] COULEUR_NOMS = {"Bleu", "Rouge", "Cyan", "Gris foncé",
			"Gris", "Vert", "Magenta", "Rose", "Jaune", "Orange", "Couleur de base"};

	/** La couleur des éléments 0 = bleu, 1 = rouge, 2 = cyan, 3 = darck Gray, 4 = gris,
	 *  5 = vert, 6 = magenta, 7 = rose, 8 = jaune, 9 = orange, 10 = couleur de base*/
	public static final Color[] COULEUR_ELEMENTS = {Color.blue,Color.red,Color.cyan,Color.darkGray,
			Color.gray,Color.green,Color.magenta,Color.PINK,Color.yellow,Color.ORANGE,COULEUR_DE_BASE};

	/** la couleur en Color*/
	private Color couleur;

	/** la valeur de la couleur en int */
	private int valeurEnInt;

	/** La position de la couleur de base*/
	static public final int COULEUR_DE_BASE_N = 10;


	/**
	 * Méthode pour créer une couleur (en francais)
	 * @param n  L'indice de la couleur choisie (0-9)
	 *  0 = bleu, 1 = rouge, 2 = cyan, 3 = darck Gray, 4 = gris, 
	 *  5 = vert, 6 = magenta, 7 = rose, 8 = jaune, 9 = orange
	 */
	
	public Couleur (int n) {
		valeurEnInt = n;
		couleur = COULEUR_ELEMENTS [n];
	}

	/**
	 * Sert a donner la position de la couleur dans la liste
	 * 
	 * @return La position de la pichenote dans la liste des couleurs
	 */
	
	public int getValeurEnInt () {
		return valeurEnInt;
	}
	/**
	 * constructeur pour créer une couleur en fonction d'une autre
	 * 
	 * @param couleur la couleur en question
	 */

	public Couleur (Color couleur) {
		this.couleur = couleur;
		if(couleur.equals(Color.white)) {
			valeurEnInt = 11;
		}else {
			if(couleur.equals(Color.black)) {
				valeurEnInt = 12;
			}
		}
	}

	/**
	 * Méthode pour retourner la couleur de la couleur
	 * 
	 * @return la couleur de la couleur
	 */
	
	public Color getCouleur() {
		return couleur;
	}

	/**
	 * permet les test entre 2 objets Couleur ( définit comme il serait par défaut
	 * 
	 * @param couleur la couleur avec lequel on le compart
	 * 
	 * @return Vrai si la couleur est la même
	 */
	public boolean equals (Couleur couleur) {
		return this.couleur.equals(couleur.getCouleur());
	}

	/**
	 * Permet de tester avec des objets qui sont directement des Color
	 * 
	 * @param color la couleur que l'on désire tester
	 * @return
	 */
	
	public boolean equals (Color color) {
		return this.couleur.equals(color);
	}

	/**
	 * 
	 */
	/**
	 * Méthode pour créer une couleur (en francais)
	 * @param n  L'indice de la couleur choisie (0-9)
	 *  0 = bleu, 1 = rouge, 2 = cyan, 3 = darck Gray, 4 = gris, 
	 *  5 = vert, 6 = magenta, 7 = rose, 8 = jaune, 9 = orange
	 */
	public String toString() {
		String representation = "";
		switch (valeurEnInt) {
		case 11:
			representation = "blanche";
			break;
		case 12:
			representation = "noire";
			break;
		default: 
			if(valeurEnInt<11) {
				representation = COULEUR_NOMS[valeurEnInt];
			}else {
				representation = "erreur dans la couleur de la pichenotte";
			}

		}
		return representation;

	}
}

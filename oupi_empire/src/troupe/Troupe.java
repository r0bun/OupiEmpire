package troupe;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import interfaces.Dessinable;
import jeu_oupi.JeuxOupi;
import plateau.Tuile;

/**
 * La classe {@code Troupe} représente une troupe dans le jeu Oupi. Elle
 * implémente l'interface {@link Dessinable} pour permettre le dessin de la
 * troupe.
 * 
 * @author Badr Rifki
 * @author Loic Simard
 */
public class Troupe implements Dessinable {
    protected BufferedImage image;
    private int x, y;
    private int col, lig, preCol, preLig;
    private boolean selectionne;
    public boolean remplie;
    
    private int bakDistParc = 0;
    private int distanceParcourable = 0;
    
    // Variables pour garder la position centrale quand tuilesSelec a été créé
    private int centreCol, centreLig;
    
    private Tuile[][] tuilesSelec;
    
    protected int HP,attaque,defense,vitesse,endurance;
    
    private int equipe;

	private static int equipeActuelle = 0;

	/**
	 * 
	 * @param equipe
	 */
	public void setEquipe(int equipe) {
		this.equipe = equipe;
	}

	public int getEquipe() {
		return equipe;
	}

	/**
	 * Constructeur de la classe {@code Troupe}.
	 * 
	 * @param col la colonne initiale de la troupe
	 * @param lig la ligne initiale de la troupe
	 */
	public Troupe(int lig, int col) {
		this.col = col;
		this.lig = lig;
		x = getX(col);
		y = getY(lig);
		preCol = col;
		preLig = lig;
		selectionne = false;
		
		HP= 100;
		attaque= 20;
		defense= 10;
		vitesse=20;
		endurance=30;
	}

	private void initialiserTuilesSelec() {
		int taille = 2 * bakDistParc + 1; // Zone autour de la troupe
		tuilesSelec = new Tuile[taille][taille];

		// Sauvegarde de la position centrale actuelle
		centreCol = col;
		centreLig = lig;

		for (int i = 0; i < taille; i++) {
			for (int j = 0; j < taille; j++) {
				int ligne = lig - bakDistParc + i;
				int colonne = col - bakDistParc + j;

				// Vérifier si la position est dans le losange
				if (Math.abs(i - bakDistParc) + Math.abs(j - bakDistParc) <= bakDistParc && ligne >= 0
						&& ligne < JeuxOupi.getNbTuiles() && colonne >= 0 && colonne < JeuxOupi.getNbTuiles()) {
					if (JeuxOupi.plateau.getTuile(ligne, colonne).estOccupee()) {
						tuilesSelec[i][j] = null;
					} else {
						tuilesSelec[i][j] = JeuxOupi.plateau.getTuile(ligne, colonne);
					}
				} else {
					tuilesSelec[i][j] = null; // En dehors du plateau ou en dehors du losange
				}

			}
		}
	}

	/**
	 * Retourne l'image de la troupe.
	 * 
	 * @return l'image de la troupe
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * Définit l'image de la troupe.
	 * 
	 * @param image l'image de la troupe
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
	}

	private int getY(int lig) {
		return lig * JeuxOupi.tailleTuile;
	}

	private int getX(int col) {
		return col * JeuxOupi.tailleTuile;
	}

	/**
	 * Charge une image à partir du chemin spécifié.
	 * 
	 * @param imagePath le chemin de l'image
	 * @return l'image chargée
	 */
	protected BufferedImage getImage(String imagePath) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	/**
	 * Dessine la troupe.
	 * 
	 * @param g2d l'objet {@link Graphics2D} utilisé pour dessiner
	 */
	@Override
	public void dessiner(Graphics2D g2d) {
		Graphics2D g2dPrive = (Graphics2D) g2d.create();
		
		Color couleur = new Color(0,0,0);

		// Dessiner un contour si la troupe est sélectionnée
		if (equipe == equipeActuelle) {
			couleur = Color.GREEN;
		} else {
			couleur = Color.RED;
		}
		g2dPrive.setColor(couleur);
		
		if (selectionne) {
			g2dPrive.drawRect(x, y, JeuxOupi.tailleTuile, JeuxOupi.tailleTuile);

			// TODO g2dPrive.draw(zoneDeplacement);
		}
		// g2dPrive.setColor(new Color(0,0,255,50));
		couleur = new Color(couleur.getRed(), couleur.getGreen(), couleur.getBlue(), 50);
		g2dPrive.setColor(couleur);
		g2dPrive.fillRect(x, y, JeuxOupi.tailleTuile, JeuxOupi.tailleTuile);
		g2dPrive.drawImage(image, x, y, JeuxOupi.tailleTuile, JeuxOupi.tailleTuile, null);
	}

	/**
	 * Vérifie si la troupe est sélectionnée.
	 * 
	 * @return {@code true} si la troupe est sélectionnée, {@code false} sinon
	 */
	public boolean estSelectionne() {
		return selectionne;
	}

	/**
	 * Définit l'état de sélection de la troupe.
	 * 
	 * @param selectionne l'état de sélection de la troupe
	 */
	public void setSelectionne(boolean selectionne) {
		this.selectionne = selectionne;
		if (selectionne) {
			initialiserTuilesSelec();
			printTuilesSelec();
			distanceParcourable = bakDistParc; // Réinitialiser la distance parcourable
		}
	}

	private void printTuilesSelec() {
		int taille = tuilesSelec.length;
		System.out.println("Tableau des tuiles sélectionnables (distance max: " + bakDistParc + "):");
		for (int i = 0; i < taille; i++) {
			for (int j = 0; j < taille; j++) {
				if (i == bakDistParc && j == bakDistParc) {
					System.out.print(" [X]  "); // Position de la troupe
				} else if (tuilesSelec[i][j] != null) {
					System.out.printf("(%d,%d) ", i, j); // Tuile valide
				} else {
					System.out.print(" [#]  "); // Hors plateau
				}
			}
			System.out.println();
		}
	}

	/**
	 * Déplace la troupe vers le haut.
	 */
	public void deplacerHaut() {
		int nouvelleLigne = lig - 1;
		if (distanceParcourable > 0 && estDansLimites(nouvelleLigne, col)) {
			preLig = lig;
			lig = nouvelleLigne;
			y = getY(lig);
		}
	}

	/**
	 * Déplace la troupe vers le bas.
	 */
	public void deplacerBas() {
		int nouvelleLigne = lig + 1;
		if (distanceParcourable > 0 && estDansLimites(nouvelleLigne, col)) {
			preLig = lig;
			lig = nouvelleLigne;
			y = getY(lig);
		}
	}

	/**
	 * Déplace la troupe vers la gauche.
	 */
	public void deplacerGauche() {
		int nouvelleColonne = col - 1;
		if (distanceParcourable > 0 && estDansLimites(lig, nouvelleColonne)) {
			preCol = col;
			col = nouvelleColonne;
			x = getX(col);
		}
	}

	/**
	 * Déplace la troupe vers la droite.
	 */
	public void deplacerDroite() {
		int nouvelleColonne = col + 1;
		if (distanceParcourable > 0 && estDansLimites(lig, nouvelleColonne)) {
			preCol = col;
			col = nouvelleColonne;
			x = getX(col);
		}
	}
	
	public void confirmerMouv() {
		selectionne = false;
		
	}

	private boolean estDansLimites(int ligne, int colonne) {
		// Calculer les indices dans tuilesSelec par rapport à la position centrale
		// initiale
		int i = ligne - (centreLig - bakDistParc);
		int j = colonne - (centreCol - bakDistParc);

		// Vérifier si le déplacement est dans les limites du tableau et que la tuile
		// existe
		return i >= 0 && i < tuilesSelec.length && j >= 0 && j < tuilesSelec[0].length && tuilesSelec[i][j] != null;
	}

	/**
	 * Vérifie si la troupe est à la position spécifiée.
	 * 
	 * @param clickX la position x du clic
	 * @param clickY la position y du clic
	 * @return {@code true} si la troupe est à la position spécifiée, {@code false}
	 *         sinon
	 */
	public boolean estA(int clickX, int clickY) {
		int troupeX = x;
		int troupeY = y;
		return clickX >= troupeX && clickX < troupeX + JeuxOupi.tailleTuile && clickY >= troupeY
				&& clickY < troupeY + JeuxOupi.tailleTuile;
	}

	/**
	 * Retourne la distance parcourable par la troupe.
	 * 
	 * @return la distance parcourable
	 */
	public int getDistanceParcourable() {
		return distanceParcourable;
	}

	/**
	 * Définit la distance parcourable par la troupe.
	 * 
	 * @param distanceParcourable la distance parcourable
	 */
	public void setDistanceParcourable(int distanceParcourable) {
		this.distanceParcourable = distanceParcourable;
	}

	/**
	 * Retourne la distance parcourable initiale de la troupe.
	 * 
	 * @return la distance parcourable initiale
	 */
	public int getBakDistParc() {
		return bakDistParc;
	}

	/**
	 * Définit la distance parcourable initiale de la troupe.
	 * 
	 * @param bakDistParc la distance parcourable initiale
	 */
	public void setBakDistParc(int bakDistParc) {
		this.bakDistParc = bakDistParc;
	}

	/**
	 * Retourne la colonne de la troupe.
	 * 
	 * @return la colonne de la troupe
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Retourne la ligne de la troupe.
	 * 
	 * @return la ligne de la troupe
	 */
	public int getLig() {
		return lig;
	}

	/**
	 * Définit la colonne de la troupe.
	 * 
	 * @param col la nouvelle colonne de la troupe
	 */
	public void setCol(int col) {
		this.col = col;
		x = getX(col);
	}

	/**
	 * Définit la ligne de la troupe.
	 * 
	 * @param lig la nouvelle ligne de la troupe
	 */
	public void setLig(int lig) {
		this.lig = lig;
		y = getY(lig);
	}

	/**
	 * Change l'equipe qui est en train de jouer
	 */
	public static void toggleEquipeActuelle() {
		if (equipeActuelle == 0) {
			equipeActuelle = 1;
		} else {
			equipeActuelle = 0;
		}
	}
	/*
	 * TODO private Path2D.Double creerZoneDeplacement(){ Path2D.Double
	 * zoneDeplacement = null; zoneDeplacement.moveTo(tuileSelec, y);
	 * 
	 * return zoneDeplacement; }
	 */

	public void deselec() {
		selectionne = false;
	}
	
	public void attaquer(Troupe troupeEnem) {
		// a implementer
	}

	
}
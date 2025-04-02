package troupe;

import interfaces.Dessinable;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
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
    
    private boolean epuisee;
    private int equipe;
        
    private int bakDistParc = 0;
    private int distanceParcourable = 0;
    
    // Variables pour garder la position centrale quand tuilesSelec a été créé
    private int centreCol, centreLig;
    
    private Tuile[][] tuilesSelec;
    
    protected int HP,attaque,defense,vitesse,endurance;
    
    // Distance d'attaque (1 = corps à corps par défaut)
    protected int distanceAttaque = 1;
    
	private static int equipeActuelle = 0;
	
	// Référence à l'instance de JeuxOupi
	private JeuxOupi jeu;

    // Variables pour l'animation de sautillement
    private int bounceSize = 0;
    private boolean bounceGrowing = true;

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
	 * @param lig la ligne initiale de la troupe
	 * @param col la colonne initiale de la troupe
	 * @param jeu l'instance du jeu à laquelle appartient cette troupe
	 */
	public Troupe(int lig, int col, JeuxOupi jeu) {
		this.col = col;
		this.lig = lig;
		this.jeu = jeu;
		// Calculer la position en pixels basée sur la taille de tuile
		updatePosition();
		preCol = col;
		preLig = lig;
		selectionne = false;
		
		HP= 100;
		attaque= 20;
		defense= 10;
		vitesse=20;
		endurance=30;
        distanceAttaque = 1; // Distance d'attaque par défaut (corps à corps)
	}
	
	/**
	 * Met à jour la position en pixels basée sur les coordonnées de la grille
	 */
	private void updatePosition() {
		x = getX(col);
		y = getY(lig);
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
						&& ligne < jeu.getNbTuiles() && colonne >= 0 && colonne < jeu.getNbTuiles()) {
					Tuile tuile = jeu.getPlateau().getTuile(ligne, colonne);
					if (tuile.estOccupee() && !(ligne == lig && colonne == col)) {
						tuilesSelec[i][j] = null;
					} else {
						tuilesSelec[i][j] = tuile;
						// Marquer la tuile comme accessible pour le rendu visuel
						tuile.setAccessible(true);
					}
				} else {
					tuilesSelec[i][j] = null; // En dehors du plateau ou en dehors du losange
				}
			}
		}
	}
	
	/**
	 * Efface le marquage d'accessibilité de toutes les tuiles
	 */
	private void effacerZoneAccessible() {
		if (tuilesSelec != null) {
			int taille = tuilesSelec.length;
			for (int i = 0; i < taille; i++) {
				for (int j = 0; j < taille; j++) {
					if (tuilesSelec[i][j] != null) {
						tuilesSelec[i][j].setAccessible(false);
					}
				}
			 }
			// Pour s'assurer que les références sont libérées correctement
			tuilesSelec = null;
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
		return lig * jeu.getTailleTuile();
	}

	private int getX(int col) {
		return col * jeu.getTailleTuile();
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
			image = ImageIO.read(getClass().getResourceAsStream(imagePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	/**
	 * Dessine la troupe avec une animation de sautillement.
	 * 
	 * @param g2d l'objet {@link Graphics2D} utilisé pour dessiner
	 */
	@Override
	public void dessiner(Graphics2D g2d) {
		Graphics2D g2dPrive = (Graphics2D) g2d.create();

		// Calculer la taille dynamique pour l'animation
		int dynamicSize = jeu.getTailleTuile() + bounceSize;
		int offset = (jeu.getTailleTuile() - dynamicSize) / 2;

		// Dessiner l'image de la troupe avec la taille dynamique
		g2dPrive.drawImage(image, x + offset, y + offset, dynamicSize, dynamicSize, null);

		g2dPrive.dispose();
	}

	/**
	 * Met à jour l'animation de sautillement.
	 */
	public void updateBounceAnimation() {
		if (bounceGrowing) {
			bounceSize += 2;
			if (bounceSize >= 10) { // Taille maximale de l'agrandissement
				bounceGrowing = false;
			}
		} else {
			bounceSize -= 2;
			if (bounceSize <= 0) { // Taille minimale de la réduction
				bounceGrowing = true;
			}
		}
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
		} else {
			effacerZoneAccessible();
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
		return clickX >= troupeX && clickX < troupeX + jeu.getTailleTuile() && clickY >= troupeY
				&& clickY < troupeY + jeu.getTailleTuile();
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

	public void deselec() {
		selectionne = false;
		effacerZoneAccessible();
		// Libérer les références pour éviter les fuites mémoire
		tuilesSelec = null;
	}
	
	public void attaquer(Troupe troupeEnem) {
		// Vérifier que les troupes sont d'équipes différentes
		if (this.equipe == troupeEnem.getEquipe()) {
			System.out.println("⚠️ Impossible d'attaquer une troupe alliée!");
			return;
		}
		
        // Calcul de la distance entre les troupes
        int distance = Math.abs(this.getCol() - troupeEnem.getCol()) + Math.abs(this.getLig() - troupeEnem.getLig());
        
        // Vérifier que la troupe est à portée d'attaque
        if (distance > distanceAttaque) {
            System.out.println("⚠️ Cible hors de portée! Distance maximale d'attaque: " + distanceAttaque);
            return;
        }
        
		// Calcul des dégâts infligés (formule simple inspirée de Fire Emblem)
		int degats = Math.max(1, this.attaque - troupeEnem.defense / 2);
		
		// Application des dégâts
		troupeEnem.HP = Math.max(0, troupeEnem.HP - degats);
		
		System.out.println("🗡️ " + this.getClass().getSimpleName() + " attaque et inflige " + degats + " points de dégâts!");
		System.out.println("   " + troupeEnem.getClass().getSimpleName() + " a maintenant " + troupeEnem.HP + " HP.");
		
		// Vérifier si la troupe ennemie est vaincue
		if (troupeEnem.HP <= 0) {
			System.out.println("💀 " + troupeEnem.getClass().getSimpleName() + " a été vaincu!");
			// Ici on pourrait ajouter une logique pour retirer la troupe du jeu
		} else {
			// Contre-attaque si la troupe ennemie est encore en vie
			// La vitesse détermine si une contre-attaque est possible (comme dans Fire Emblem)
			if (troupeEnem.vitesse >= this.vitesse - 5) {
				int degatsContre = Math.max(1, troupeEnem.attaque - this.defense / 2);
				this.HP = Math.max(0, this.HP - degatsContre);
				
				System.out.println("⚔️ " + troupeEnem.getClass().getSimpleName() + " contre-attaque et inflige " + degatsContre + " points de dégâts!");
				System.out.println("   " + this.getClass().getSimpleName() + " a maintenant " + this.HP + " HP.");
				
				// Vérifier si l'attaquant est vaincu par la contre-attaque
				if (this.HP <= 0) {
					System.out.println("💀 " + this.getClass().getSimpleName() + " a été vaincu!");
					// Ici on pourrait ajouter une logique pour retirer la troupe du jeu
				}
			} else {
				System.out.println("🛡️ " + troupeEnem.getClass().getSimpleName() + " est trop lent pour contre-attaquer.");
			}
		}
		
		// Réduction de l'endurance après l'attaque
		this.endurance = Math.max(0, this.endurance - 5);
		System.out.println("⚡ Endurance de " + this.getClass().getSimpleName() + " réduite à " + this.endurance);
	}

	public int getHP() {
		return HP;
	}

	public boolean isEpuisee() {
		return epuisee;
	}

	public void setEpuisee(boolean epuisee) {
		this.epuisee = epuisee;
	}

	public int getAttaque() {
		return attaque;
	}

	public void setAttaque(int attaque) {
		this.attaque = attaque;
	}

	public int getDefense() {
		return defense;
	}

	public void setDefense(int defense) {
		this.defense = defense;
	}

	public int getVitesse() {
		return vitesse;
	}

	public void setVitesse(int vitesse) {
		this.vitesse = vitesse;
	}

	public int getEndurance() {
		return endurance;
	}

	public void setEndurance(int endurance) {
		this.endurance = endurance;
	}

	public void setHP(int hP) {
		HP = hP;
	}
	
	/**
	 * Obtient l'instance du jeu associée à cette troupe
	 * 
	 * @return l'instance de JeuxOupi
	 */
	public JeuxOupi getJeu() {
		return jeu;
	}
	
	/**
	 * Obtient la distance d'attaque de la troupe
	 * 
	 * @return la distance d'attaque
	 */
	public int getDistanceAttaque() {
		return distanceAttaque;
	}
	
	/**
	 * Définit la distance d'attaque de la troupe
	 * 
	 * @param distanceAttaque la nouvelle distance d'attaque
	 */
	public void setDistanceAttaque(int distanceAttaque) {
		this.distanceAttaque = distanceAttaque;
	}
}
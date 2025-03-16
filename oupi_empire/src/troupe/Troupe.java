package troupe;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import interfaces.Dessinable;
import jeu_oupi.JeuxOupi;
import plateau.Tuile;

/**
 * La classe {@code Troupe} représente une troupe dans le jeu Oupi.
 * Elle implémente l'interface {@link Dessinable} pour permettre le dessin de la troupe.
 * 
 * @author Badr Rifki
 */
public class Troupe implements Dessinable {
    
    protected BufferedImage image;
    private int x, y;
    private int col, lig, preCol, preLig;
    private boolean selectionne;
    
    private int bakDistParc = 0;
    private int distanceParcourable = 0;
    
    /*
    private int movsUp=0;
    private int movsDown=0;
    private int movsLeft=0;
    private int movsRight=0;
    */
    
    private Tuile[][] tuilesSelec;

    /**
     * Constructeur de la classe {@code Troupe}.
     * 
     * @param col la colonne initiale de la troupe
     * @param lig la ligne initiale de la troupe
     */
    public Troupe(int col, int lig) {
        this.col = col;
        this.lig = lig;
        x = getX(col);
        y = getY(lig);
        preCol = col;
        preLig = lig;
        selectionne = false;
    
        
    }

    private void initialiserTuilesSelec() {
    	int taille = 2 * bakDistParc + 1; // Zone autour de la troupe
        tuilesSelec = new Tuile[taille][taille];

        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                int ligne = lig - bakDistParc + i;
                int colonne = col - bakDistParc + j;

                if (ligne >= 0 && ligne < JeuxOupi.getNbTuiles() &&
                    colonne >= 0 && colonne < JeuxOupi.getNbTuiles()) {
                    tuilesSelec[i][j] = JeuxOupi.plateau.getTuile(ligne, colonne);
                } else {
                    tuilesSelec[i][j] = null; // En dehors du plateau
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

    private int getY(int col) {
        return col * JeuxOupi.tailleTuile;
    }

    private int getX(int lig) {
        return lig * JeuxOupi.tailleTuile;
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
        
        // Dessiner un contour si la troupe est sélectionnée
        if (selectionne) {
            g2dPrive.setColor(Color.GREEN);
            g2dPrive.drawRect(x, y, JeuxOupi.tailleTuile, JeuxOupi.tailleTuile);
        }
        
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
        initialiserTuilesSelec();
        printTuilesSelec();
    }

    private void printTuilesSelec() {
    	int taille = tuilesSelec.length;
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                int ligne = lig - bakDistParc + i;
                int colonne = col - bakDistParc + j;

                if (i == bakDistParc && j == bakDistParc) {
                    System.out.print(" X  "); // Position de la troupe
                } else if (tuilesSelec[i][j] != null) {
                    System.out.printf("(%d,%d) ", ligne, colonne); // Coordonnées réelles sur le plateau
                } else {
                    System.out.print(" #   "); // Hors plateau
                }
            }
            System.out.println();
        }
	}

	/**
     * Déplace la troupe vers le haut.
     * 
     * @param minusMov si true, diminue la distance parcourable
     */
    public void deplacerHaut() {
            int nouvelleLigne = lig - 1;
            if (estDansLimites(nouvelleLigne, col)) { // Vérification avant déplacement
                preLig = lig;
                lig = nouvelleLigne;
                y = getY(lig);
            
        }
    }

    /**
     * Déplace la troupe vers le bas.
     * 
     * @param minusMov si true, diminue la distance parcourable
     */
    public void deplacerBas() {
            int nouvelleLigne = lig + 1;
            if (estDansLimites(nouvelleLigne, col)) {
                preLig = lig;
                lig = nouvelleLigne;
                y = getY(lig);
            
        }
    }

    /**
     * Déplace la troupe vers la gauche.
     * 
     * @param minusMov si true, diminue la distance parcourable
     */
    public void deplacerGauche() {
    	
            int nouvelleColonne = col - 1;
            if (estDansLimites(lig, nouvelleColonne)) {
                preCol = col;
                col = nouvelleColonne;
                x = getX(col);
            
        }
    }

    /**
     * Déplace la troupe vers la droite.
     * 
     * @param minusMov si true, diminue la distance parcourable
     */
    public void deplacerDroite() {
    	
            int nouvelleColonne = col + 1;
            if (estDansLimites(lig, nouvelleColonne)) {
                preCol = col;
                col = nouvelleColonne;
                x = getX(col);
            
        }
    }

    private boolean estDansLimites(int ligne, int colonne) {
    	int i = ligne - (lig - bakDistParc);
        int j = colonne - (col - bakDistParc);

        return i >= 0 && i < tuilesSelec.length && 
               j >= 0 && j < tuilesSelec[0].length &&
               tuilesSelec[i][j] != null; // Vérifie si la tuile est valide
	}

	/**
     * Vérifie si la troupe est à la position spécifiée.
     * 
     * @param clickX la position x du clic
     * @param clickY la position y du clic
     * @return {@code true} si la troupe est à la position spécifiée, {@code false} sinon
     */
    public boolean estA(int clickX, int clickY) {
        int troupeX = x;
        int troupeY = y;
        return clickX >= troupeX && clickX < troupeX + JeuxOupi.tailleTuile &&
               clickY >= troupeY && clickY < troupeY + JeuxOupi.tailleTuile;
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
}
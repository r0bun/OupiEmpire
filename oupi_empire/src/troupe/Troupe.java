package troupe;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import interfaces.Dessinable;
import jeu_oupi.JeuxOupi;

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
    }

    /**
     * Déplace la troupe vers le haut.
     */
    public void deplacerHaut() {
        if (distanceParcourable != 0) {
            if (lig > 0) {
                preLig = lig;
                lig--;
                y = getY(lig);
            }
            distanceParcourable -= 1;
        }
    }

    /**
     * Déplace la troupe vers le bas.
     */
    public void deplacerBas() {
        if (distanceParcourable != 0) {
            if (lig < JeuxOupi.getNbTuiles() - 1) {
                preLig = lig;
                lig++;
                y = getY(lig);
            }
            distanceParcourable -= 1;
        }
    }

    /**
     * Déplace la troupe vers la gauche.
     */
    public void deplacerGauche() {
        if (distanceParcourable != 0) {
            if (col > 0) {
                preCol = col;
                col--;
                x = getX(col);
            }
            distanceParcourable -= 1;
        }
    }

    /**
     * Déplace la troupe vers la droite.
     */
    public void deplacerDroite() {
        if (distanceParcourable != 0) {
            if (col < JeuxOupi.getNbTuiles() - 1) {
                preCol = col;
                col++;
                x = getX(col);
            }
            distanceParcourable -= 1;
        }
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
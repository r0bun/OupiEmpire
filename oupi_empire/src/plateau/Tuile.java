package plateau;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * La classe {@code Tuile} représente une tuile sur le plateau de jeu.
 * Elle contient des informations sur la position, la taille, l'état d'occupation et la couleur de la tuile.
 * 
 * @author Badr Rifki
 */
public abstract class Tuile {
	protected int x, y; // Position de la tuile sur le plateau
    protected int taille; // Taille de la tuile
    protected boolean occupee; // Indique si la tuile est occupée
    protected Color couleur; // Couleur de la tuile
    protected BufferedImage texture;
    protected TerrainObstacle obstacle;

    

	/**
     * Constructeur de la classe {@code Tuile}.
     * 
     * @param x la position x de la tuile
     * @param y la position y de la tuile
     * @param taille la taille de la tuile
     * @param couleur la couleur de la tuile
     */
    public Tuile(int x, int y, int taille, Color couleur) {
        this.x = x;
        this.y = y;
        this.taille = taille;
        this.couleur = couleur;
        this.occupee = false;
    }

    /**
     * Dessine la tuile.
     * 
     * @param g2d l'objet {@link Graphics2D} utilisé pour dessiner
     */
//    public void dessiner(Graphics2D g2d) {
//        Graphics2D g2dPrive = (Graphics2D) g2d.create();
//        g2dPrive.setColor(couleur);
//        //g2dPrive.fillRect(x, y, taille, taille);
//        g2dPrive.setColor(Color.BLACK); // Contour de la tuile
//        g2dPrive.drawImage(texture, null, x, y);
//        //g2dPrive.drawRect(x, y, taille, taille);
//    }
    
    public void dessiner(Graphics2D g2d) {
        if (texture == null) {
            // Dessiner un rectangle de couleur si pas de texture
            Graphics2D g2dPrive = (Graphics2D) g2d.create();
            g2dPrive.setColor(couleur);
            g2dPrive.fillRect(x, y, taille, taille);
            g2dPrive.setColor(Color.BLACK);
            g2dPrive.drawRect(x, y, taille, taille);
            g2dPrive.dispose();
            return;
        }
        
        // Créer une copie locale du contexte graphique
        Graphics2D g2dPrive = (Graphics2D) g2d.create();
        
        // Définir la forme dans laquelle l'image sera confinée
        Shape shape = new Rectangle(x, y, taille, taille);
        
        // Sauvegarder le clip actuel
        Shape oldClip = g2dPrive.getClip();
        
        try {
            // Appliquer la forme comme zone de clipping
            g2dPrive.clip(shape);
            
            // Dessiner l'image dans la zone de clipping
            g2dPrive.drawImage(texture, x, y, taille, taille, null);
            
            // Dessiner le contour de la forme si nécessaire
            g2dPrive.setClip(oldClip);
            g2dPrive.setColor(Color.DARK_GRAY);
            g2dPrive.draw(shape);
        } finally {
            // Restaurer le clip original et libérer les ressources
            g2dPrive.dispose();
        }
    }


    /**
     * Vérifie si la tuile est occupée.
     * 
     * @return {@code true} si la tuile est occupée, {@code false} sinon
     */
    public boolean estOccupee() {
        return occupee;
    }

    /**
     * Définit l'état d'occupation de la tuile.
     * 
     * @param occupee l'état d'occupation de la tuile
     */
    public void setOccupee(boolean occupee) {
        this.occupee = occupee;
    }

    /**
     * Définit la couleur de la tuile.
     * 
     * @param couleur la nouvelle couleur de la tuile
     */
    public void setCouleur(Color couleur) {
        this.couleur = couleur;
    }
    
    public void print() {
    	System.out.println(x+ " "+ y);
    }
    

    protected void setTexture(String imagePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        texture = image;
    }

}

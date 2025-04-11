package plateau;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import tuiles.Rocher;

/**
 * La classe {@code Tuile} représente une tuile sur le plateau de jeu. Elle
 * contient des informations sur la position, la taille, l'état d'occupation et
 * la couleur de la tuile.
 *
 * @author Badr Rifki
 */
public abstract class Tuile {

    protected int x, y; // Position de la tuile sur le plateau
    protected int taille; // Taille de la tuile
    protected boolean occupee; // Indique si la tuile est occupée
    protected boolean posDep;
    protected boolean accessible; // Indique si la tuile est accessible par la troupe sélectionnée
    protected boolean placable;
    protected Color couleur; // Couleur de la tuile
    protected BufferedImage texture;
    protected TerrainObstacle obstacle;
	protected String nomTuile; //Utilise pour trouver le fichier image
    protected final String PATH_TUILE = "/tuilesTexture/";
    protected int lig, col;

    /**
     * Constructeur de la classe {@code Tuile}.
     *
     * @param x la position x de la tuile
     * @param y la position y de la tuile
     * @param taille la taille de la tuile
     * @param couleur la couleur de la tuile
     */
    public Tuile(int x, int y, int taille, Color couleur, int lig, int col) {
        this.x = x;
        this.y = y;
        this.taille = taille;
        this.couleur = couleur;
        this.occupee = false;
        this.posDep = false;
        this.accessible = false;
        this.lig = lig;
        this.col = col;
    }

    /**
     * Dessine la tuile.
     *
     * @param g2d l'objet {@link Graphics2D} utilisé pour dessiner
     */
    public void dessiner(Graphics2D g2d) {
        // Créer une copie locale du contexte graphique
        Graphics2D g2dPrive = (Graphics2D) g2d.create();

        // Définir la forme dans laquelle l'image sera confinée
        Shape shape = new Rectangle(x, y, taille, taille);

        // Sauvegarder le clip actuel
        Shape oldClip = g2dPrive.getClip();

        try {
            // Appliquer la forme comme zone de clipping
            g2dPrive.clip(shape);

            // Dessiner l'image de fond (texture) dans tous les cas
            if (texture != null) {
                g2dPrive.drawImage(texture, x, y, taille, taille, null);
            } else {
                // Si pas de texture, utiliser la couleur de fond
                g2dPrive.setColor(couleur);
                g2dPrive.fillRect(x, y, taille, taille);
            }

            // Draw obstacle if present
            if (obstacle != null) {
                obstacle.dessiner(g2dPrive, x, y, taille);
            }
            
            // Si la tuile est accessible par la troupe sélectionnée
            if (accessible || placable) {
                // Sauvegarder le composite original
                Composite originalComposite = g2dPrive.getComposite();
                
                // Appliquer un effet de surbrillance (vert clair semi-transparent)
                g2dPrive.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                if(accessible) {
                	g2dPrive.setColor(new Color(100, 255, 100)); // Vert clair
                } else {
                	g2dPrive.setColor(new Color(255, 100, 100));
                }
                g2dPrive.fillRect(x, y, taille, taille);
                
                // Restaurer le composite original
                g2dPrive.setComposite(originalComposite);
            }

            // Si la tuile est la position de départ d'une troupe sélectionnée
            if (posDep) {
                // Ajouter une bordure cyan pour indiquer la position de départ
                g2dPrive.setColor(new Color(0, 255, 255, 100)); // Cyan semi-transparent
                g2dPrive.setStroke(new java.awt.BasicStroke(3));
                g2dPrive.drawRect(x + 2, y + 2, taille - 4, taille - 4);
            }

            // Restaurer le clip original
            g2dPrive.setClip(oldClip);
            
            // Dessiner le contour de la tuile
            g2dPrive.setColor(Color.DARK_GRAY);
            g2dPrive.draw(shape);
            
        } finally {
            // Libérer les ressources
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

    public boolean isPosDep() {
        return posDep;
    }

    public void setPosDep(boolean posDep) {
        this.posDep = posDep;
    }
    
    /**
     * Vérifie si la tuile est accessible par la troupe sélectionnée.
     *
     * @return {@code true} si la tuile est accessible, {@code false} sinon
     */
    public boolean isAccessible() {
        return accessible;
    }

    /**
     * Définit si la tuile est accessible par la troupe sélectionnée.
     *
     * @param accessible l'état d'accessibilité de la tuile
     */
    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }

    public boolean isPlacable() {
		return placable;
	}

	public void setPlacable(boolean placable) {
		this.placable = placable;
	}

	/**
     * Définit la couleur de la tuile.
     *
     * @param couleur la nouvelle couleur de la tuile
     */
    public void setCouleur(Color couleur) {
        this.couleur = couleur;
    }

    public String toString() {
        return x + " " + y+" "+occupee;
    }

    protected void setTexture(String imagePath) {
        // Utilisation de TextureManager pour charger la texture
        texture = TextureManager.getInstance().getTexture(imagePath);
    }

    public int getLig() {
        return lig;
    }

    public int getCol() {
        return col;
    }
    
    public void setObstacle(TerrainObstacle obstacle) {
		this.obstacle = obstacle;
	}
    
    public boolean hasObstacle() {
    	return obstacle != null;
    }
    
    // Add debug method
    public String debugInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tuile[").append(lig).append(",").append(col).append("]");
        sb.append(" hasObstacle=").append(hasObstacle());
        if (hasObstacle()) {
            sb.append(" obstacle=").append(obstacle.toString());
        }
        return sb.toString();
    }
    
}

package troupe;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import interfaces.Dessinable;
import jeu_oupi.JeuxOupi;

public class Troupe implements Dessinable {
	
	protected BufferedImage image;
	private int x,y;
	private int col, lig, preCol, preLig;
	private boolean selectionne;
	
	private int bakDistParc=0;
	private int distanceParcourable=0;
	
	public Troupe(int col, int lig) {
		
		this.col = col;
		this.lig= lig;
		x= getX(col);
		y= getY(lig);
		preCol = col;
		preLig= lig;
		selectionne = false;
	}
	

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	private int getY(int col) {
		return col * JeuxOupi.tailleTuile;
	}

	private int getX(int lig) {
		return lig * JeuxOupi.tailleTuile;
	}
	
	protected BufferedImage getImage(String imagePath) {
		
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream(imagePath+".jpg"));
		}catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	public void dessiner(Graphics2D g2d) {
		Graphics2D g2dPrive = (Graphics2D) g2d.create();
		
		// Dessiner un contour si la troupe est sélectionnée
		if (selectionne) {
			g2dPrive.setColor(Color.GREEN);
			g2dPrive.drawRect(x, y, JeuxOupi.tailleTuile, JeuxOupi.tailleTuile);
		}
		
		g2dPrive.drawImage(image, x, y, JeuxOupi.tailleTuile, JeuxOupi.tailleTuile, null);
	}
	
	public boolean estSelectionne() {
		return selectionne;
	}
	
	public void setSelectionne(boolean selectionne) {
		this.selectionne = selectionne;
	}
	
	public void deplacerHaut() {
		if(distanceParcourable != 0) {
		if (lig > 0) {
			preLig = lig;
			lig--;
			y = getY(lig);
		}
		distanceParcourable -= 1;
		}
	}
	
	public void deplacerBas() {
		if(distanceParcourable != 0) {
		if (lig < JeuxOupi.getNbTuiles() - 1) {
			preLig = lig;
			lig++;
			y = getY(lig);
		}
		distanceParcourable -= 1;
		}
	}
	
	public void deplacerGauche() {
		if(distanceParcourable != 0) {
		if (col > 0) {
			preCol = col;
			col--;
			x = getX(col);
		}
		distanceParcourable -= 1;
		}
	}
	
	public void deplacerDroite() {
		if(distanceParcourable != 0) {
		if (col < JeuxOupi.getNbTuiles() - 1) {
			preCol = col;
			col++;
			x = getX(col);
		}
		distanceParcourable -= 1;
		}
	}
	
	public boolean estA(int clickX, int clickY) {
		int troupeX = x;
		int troupeY = y;
		return clickX >= troupeX && clickX < troupeX + JeuxOupi.tailleTuile &&
			   clickY >= troupeY && clickY < troupeY + JeuxOupi.tailleTuile;
	}
	
	public int getDistanceParcourable() {
		return distanceParcourable;
	}

	public void setDistanceParcourable(int distanceParcourable) {
		this.distanceParcourable = distanceParcourable;
	}

	
	public int getBakDistParc() {
		return bakDistParc;
	}


	public void setBakDistParc(int bakDistParc) {
		this.bakDistParc = bakDistParc;
	}

	public int getCol() {
		return col;
	}
	
	public int getLig() {
		return lig;
	}

	public void setCol(int col) {
		this.col = col;
	}


	public void setLig(int lig) {
		this.lig = lig;
	}

	
}

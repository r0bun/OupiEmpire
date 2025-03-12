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
	
	public Troupe(int col, int lig) {
		
		this.col = col;
		this.lig= lig;
		x= getX(col);
		y= getY(lig);
		preCol = col;
		preLig= lig;
		
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
		g2dPrive.drawImage(image,x,y, JeuxOupi.tailleTuile, JeuxOupi.tailleTuile, null);
		
	}
	
	
}

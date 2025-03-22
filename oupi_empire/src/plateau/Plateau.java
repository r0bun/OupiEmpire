package plateau;

import interfaces.Dessinable;
import tuiles.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * La classe {@code Plateau} représente le plateau de jeu composé de tuiles.
 * Elle implémente l'interface {@link Dessinable} pour permettre le dessin du
 * plateau.
 *
 * @author Badr Rifki
 */
public class Plateau implements Dessinable {
	private int lignes, colonnes;
	private int tailleTuile;
	private Tuile[][] tuiles;

	/**
	 * Constructeur de la classe {@code Plateau}.
	 * 
	 * @param lignes      le nombre de lignes du plateau
	 * @param colonnes    le nombre de colonnes du plateau
	 * @param tailleTuile la taille de chaque tuile
	 */
	public Plateau(int lignes, int colonnes, int tailleTuile) {
		this.lignes = lignes;
		this.colonnes = colonnes;
		this.tailleTuile = tailleTuile;
		initialiserTuiles();
	}

	/**
	 * Initialise les tuiles du plateau avec des couleurs alternées pour un effet
	 * damier.
	 */
	private void initialiserTuiles() {
		tuiles = new Tuile[lignes][colonnes];
		for (int ligne = 0; ligne < lignes; ligne++) {
			for (int colonne = 0; colonne < colonnes; colonne++) {
				
				Color couleur = Color.RED;
				tuiles[ligne][colonne] = new Sable(colonne * tailleTuile, ligne * tailleTuile, tailleTuile, couleur,ligne,colonne);
			}
		}
	}

	/**
	 * Dessine le plateau de jeu.
	 * 
	 * @param g2d l'objet {@link Graphics2D} utilisé pour dessiner
	 */
	@Override
	public void dessiner(Graphics2D g2d) {
		Graphics2D g2dPrive = (Graphics2D) g2d.create();
		for (int ligne = 0; ligne < lignes; ligne++) {
			for (int colonne = 0; colonne < colonnes; colonne++) {
				tuiles[ligne][colonne].dessiner(g2dPrive);
			}
		}
	}

	/**
	 * Retourne la tuile à la position spécifiée.
	 * 
	 * @param ligne   la ligne de la tuile
	 * @param colonne la colonne de la tuile
	 * @return la tuile à la position spécifiée
	 */
	public Tuile getTuile(int ligne, int colonne) {
		return tuiles[ligne][colonne];
	}

    public void loadPlateau(String textPath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(textPath))) {
            this.lignes = Integer.parseInt(reader.readLine().trim());
            this.colonnes = Integer.parseInt(reader.readLine().trim());
            this.tailleTuile = Integer.parseInt(reader.readLine().trim());

            this.tuiles = new Tuile[lignes][colonnes];

            for (int ligne = 0; ligne < lignes; ligne++) {
                String rowData = reader.readLine();
                if (rowData == null || rowData.length() < colonnes) {
                    throw new IOException("Format de fichier incorrect: ligne " + ligne + " trop courte");
                }

                for (int colonne = 0; colonne < colonnes; colonne++) {
                    char type = rowData.charAt(colonne);
                    int x = colonne * tailleTuile;
                    int y = ligne * tailleTuile;

                    switch (type) {
                        case 'S':
                            tuiles[ligne][colonne] = new Sable(x, y, tailleTuile, Color.YELLOW, ligne, colonne);
                            break;
                        case 'W':
                            tuiles[ligne][colonne] = new Eau(x, y, tailleTuile, Color.BLUE, ligne, colonne);
                            break;
                        case 'H':
                            tuiles[ligne][colonne] = new Herbe(x, y, tailleTuile, Color.GREEN, ligne, colonne);
                            break;
                        case 'G':
                            tuiles[ligne][colonne] = new Gravier(x, y, tailleTuile, Color.GRAY, ligne, colonne);
                            break;
                        default:
                            tuiles[ligne][colonne] = new Sable(x, y, tailleTuile, Color.RED, ligne, colonne);
                            break;
                    }
                }
            }

            System.out.println("Plateau loaded successfully from: " + textPath);
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading plateau from text file: " + e.getMessage());
            e.printStackTrace();
            initialiserTuiles();
        }
    }
    
	public int getTailleTuile() {
		return tailleTuile;
	}


}

package plateau;

import interfaces.Dessinable;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import tuiles.*;

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

                System.out.println(rowData);
                
                if(rowData.equals("OBS")) {
                    break;
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
                            tuiles[ligne][colonne].setOccupee(true);
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
            
            // Check if we're at the obstacles section
            String line = reader.readLine();
            if (line != null && line.equals("OBS")) {
                // Read obstacles
                String obsLine;
                while ((obsLine = reader.readLine()) != null) {
                    if (obsLine.trim().isEmpty()) continue;
                    String[] obstacle = obsLine.split(" ");
                    if (obstacle.length < 3) {
                        System.err.println("Format d'obstacle incorrect: " + obsLine);
                        continue;
                    }
                    
                    String type = obstacle[0];
                    try {
                        int col = Integer.parseInt(obstacle[1].trim());
                        int lig = Integer.parseInt(obstacle[2].trim());
                        
                        // Verify coordinates are within map bounds
                        if (lig < 0 || lig >= lignes || col < 0 || col >= colonnes) {
                            System.err.println("Coordonnées d'obstacle hors limites: " + obsLine);
                            continue;
                        }
                        
                        int x = col * tailleTuile;
                        int y = lig * tailleTuile;
                        
                        switch (type) {
                            case "rocher":
                                tuiles[lig][col].setObstacle(new Rocher(x, y, tailleTuile, lig, col));
                                tuiles[lig][col].setOccupee(true);
                                break;
                            case "arbre":
                                tuiles[lig][col].setObstacle(new Arbre(x, y, tailleTuile, lig, col));
                                tuiles[lig][col].setOccupee(true);
                                break;
                            case "buisson":
                                tuiles[lig][col].setObstacle(new Buisson(x, y, tailleTuile, lig, col));
                                //tuiles[lig][col].setOccupee(true); Peut aller sur un buisson
                                break;
                            // Add other obstacle types here as needed
                            default:
                                System.err.println("Type d'obstacle inconnu: " + type);
                                break;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Coordonnées d'obstacle invalides: " + obsLine);
                    }
                }
            }
            
            verifyObstacles();
            System.out.println("Plateau loaded successfully from: " + textPath);
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading plateau from text file: " + e.getMessage());
            e.printStackTrace();
            initialiserTuiles();
        }
    }
    
    public void verifyObstacles() {
        System.out.println("Verifying obstacles on the plateau:");
        int obstacleCount = 0;
        for (int ligne = 0; ligne < lignes; ligne++) {
            for (int colonne = 0; colonne < colonnes; colonne++) {
                if (tuiles[ligne][colonne].hasObstacle()) {
                    obstacleCount++;
                    System.out.println("Obstacle found at: (" + ligne + "," + colonne + ")");
                }
            }
        }
        System.out.println("Total obstacles found: " + obstacleCount);
    }
    
	public int getTailleTuile() {
		return tailleTuile;
	}

	/**
	 * Retourne le nombre de lignes du plateau.
	 * 
	 * @return le nombre de lignes
	 */
	public int getLignes() {
		return lignes;
	}
	
	/**
	 * Retourne le nombre de colonnes du plateau.
	 * 
	 * @return le nombre de colonnes
	 */
	public int getColonnes() {
		return colonnes;
	}
}

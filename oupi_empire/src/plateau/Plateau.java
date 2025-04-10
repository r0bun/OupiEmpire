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
    // Variables d'instance
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
                tuiles[ligne][colonne] = new Sable(colonne * tailleTuile, ligne * tailleTuile, tailleTuile, couleur, ligne, colonne);
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
     * Charge le plateau à partir d'un fichier texte.
     * 
     * @param textPath le chemin du fichier texte
     */
    public void loadPlateau(String textPath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(textPath))) {
            this.lignes = Integer.parseInt(reader.readLine().trim());
            this.colonnes = Integer.parseInt(reader.readLine().trim());
            this.tailleTuile = Integer.parseInt(reader.readLine().trim());

            this.tuiles = new Tuile[lignes][colonnes];

            int ligne = 0;
            
            for (; ligne < lignes; ligne++) {
                String rowData = reader.readLine();
                if (rowData == null) {
                    // Si on manque de lignes, remplir le reste avec des tuiles par défaut
                    fillRemainingRows(ligne);
                    break;
                }
                
                if(rowData.equals("OBS")) {
                    // Si on atteint la section des obstacles, arrêter la lecture des données de la carte
                    reader.mark(1000);  // Marquer cette position pour y revenir
                    break;
                }
                
                System.out.println(rowData);
                
                int rowLength = rowData.length();
                
                for (int colonne = 0; colonne < colonnes; colonne++) {
                    // Par défaut, du sable si la colonne est au-delà de ce qui est dans le fichier
                    char type = (colonne < rowLength) ? rowData.charAt(colonne) : 'S';
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
            
            // Remplir les lignes restantes qui n'étaient pas dans le fichier
            if (ligne < lignes) {
                fillRemainingRows(ligne);
            }
            
            // Vérifier si on est à la section des obstacles
            String line = reader.readLine();
            if (line != null && line.equals("OBS")) {
                // Lire les obstacles
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
                        
                        // Vérifier que les coordonnées sont dans les limites de la carte
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
            System.out.println("Plateau chargé avec succès depuis: " + textPath);
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erreur lors du chargement du plateau depuis le fichier texte: " + e.getMessage());
            e.printStackTrace();
            initialiserTuiles();
        }
    }
    
    /**
     * Remplit les lignes restantes avec des tuiles par défaut (Sable)
     * @param startRow La ligne à partir de laquelle commencer le remplissage
     */
    private void fillRemainingRows(int startRow) {
        for (int ligne = startRow; ligne < lignes; ligne++) {
            for (int colonne = 0; colonne < colonnes; colonne++) {
                int x = colonne * tailleTuile;
                int y = ligne * tailleTuile;
                // Créer une tuile de sable par défaut
                tuiles[ligne][colonne] = new Sable(x, y, tailleTuile, Color.YELLOW, ligne, colonne);
            }
        }
    }
    
    /**
     * Vérifie les obstacles sur le plateau et affiche leur position
     */
    public void verifyObstacles() {
        System.out.println("Vérification des obstacles sur le plateau:");
        int obstacleCount = 0;
        for (int ligne = 0; ligne < lignes; ligne++) {
            for (int colonne = 0; colonne < colonnes; colonne++) {
                if (tuiles[ligne][colonne].hasObstacle()) {
                    obstacleCount++;
                    System.out.println("Obstacle trouvé en: (" + ligne + "," + colonne + ")");
                }
            }
        }
        System.out.println("Total des obstacles trouvés: " + obstacleCount);
    }
    
    // --- GETTERS ET SETTERS ---
    
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
    
    /**
     * Retourne la taille d'une tuile.
     * 
     * @return la taille d'une tuile
     */
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

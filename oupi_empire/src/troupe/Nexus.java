package troupe;

import java.awt.Color;
import java.awt.Graphics2D;
import jeu_oupi.JeuxOupi;

/**
 * Classe Nexus - Une structure stationnaire qui occupe une zone de 2x2 tuiles.
 * Quand un Nexus est détruit, l'équipe adverse gagne la partie.
 * 
 * @author Badr Rifki
 */
public class Nexus extends Troupe {
    
    // Dimensions du Nexus (2x2 tuiles)
    private static final int TAILLE_NEXUS = 2;
    
    /**
     * Constructeur pour créer un objet de type Nexus
     * 
     * @param col colonne supérieure gauche du Nexus
     * @param lig ligne supérieure gauche du Nexus
     * @param equipe l'équipe à laquelle appartient le Nexus
     * @param jeu l'instance du jeu associée à ce Nexus
     */
    public Nexus(int col, int lig, int equipe, JeuxOupi jeu) {

        super(lig, col, jeu, "Oupi Goupi", "res/bak/nexus_player_card.png", true);

        this.setEquipe(equipe);
        image = getImage("/troupes/nexus.png");
        
        // Ne peut pas se déplacer, donc la distance parcourable est 0
        this.setDistanceParcourable(0);
        this.setBakDistParc(0);
        
        // Le Nexus a une portée d'attaque de 1 (défense de proximité)
        this.setDistanceAttaque(1);
        
        // Statistiques puissantes pour une structure défensive
        HP = 300;
        attaque = 15;
        defense = 30;
        vitesse = 0;
        endurance = 100;
        
        // Nom personnalisé selon l'équipe
        nom = "Nexus de l'équipe " + equipe;
        
        // Marquer les 4 tuiles comme occupées
        occuperTuiles();
        
        id = 4; // Identifiant unique pour le Nexus
    }
    
    /**
     * Marque les 4 tuiles occupées par le Nexus
     */
    public void occuperTuiles() {
        if (getJeu() != null && getJeu().getPlateau() != null) {
            for (int i = 0; i < TAILLE_NEXUS; i++) {
                for (int j = 0; j < TAILLE_NEXUS; j++) {
                    int ligCible = getLig() + i;
                    int colCible = getCol() + j;
                    
                    // Vérifier que la tuile est dans les limites du plateau
                    if (ligCible >= 0 && colCible >= 0 && 
                        ligCible < getJeu().getPlateau().getLignes() && 
                        colCible < getJeu().getPlateau().getColonnes()) {
                        getJeu().getPlateau().getTuile(ligCible, colCible).setOccupee(true);
                    }
                }
            }
        }
    }
    
    /**
     * Libère les 4 tuiles occupées par le Nexus (appelé lors de sa destruction)
     */
    public void libererTuiles() {
        if (getJeu() != null && getJeu().getPlateau() != null) {
            for (int i = 0; i < TAILLE_NEXUS; i++) {
                for (int j = 0; j < TAILLE_NEXUS; j++) {
                    int ligCible = getLig() + i;
                    int colCible = getCol() + j;
                    
                    // Vérifier que la tuile est dans les limites du plateau
                    if (ligCible >= 0 && colCible >= 0 && 
                        ligCible < getJeu().getPlateau().getLignes() && 
                        colCible < getJeu().getPlateau().getColonnes()) {
                        getJeu().getPlateau().getTuile(ligCible, colCible).setOccupee(false);
                    }
                }
            }
        }
    }
    
    /**
     * Vérifie si toutes les tuiles nécessaires pour placer le Nexus sont disponibles
     * 
     * @return true si toutes les tuiles sont disponibles, false sinon
     */
    public boolean peutEtrePlacé() {
        if (getJeu() == null || getJeu().getPlateau() == null) {
            return false;
        }
        
        for (int i = 0; i < TAILLE_NEXUS; i++) {
            for (int j = 0; j < TAILLE_NEXUS; j++) {
                int ligCible = getLig() + i;
                int colCible = getCol() + j;
                
                // Vérifier que la tuile est dans les limites du plateau et non occupée
                if (ligCible < 0 || colCible < 0 || 
                    ligCible >= getJeu().getPlateau().getLignes() || 
                    colCible >= getJeu().getPlateau().getColonnes() ||
                    getJeu().getPlateau().getTuile(ligCible, colCible).estOccupee()) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Dessine le Nexus sur une zone de 2x2 tuiles.
     */
    @Override
    public void dessiner(Graphics2D g2d) {
        Graphics2D g2dPrive = (Graphics2D) g2d.create();
        
        int tailleTuile = getJeu().getTailleTuile();
        int x = getX(getCol());
        int y = getY(getLig());
        
        // Dessiner le fond transparent avec la couleur de l'équipe
        Color couleur;
        if (getEquipe() == 0) {
            couleur = new Color(0, 200, 0, 80); // Vert semi-transparent
        } else {
            couleur = new Color(200, 0, 0, 80); // Rouge semi-transparent
        }
        
        g2dPrive.setColor(couleur);
        g2dPrive.fillRect(x, y, tailleTuile * TAILLE_NEXUS, tailleTuile * TAILLE_NEXUS);
        
        // Dessiner une bordure si la tour est sélectionnée
        if (estSelectionne()) {
            g2dPrive.setColor(Color.YELLOW);
            g2dPrive.drawRect(x, y, tailleTuile * TAILLE_NEXUS, tailleTuile * TAILLE_NEXUS);
        }
        
        // Dessiner l'image sur toute la zone 2x2
        g2dPrive.drawImage(image, x, y, tailleTuile * TAILLE_NEXUS, tailleTuile * TAILLE_NEXUS, null);
        
        // Dessiner une barre de vie au-dessus du Nexus
        int barreWidth = tailleTuile * TAILLE_NEXUS;
        int barreHeight = 5;
        int yBarre = y - 10;
        
        // Fond de la barre de vie (rouge)
        g2dPrive.setColor(Color.RED);
        g2dPrive.fillRect(x, yBarre, barreWidth, barreHeight);
        
        // Partie remplie de la barre de vie (vert)
        float pourcentageVie = (float)HP / 300.0f; // 300 est la vie max initiale
        g2dPrive.setColor(Color.GREEN);
        g2dPrive.fillRect(x, yBarre, (int)(barreWidth * pourcentageVie), barreHeight);
        
        g2dPrive.dispose();
    }
    
    /**
     * Vérifie si une position donnée est à l'intérieur du Nexus.
     */
    @Override
    public boolean estA(int clickX, int clickY) {
        int tailleTuile = getJeu().getTailleTuile();
        int nexusX = getX(getCol());
        int nexusY = getY(getLig());
        
        return clickX >= nexusX && clickX < nexusX + (tailleTuile * TAILLE_NEXUS) && 
               clickY >= nexusY && clickY < nexusY + (tailleTuile * TAILLE_NEXUS);
    }
    
    /**
     * Les méthodes de déplacement sont surchargées pour ne rien faire,
     * car un Nexus est stationnaire.
     */
    @Override
    public void deplacerHaut() {
        // Ne fait rien - le Nexus est immobile
    }
    
    @Override
    public void deplacerBas() {
        // Ne fait rien - le Nexus est immobile
    }
    
    @Override
    public void deplacerGauche() {
        // Ne fait rien - le Nexus est immobile
    }
    
    @Override
    public void deplacerDroite() {
        // Ne fait rien - le Nexus est immobile
    }
    
    /**
     * Méthode appelée quand un Nexus est détruit.
     * Doit libérer toutes les tuiles et signal la victoire de l'adversaire.
     */
    public void detruire() {
        // Libérer toutes les tuiles occupées
        libererTuiles();
        
        // L'équipe qui n'est pas propriétaire du Nexus gagne
        int equipeGagnante = (getEquipe() == 0) ? 1 : 0;
        
        // À ce stade, il faudrait signaler la fin du jeu avec l'équipe gagnante
        System.out.println("🏆 Le Nexus de l'équipe " + getEquipe() + " a été détruit ! L'équipe " + equipeGagnante + " GAGNE !");
    }
    
    // Helper methods to access coordinates
    private int getY(int lig) {
        return lig * getJeu().getTailleTuile();
    }

    private int getX(int col) {
        return col * getJeu().getTailleTuile();
    }

    /**
     * Calcule la distance minimale entre une troupe et n'importe quelle case du Nexus.
     * 
     * @param troupe La troupe dont on veut calculer la distance avec le Nexus
     * @return La distance minimale (distance Manhattan) entre la troupe et le Nexus
     */
    public int getDistanceMinimale(Troupe troupe) {
        int minDistance = Integer.MAX_VALUE;
        
        // Parcourir les 4 cases du Nexus (2x2)
        for (int i = 0; i < TAILLE_NEXUS; i++) {
            for (int j = 0; j < TAILLE_NEXUS; j++) {
                int nexusLig = getLig() + i;
                int nexusCol = getCol() + j;
                
                // Calculer la distance Manhattan avec cette case du Nexus
                int distance = Math.abs(troupe.getCol() - nexusCol) + Math.abs(troupe.getLig() - nexusLig);
                
                // Garder la distance minimale
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
        }
        
        return minDistance;
    }
}

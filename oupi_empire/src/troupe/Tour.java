package troupe;

import java.awt.Color;
import java.awt.Graphics2D;
import jeu_oupi.JeuxOupi;

/**
 * Classe Tour - Une structure stationnaire défensive qui occupe une zone de 2x2 tuiles.
 * Contrairement au Nexus, la Tour est principalement offensive et peut attaquer les unités ennemies.
 * 
 * @author Badr Rifki
 */
public class Tour extends Troupe {
    
    // Dimensions de la Tour (2x2 tuiles)
    private static final int TAILLE_TOUR = 2;
    
    /**
     * Constructeur pour créer un objet de type Tour
     * 
     * @param col colonne supérieure gauche de la Tour
     * @param lig ligne supérieure gauche de la Tour
     * @param equipe l'équipe à laquelle appartient la Tour
     * @param jeu l'instance du jeu associée à cette Tour
     */
    public Tour(int col, int lig, int equipe, JeuxOupi jeu) {
        super(lig, col, jeu, "Tour de défense", "res/bak/tower_player_card.png", false);

        this.setEquipe(equipe);
        image = getImage("/troupes/tour.png");
        
        // Ne peut pas se déplacer, donc la distance parcourable est 0
        this.setDistanceParcourable(0);
        this.setBakDistParc(0);
        
        // La Tour a une portée d'attaque de 10 (attaque à distance)
        this.setDistanceAttaque(10);
        
        // Statistiques adaptées pour une structure offensive
        HP = 200;
        attaque = 30;
        defense = 20;
        vitesse = 0;
        endurance = 100;
        
        // Nom personnalisé selon l'équipe
        nom = "Tour de l'équipe " + equipe;
        
        // Marquer les 4 tuiles comme occupées
        occuperTuiles();
        
        id = 5; // Identifiant unique pour la Tour (différent du Nexus)
    }
    
    /**
     * Marque les 4 tuiles occupées par la Tour
     */
    public void occuperTuiles() {
        if (getJeu() != null && getJeu().getPlateau() != null) {
            for (int i = 0; i < TAILLE_TOUR; i++) {
                for (int j = 0; j < TAILLE_TOUR; j++) {
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
     * Libère les 4 tuiles occupées par la Tour (appelé lors de sa destruction)
     */
    public void libererTuiles() {
        if (getJeu() != null && getJeu().getPlateau() != null) {
            for (int i = 0; i < TAILLE_TOUR; i++) {
                for (int j = 0; j < TAILLE_TOUR; j++) {
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
     * Vérifie si toutes les tuiles nécessaires pour placer la Tour sont disponibles
     * 
     * @return true si toutes les tuiles sont disponibles, false sinon
     */
    public boolean peutEtrePlacé() {
        if (getJeu() == null || getJeu().getPlateau() == null) {
            return false;
        }
        
        for (int i = 0; i < TAILLE_TOUR; i++) {
            for (int j = 0; j < TAILLE_TOUR; j++) {
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
     * Dessine la Tour sur une zone de 2x2 tuiles.
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
            couleur = new Color(0, 100, 200, 80); // Bleu semi-transparent pour équipe 0
        } else {
            couleur = new Color(200, 100, 0, 80); // Orange semi-transparent pour équipe 1
        }
        
        g2dPrive.setColor(couleur);
        g2dPrive.fillRect(x, y, tailleTuile * TAILLE_TOUR, tailleTuile * TAILLE_TOUR);
        
        // Dessiner une bordure si la tour est sélectionnée
        if (estSelectionne()) {
            g2dPrive.setColor(Color.YELLOW);
            g2dPrive.drawRect(x, y, tailleTuile * TAILLE_TOUR, tailleTuile * TAILLE_TOUR);
        }
        
        // Dessiner l'image sur toute la zone 2x2
        g2dPrive.drawImage(image, x, y, tailleTuile * TAILLE_TOUR, tailleTuile * TAILLE_TOUR, null);
        
        // Dessiner une barre de vie au-dessus de la Tour
        int barreWidth = tailleTuile * TAILLE_TOUR;
        int barreHeight = 5;
        int yBarre = y - 10;
        
        // Fond de la barre de vie (rouge)
        g2dPrive.setColor(Color.RED);
        g2dPrive.fillRect(x, yBarre, barreWidth, barreHeight);
        
        // Partie remplie de la barre de vie (vert)
        float pourcentageVie = (float)HP / 200.0f; // 200 est la vie max initiale
        g2dPrive.setColor(Color.GREEN);
        g2dPrive.fillRect(x, yBarre, (int)(barreWidth * pourcentageVie), barreHeight);
        
        g2dPrive.dispose();
    }
    
    /**
     * Vérifie si une position donnée est à l'intérieur de la Tour.
     */
    @Override
    public boolean estA(int clickX, int clickY) {
        int tailleTuile = getJeu().getTailleTuile();
        int tourX = getX(getCol());
        int tourY = getY(getLig());
        
        return clickX >= tourX && clickX < tourX + (tailleTuile * TAILLE_TOUR) && 
               clickY >= tourY && clickY < tourY + (tailleTuile * TAILLE_TOUR);
    }
    
    /**
     * Les méthodes de déplacement sont surchargées pour ne rien faire,
     * car une Tour est stationnaire.
     */
    @Override
    public void deplacerHaut() {
        // Ne fait rien - la Tour est immobile
    }
    
    @Override
    public void deplacerBas() {
        // Ne fait rien - la Tour est immobile
    }
    
    @Override
    public void deplacerGauche() {
        // Ne fait rien - la Tour est immobile
    }
    
    @Override
    public void deplacerDroite() {
        // Ne fait rien - la Tour est immobile
    }
    
    /**
     * Calcule la distance minimale entre une troupe et n'importe quelle case de la Tour.
     * 
     * @param troupe La troupe dont on veut calculer la distance avec la Tour
     * @return La distance minimale (distance Manhattan) entre la troupe et la Tour
     */
    public int getDistanceMinimale(Troupe troupe) {
        int minDistance = Integer.MAX_VALUE;
        
        // Parcourir les 4 cases de la Tour (2x2)
        for (int i = 0; i < TAILLE_TOUR; i++) {
            for (int j = 0; j < TAILLE_TOUR; j++) {
                int tourLig = getLig() + i;
                int tourCol = getCol() + j;
                
                // Calculer la distance Manhattan avec cette case de la Tour
                int distance = Math.abs(troupe.getCol() - tourCol) + Math.abs(troupe.getLig() - tourLig);
                
                // Garder la distance minimale
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
        }
        
        return minDistance;
    }
    
    /**
     * Retourne la valeur maximale des points de vie pour cette Tour.
     */
    @Override
    public int getHPMax() {
        return 200; // HP max de la Tour
    }
    
    // Helper methods to access coordinates
    private int getY(int lig) {
        return lig * getJeu().getTailleTuile();
    }

    private int getX(int col) {
        return col * getJeu().getTailleTuile();
    }
}

package troupe;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import interfaces.Dessinable;
import jeu_oupi.JeuxOupi;
import plateau.Tuile;
import tuiles.Sable;

/**
 * La classe {@code Troupe} représente une troupe dans le jeu Oupi. Elle
 * implémente l'interface {@link Dessinable} pour permettre le dessin de la
 * troupe.
 * 
 * @author Badr Rifki
 * @author Loic Simard
 */
public class Troupe implements Dessinable {
    // Variables d'instance
    protected BufferedImage image;
    protected String playerCard;
    protected String nom;
    private int x, y;
    private int col, lig, preCol, preLig;
    private boolean selectionne;
    public boolean remplie;
    private boolean isNexus;
    
    private boolean epuisee;
    private int equipe;
        
    private int bakDistParc = 0;
    private int distanceParcourable = 0;
    
    // Variables pour garder la position centrale quand tuilesSelec a été créé
    private int centreCol, centreLig;
    
    private Tuile[][] tuilesSelec;
    
    protected int HP, attaque, defense, vitesse, endurance;
    protected ArrayList<Debuff> debuffs = new ArrayList<Debuff>();
    protected int id;
    protected int lvl = 0;
    protected int kills = 0;
    
    // Distance d'attaque (1 = corps à corps par défaut)
    protected int distanceAttaque = 1;
    private static int equipeActuelle = 0;

    // Référence à l'instance de JeuxOupi
    private JeuxOupi jeu;

    // Variables pour l'animation de sautillement
    private int bounceSize = 0;
    private boolean bounceGrowing = true;
    
    private static ArrayList<String> combatMessages = new ArrayList<>();
    private static final Random random = new Random();
	
    /**
     * Constructeur de la classe {@code Troupe}.
     * 
     * @param lig la ligne initiale de la troupe
     * @param col la colonne initiale de la troupe
     * @param jeu l'instance du jeu à laquelle appartient cette troupe
     */
    public Troupe(int lig, int col, JeuxOupi jeu, String nom, String playerCardPath, boolean isNexus) {
        this.col = col;
        this.lig = lig;
        this.jeu = jeu;
        // Calculer la position en pixels basée sur la taille de tuile
        updatePosition();
        preCol = col;
        preLig = lig;
        selectionne = false;
        
        HP = 100;
        attaque = 20;
        defense = 10;
        vitesse = 20;
        endurance = 30;
        distanceAttaque = 1; // Distance d'attaque par défaut (corps à corps)
        playerCard = playerCardPath;
        this.nom = nom;
        this.isNexus = isNexus;
    }

    /**
     * Change l'equipe qui est en train de jouer
     */
    public static void toggleEquipeActuelle() {
        if (equipeActuelle == 0) {
            equipeActuelle = 1;
        } else {
            equipeActuelle = 0;
        }
    }

    /**
     * Met à jour la position en pixels basée sur les coordonnées de la grille
     */
    private void updatePosition() {
        x = getX(col);
        y = getY(lig);
    }

    private void initialiserTuilesSelec() {
        int taille = 2 * bakDistParc + 1; // Zone autour de la troupe
        tuilesSelec = new Tuile[taille][taille];

        // Sauvegarde de la position centrale actuelle
        centreCol = col;
        centreLig = lig;

        // Get actual map boundaries
        int mapRows = jeu.getPlateau().getLignes();
        int mapCols = jeu.getPlateau().getColonnes();

        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                int ligne = lig - bakDistParc + i;
                int colonne = col - bakDistParc + j;

                // Vérifier si la position est dans le losange et dans les limites de la carte
                if (Math.abs(i - bakDistParc) + Math.abs(j - bakDistParc) <= bakDistParc && ligne >= 0
                        && ligne < mapRows && colonne >= 0 && colonne < mapCols) {
                    Tuile tuile = jeu.getPlateau().getTuile(ligne, colonne);
                    if (tuile.estOccupee() && !(ligne == lig && colonne == col)) {
                        tuilesSelec[i][j] = null;
                    } else {
                        tuilesSelec[i][j] = tuile;
                        // Marquer la tuile comme accessible pour le rendu visuel
                        tuile.setAccessible(true);
                    }
                } else {
                    tuilesSelec[i][j] = null; // En dehors du plateau ou en dehors du losange
                }
            }
        }
    }
    
    /**
     * Efface le marquage d'accessibilité de toutes les tuiles
     */
    private void effacerZoneAccessible() {
        if (tuilesSelec != null) {
            int taille = tuilesSelec.length;
            for (int i = 0; i < taille; i++) {
                for (int j = 0; j < taille; j++) {
                    if (tuilesSelec[i][j] != null) {
                        tuilesSelec[i][j].setAccessible(false);
                    }
                }
            }
            // Pour s'assurer que les références sont libérées correctement
            tuilesSelec = null;
        }
    }

    private void printTuilesSelec() {
        int taille = tuilesSelec.length;
        System.out.println("Tableau des tuiles sélectionnables (distance max: " + bakDistParc + "):");
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                if (i == bakDistParc && j == bakDistParc) {
                    System.out.print(" [X]  "); // Position de la troupe
                } else if (tuilesSelec[i][j] != null) {
                    System.out.printf("(%d,%d) ", i, j); // Tuile valide
                } else {
                    System.out.print(" [#]  "); // Hors plateau
                }
            }
            System.out.println();
        }
    }

    /**
     * Dessine la troupe avec une animation de sautillement.
     * 
     * @param g2d l'objet {@link Graphics2D} utilisé pour dessiner
     */
    @Override
    public void dessiner(Graphics2D g2d) {
        Graphics2D g2dPrive = (Graphics2D) g2d.create();

        Color couleur = new Color(0, 0, 0);

        // Dessiner un contour si la troupe est sélectionnée
        if (equipe == equipeActuelle) {
            couleur = Color.GREEN;
        } else {
            bounceSize -= 2;
            if (bounceSize <= 0) { // Taille minimale de la réduction
                bounceGrowing = true;
            }
        }

        g2dPrive.setColor(couleur);

        if (selectionne && !epuisee) {
            g2dPrive.drawRect(x, y, jeu.getTailleTuile(), jeu.getTailleTuile());
        }

        couleur = new Color(couleur.getRed(), couleur.getGreen(), couleur.getBlue(), 50);
        g2dPrive.setColor(couleur);
        g2dPrive.fillRect(x, y, jeu.getTailleTuile(), jeu.getTailleTuile());
        g2dPrive.drawImage(image, x, y, jeu.getTailleTuile(), jeu.getTailleTuile(), null);
        
        // Dessiner une barre de vie au-dessus de la troupe
        int barreWidth = jeu.getTailleTuile();
        int barreHeight = 4;
        int yBarre = y - 6;
        
        // Calculer la hauteur maximale des HP pour cette troupe
        // Pour les troupes standard, on considère généralement leur HP initial comme max
        // Pour une approche plus générique, on pourrait ajouter un attribut HPMax
        int hpMax = getHPMax();
        
        // Fond de la barre de vie (rouge)
        g2dPrive.setColor(Color.RED);
        g2dPrive.fillRect(x, yBarre, barreWidth, barreHeight);
        
        // Partie remplie de la barre de vie (vert)
        float pourcentageVie = (float)HP / (float)hpMax;
        g2dPrive.setColor(Color.GREEN);
        g2dPrive.fillRect(x, yBarre, (int)(barreWidth * pourcentageVie), barreHeight);
        
        // Contour noir pour la lisibilité
        g2dPrive.setColor(Color.BLACK);
        g2dPrive.drawRect(x, yBarre, barreWidth, barreHeight);
        
        g2dPrive.dispose();
    }
    
    /**
     * Détermine la valeur maximale des points de vie pour cette troupe.
     * Cette méthode peut être surchargée par les sous-classes si nécessaire.
     * 
     * @return la valeur maximale des points de vie
     */
    protected int getHPMax() {
        // Valeurs par défaut pour chaque type de troupe
        // Ces valeurs correspondent aux HP initiaux définis dans les constructeurs
        String className = this.getClass().getSimpleName();
        switch (className) {
            case "Oupi":
                return 150;
            case "Electricien":
                return 90;
            case "Genial":
                return 120;
            case "Lobotomisateur":
                return 80;
            case "Nexus":
                return 300;
            default:
                return 100; // Valeur par défaut
        }
    }

    /**
     * Déplace la troupe vers le haut.
     */
    public void deplacerHaut() {
        int nouvelleLigne = lig - 1;
        if (estDansLimites(nouvelleLigne, col)) {
            preLig = lig;
            lig = nouvelleLigne;
            y = getY(lig);
            
            Tuile currentTile = jeu.getPlateau().getTuile(lig, col);
            if (currentTile instanceof tuiles.Sable) {
            }
        }
    }

    /**
     * Déplace la troupe vers le bas.
     */
    public void deplacerBas() {
        int nouvelleLigne = lig + 1;
        if (estDansLimites(nouvelleLigne, col)) {
            preLig = lig;
            lig = nouvelleLigne;
            y = getY(lig);
            
            Tuile currentTile = jeu.getPlateau().getTuile(lig, col);
            if (currentTile instanceof tuiles.Sable) {
            }
        }
    }

    /**
     * Déplace la troupe vers la gauche.
     */
    public void deplacerGauche() {
        int nouvelleColonne = col - 1;
        if (estDansLimites(lig, nouvelleColonne)) {
            preCol = col;
            col = nouvelleColonne;
            x = getX(col);
            
            Tuile currentTile = jeu.getPlateau().getTuile(lig, col);
            if (currentTile instanceof tuiles.Sable) {
            }
        }
    }

    /**
     * Déplace la troupe vers la droite.
     */
    public void deplacerDroite() {
        int nouvelleColonne = col + 1;
        if (estDansLimites(lig, nouvelleColonne)) {
            preCol = col;
            col = nouvelleColonne;
            x = getX(col);
            
            Tuile currentTile = jeu.getPlateau().getTuile(lig, col);
            if (currentTile instanceof tuiles.Sable) {
            }
        }
    }

    public void confirmerMouv() {
        selectionne = false;
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
            // Vérifier si la ressource existe
            java.net.URL url = getClass().getResource(imagePath);
            if (url == null) {
                System.err.println("Erreur: Ressource image introuvable: " + imagePath);
                System.err.println("Chemin de recherche: " + System.getProperty("java.class.path"));
                // Créer une image par défaut comme solution de secours
                return createPlaceholderImage();
            }
            
            image = ImageIO.read(url);
            if (image == null) {
                System.err.println("Erreur: Échec de lecture de l'image: " + imagePath);
                return createPlaceholderImage();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de l'image: " + imagePath);
            e.printStackTrace();
            return createPlaceholderImage();
        }
        return image;
    }

    /**
     * Crée une image placeholder simple qui sera utilisée si l'image originale ne peut pas être chargée.
     * 
     * @return une image placeholder
     */
    private BufferedImage createPlaceholderImage() {
        BufferedImage placeholder = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = placeholder.createGraphics();
        g2d.setColor(Color.RED);
        g2d.fillRect(0, 0, 32, 32);
        g2d.setColor(Color.WHITE);
        g2d.drawLine(0, 0, 32, 32);
        g2d.drawLine(0, 32, 32, 0);
        g2d.dispose();
        return placeholder;
    }

    private boolean estDansLimites(int ligne, int colonne) {
        // Calculer les indices dans tuilesSelec par rapport à la position centrale
        // initiale
        int i = ligne - (centreLig - bakDistParc);
        int j = colonne - (centreCol - bakDistParc);

        // Vérifier si le déplacement est dans les limites du tableau et que la tuile
        // existe
        return i >= 0 && i < tuilesSelec.length && j >= 0 && j < tuilesSelec[0].length && tuilesSelec[i][j] != null;
    }

    /**
     * Vérifie si la troupe est à la position spécifiée.
     * 
     * @param clickX la position x du clic
     * @param clickY la position y du clic
     * @return {@code true} si la troupe est à la position spécifiée, {@code false}
     *         sinon
     */
    public boolean estA(int clickX, int clickY) {
        int troupeX = x;
        int troupeY = y;
        return clickX >= troupeX && clickX < troupeX + jeu.getTailleTuile() && clickY >= troupeY
                && clickY < troupeY + jeu.getTailleTuile();
    }

    public void deselec() {
        selectionne = false;
        effacerZoneAccessible();
        // Libérer les références pour éviter les fuites mémoire
        tuilesSelec = null;
    }

    public void attaquer(Troupe troupeEnem) {
        combatMessages.clear();
        
        // Vérifier que les troupes sont d'équipes différentes
        if (this.equipe == troupeEnem.getEquipe()) {
            String message = "⚠️ Impossible d'attaquer une troupe alliée!";
            System.out.println(message);
            combatMessages.add(message);
            return;
        }
        
        // Calcul de la distance entre les troupes
        int distance;
        
        // Si la cible est un Nexus, utiliser sa méthode spéciale pour calculer la distance minimale
        if (troupeEnem instanceof Nexus) {
            distance = ((Nexus) troupeEnem).getDistanceMinimale(this);
        } else {
            // Calcul standard pour les autres troupes
            distance = Math.abs(this.getCol() - troupeEnem.getCol()) + Math.abs(this.getLig() - troupeEnem.getLig());
        }
        
        // Vérifier que la troupe est à portée d'attaque
        if (distance > distanceAttaque) {
            String message = "⚠️ Cible hors de portée! Distance maximale d'attaque: " + distanceAttaque;
            System.out.println(message);
            combatMessages.add(message);
            return;
        }
        
        int dodgeChance = Math.max(0, Math.max(7000, troupeEnem.vitesse - this.vitesse + 20));
        
        if (random.nextInt(100) > dodgeChance) {
            String message = "🏃 " + troupeEnem.getClass().getSimpleName() + " esquive l'attaque!";
            System.out.println(message);
            combatMessages.add(message);
            return;
        }
        
        // Calcul des dégâts infligés (formule simple inspirée de Fire Emblem)
        int degats = Math.max(1, this.attaque - troupeEnem.defense / 2);

        // Application des dégâts
        troupeEnem.HP = Math.max(0, troupeEnem.HP - degats);

        String message = "🗡️ " + this.getClass().getSimpleName() + " attaque et inflige " + degats + " points de dégâts!";
        System.out.println(message);
        combatMessages.add(message);
        
        message = "   " + troupeEnem.getClass().getSimpleName() + " a maintenant " + troupeEnem.HP + " HP.";
        System.out.println(message);
        combatMessages.add(message);

        // Vérifier si la troupe ennemie est vaincue
        if (troupeEnem.HP <= 0) {
            message = "💀 " + troupeEnem.getClass().getSimpleName() + " a été vaincu!";
            System.out.println(message);
            combatMessages.add(message);
        } else {
            // Contre-attaque si la troupe ennemie est encore en vie
            // Vérifier si l'attaque était à distance et que l'ennemi ne peut attaquer qu'au corps à corps
            if (distance > 1 && troupeEnem.distanceAttaque == 1) {
                message = "🛡️ " + troupeEnem.getClass().getSimpleName() + " ne peut pas contre-attaquer à distance!";
                System.out.println(message);
                combatMessages.add(message);
            }
            else if (troupeEnem.vitesse >= this.vitesse - 5) {
                int counterDodgeChance = Math.max(0, Math.min(70, this.vitesse - troupeEnem.vitesse + 20));
                
                if (random.nextInt(100) < counterDodgeChance) {
                    message = "🏃 " + this.getClass().getSimpleName() + " esquive la contre-attaque!";
                    System.out.println(message);
                    combatMessages.add(message);
                } else {
                    int degatsContre = Math.max(1, troupeEnem.attaque - this.defense / 2);
                    this.HP = Math.max(0, this.HP - degatsContre);
    
                    message = "⚔️ " + troupeEnem.getClass().getSimpleName() + " contre-attaque et inflige "
                            + degatsContre + " points de dégâts!";
                    System.out.println(message);
                    combatMessages.add(message);
                    
                    message = "   " + this.getClass().getSimpleName() + " a maintenant " + this.HP + " HP.";
                    System.out.println(message);
                    combatMessages.add(message);
    
                    // Vérifier si l'attaquant est vaincu par la contre-attaque
                    if (this.HP <= 0) {
                        message = "💀 " + this.getClass().getSimpleName() + " a été vaincu!";
                        System.out.println(message);
                        combatMessages.add(message);
                    }
                }
            } else {
                message = "🛡️ " + troupeEnem.getClass().getSimpleName() + " est trop lent pour contre-attaquer.";
                System.out.println(message);
                combatMessages.add(message);
            }
        }

        // Réduction de l'endurance après l'attaque
        if(HP > 0) {
            this.endurance = Math.max(0, this.endurance - 5);
            message = "⚡ Endurance de " + this.getClass().getSimpleName() + " réduite à " + this.endurance;
            System.out.println(message);
            combatMessages.add(message);
        }
    }
    
    /**
     * Ajoutes un au nombre d'elimination de la troupe
     */
    public void kill() {
    	kills++;
    }
    
    /**
     * Ajuste le niveau de la troupe pour qu'il soit egal au nombre d'eliminations de la troupe
     * 
     * @return Le nombre de niveaux que la troupe a gagnee
     */
    public int levelUp() {
    	int temp;
    	if(kills != lvl) {
    		temp = kills-lvl;
    		lvl = kills;
    		return temp;
    	}
    	
    	return 0;
    }

    private int getY(int lig) {
        return lig * jeu.getTailleTuile();
    }

    private int getX(int col) {
        return col * jeu.getTailleTuile();
    }

    // --- GETTERS ET SETTERS ---
    
    /**
     * @param equipe l'équipe à définir
     */
    public void setEquipe(int equipe) {
        this.equipe = equipe;
    }

    public int getEquipe() {
        return equipe;
    }
    
    public String getPlayerCard() {
    	return playerCard;
    }
    
    public String getNom() {
    	return nom;
    }
    
    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
    
    public boolean estSelectionne() {
        return selectionne;
    }

    public void setSelectionne(boolean selectionne) {
        this.selectionne = selectionne;
        if (selectionne) {
            initialiserTuilesSelec();
            printTuilesSelec();
            distanceParcourable = bakDistParc; // Réinitialiser la distance parcourable
        } else {
            effacerZoneAccessible();
        }
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
        x = getX(col);
    }

    public void setLig(int lig) {
        this.lig = lig;
        y = getY(lig);
    }
    
    public static ArrayList<String> getCombatMessages() {
        return combatMessages;
    }

	public static void clearCombatMessages() {
		combatMessages.clear();;
	}

    public int getHP() {
        return HP;
    }
    
    public void setHP(int hP) {
        HP = hP;
    }

    public boolean isEpuisee() {
        return epuisee;
    }

    public void setEpuisee(boolean epuisee) {
        this.epuisee = epuisee;
    }

    public int getAttaque() {
        return attaque;
    }

    public void setAttaque(int attaque) {
        this.attaque = attaque;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getVitesse() {
        return vitesse;
    }

    public void setVitesse(int vitesse) {
        this.vitesse = vitesse;
    }

    public int getEndurance() {
        return endurance;
    }

    public void setEndurance(int endurance) {
        this.endurance = endurance;
    }

    public JeuxOupi getJeu() {
        return jeu;
    }
    
    public int getDistanceAttaque() {
        return distanceAttaque;
    }
    
    public void setDistanceAttaque(int distanceAttaque) {
        this.distanceAttaque = distanceAttaque;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public boolean getIsNexus() {
    	return isNexus;
    }
}
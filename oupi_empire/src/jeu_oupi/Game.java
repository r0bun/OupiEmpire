
package jeu_oupi;

import java.util.ArrayList;

import troupe.Troupe;

public class Game {
    private JeuxOupi jeuxOupi; // Instance de la logique du jeu
    private ZoneAnimationOupi zoneAnimation; // Interface utilisateur
    private ArrayList<Joueur> joueurs;
    private int joueurActuel; // Index du joueur actuel
    private boolean enCours; // État de la partie (en cours ou terminée)
    private String resultat; // "Victoire", "Défaite", ou "Égalité"
    private ArrayList<String> messages; // Messages globaux de la partie

    /**
     * Constructeur de la classe Game. 
     * On initialise ici les 2 joueurs avec leurs équipe respectives.
     * @param jeuxOupi Instance de la partie logique du Jeu.
     * @param zoneAnimation Instance de l'interface graphique du Jeu.
     */
    public Game(JeuxOupi jeuxOupi, ZoneAnimationOupi zoneAnimation) {
        this.jeuxOupi = jeuxOupi;
        this.zoneAnimation = zoneAnimation;

        // === Initialisation des joueurs ===
        this.joueurs = new ArrayList<>();
        this.joueurs.add(new Joueur(1, "Joueur 1", 0)); // Joueur 1, équipe 0
        this.joueurs.add(new Joueur(2, "Joueur 2", 1)); // Joueur 2, équipe 1

        this.joueurActuel = 0; // Commence avec le joueur 1
        this.enCours = true;
        this.messages = new ArrayList<>();
    }

    /**
     * On démarre ici la partie.
     * On commence par remplir les équipes des deux joueurs avec les troupes de bases.
     * Tout autre initalisation des joueurs sera ici.
     * On démarre la zone d'animation.
     * On donne le tour au premier joueur.
     */
    public void demarrer() {
    	initialiserTroupes(); // Initialiser les troupes de base
        zoneAnimation.demarrer();
        joueurs.get(joueurActuel).setActif(true); // Activer le premier joueur
        System.out.println("La partie commence ! C'est au tour de " + joueurs.get(joueurActuel).getNom());
    }

    /**
     * On Termine la partie
     * Logique à implémenter
     * @param resultat
     */
    public void terminerPartie() {
        System.out.println("Partie terminée : " + resultat);
        // Vous pouvez ici notifier le GameManager ou afficher un écran de fin
    }
    
    
    /**
     * Cette fonction sert à remplir les armées des deux joueurs avec les troupes de bases.
     * On appelle les fonctions existantes dans jeuxOupi.
     * On place aussi les troupes de bases aux cases de départ.
     */
    private void initialiserTroupes() {
    	/*
        for (Joueur joueur : joueurs) {
            //ArrayList<Troupe> troupesDeBase = jeuxOupi.setTroupes(joueur);
            for (Troupe troupe : troupesDeBase) {
                joueur.ajouterTroupe(troupe); // Associer la troupe au joueur
            }
        }
        */
        System.out.println("Troupes de base initialisées pour tous les joueurs.");
    }

    
    /**
     * Cette fonction est activé quand l'utilisateur appuie sur fin du tour.
     * On vérifie si les conditions de fin de partie sont remplies.
     * Si non, on change le joueur de qui c'est le tour.
     */
    public void finDuTour() {
        if (!enCours) {
            return; // Si la partie est terminée, ne rien faire
        }

        // Vérifier la fin de partie
        checkFinPartie(joueurActuel);

        // Changer de joueur si la partie n'est pas terminée
        if (enCours) {
            changerJoueur();
        }
    }

    /**
     * On effectue dans cette fonction les vérifications de fin de partie.
     * La seule condition pour le moment est qu'une ou les deux équipes n'ont plus de troupes.
     */
    public void checkFinPartie(int player) {

        ArrayList<Troupe> listeTroupes= getPlayerTroupes(player);
        
        if(listeTroupes.isEmpty() || !containsNexus(listeTroupes)) {
        	terminerPartie();
        }
       
    }

    
    /**
     * Cette fonction change le joueur de qui c'est le tour.
     * On réinitialise également les actions des troupes du joueur.
     */
    public void changerJoueur() {
        Joueur joueur = joueurs.get(joueurActuel);
        joueur.setActif(false);
        joueur.reinitialiserTroupes();

        joueurActuel = (joueurActuel + 1) % joueurs.size(); // Passer au joueur suivant
        joueurs.get(joueurActuel).setActif(true);

        System.out.println("🔄 C'est maintenant au tour de " + joueurs.get(joueurActuel).getNom());
    }
    
    /**
     * Cette fonction retourne la liste de troupes appartenant au joueur.
     * @param player
     * @return
     */
	public ArrayList<Troupe> getPlayerTroupes (int player){
		
		ArrayList<Troupe> troupes = jeuxOupi.getTroupes();
		ArrayList<Troupe> troupesJoueur = new ArrayList<>();
		
		for (int i = 0; i < troupes.size(); i++) {
			if(troupes.get(i).getEquipe() == player) {
				troupesJoueur.add(troupes.get(i));
			}
		}
		return troupesJoueur;
	}
	
	public boolean containsNexus (ArrayList<Troupe> liste) {
		for(Troupe troupe : liste) {
			if(troupe.getIsNexus() == true) {
				return true;
			}
		}
		return false;
	}
	

    public void ajouterMessage(String message) {
        messages.add(message);
    }

    public ArrayList<String> getMessages() {
        return new ArrayList<>(messages);
    }

    public boolean isEnCours() {
        return enCours;
    }

    public String getResultat() {
        return resultat;
    }

    public ArrayList<Joueur> getJoueurs() {
        return joueurs;
    }

    public Joueur getJoueurActuel() {
        return joueurs.get(joueurActuel);
    }
    
    public ZoneAnimationOupi getZoneAnimation() {
        return zoneAnimation;
    }
}


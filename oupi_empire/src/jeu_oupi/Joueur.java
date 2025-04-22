package jeu_oupi;

import java.util.ArrayList;
import troupe.Troupe;

public class Joueur {
    private int id;
    private String nom;
    private int equipe; // 0 ou 1
    private boolean actif;
    private ArrayList<Troupe> troupes;

    public Joueur(int id, String nom, int equipe) {
        this.id = id;
        this.nom = nom;
        this.equipe = equipe;
        this.actif = false;
        this.troupes = new ArrayList<>();
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public int getEquipe() {
        return equipe;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public ArrayList<Troupe> getTroupes() {
        return troupes;
    }

    // Méthodes spécifiques
    public boolean aEncoreDesTroupes() {
        for (Troupe troupe : troupes) {
            if (!troupe.isEpuisee()) {
                return true;
            }
        }
        return false;
    }

    public void ajouterTroupe(Troupe troupe) {
        troupes.add(troupe);
    }

    public void retirerTroupe(Troupe troupe) {
        troupes.remove(troupe);
    }

    public void reinitialiserTroupes() {
        for (Troupe troupe : troupes) {
            troupe.setEpuisee(false);
        }
    }
}
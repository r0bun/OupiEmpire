
package jeu_oupi;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import database.DatabaseUpload;
import troupe.Troupe;

public class Game {
    private JeuxOupi jeuxOupi; // Instance de la logique du jeu
    private ZoneAnimationOupi zoneAnimation; // Interface utilisateur
    private boolean resultat; // "Victoire", "Défaite", ou "Égalité"
    private int killsJ1;
    private int killsJ2;
    private int towersDestroyedJ1;
    private int towersDestroyedJ2;
    private int deathsJ1;
    private int deathsJ2;
    private int nexusHpLostJ1;
    private int nexusHpLostJ2;
    private DatabaseUpload db;


    /**
     * Constructeur de la classe Game. 
     * On initialise ici les 2 joueurs avec leurs équipe respectives.
     * @param jeuxOupi Instance de la partie logique du Jeu.
     * @param zoneAnimation Instance de l'interface graphique du Jeu.
     */
    public Game(JeuxOupi jeuxOupi, ZoneAnimationOupi zoneAnimation) {
        this.jeuxOupi = jeuxOupi;
        this.zoneAnimation = zoneAnimation;
        killsJ1 = 0;
        killsJ2 = 0;
        towersDestroyedJ1 = 0;
        towersDestroyedJ2 = 0;
        deathsJ1 = 0;
        deathsJ2 = 0;
        nexusHpLostJ1 = 0;
        nexusHpLostJ2 = 0;
        
        
        zoneAnimation.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("Win")) {
                	
                	
                    db = new DatabaseUpload();
                    
                    db.insertData(GameManager.getInstance().getStringJ1(), killsJ1, deathsJ1, towersDestroyedJ1, nexusHpLostJ1, true);
                }
                
                if (evt.getPropertyName().equals("Lose")) {
                	
                	
                    db = new DatabaseUpload();
                    
                    db.insertData(GameManager.getInstance().getStringJ1(), killsJ1, deathsJ1, towersDestroyedJ1, nexusHpLostJ1, false);
                }
            }
           
        });
    }
     
    
    public ZoneAnimationOupi getZoneAnimation () {
    	return zoneAnimation;
    }
    
    public void addKill (int joueur) {
    	if(joueur == 0) {
    		killsJ1++;
    		System.out.println("kill ajouter au J1");
    	}else {
    		killsJ2 ++;
    		System.out.println("kill ajouter au J2");
    	}
    }
	
    public void addDeath (int joueur) {
    	if(joueur == 0) {
    		deathsJ1++;
    		System.out.println("death ajouter au J1");
    	}else {
    		deathsJ2 ++;
    		System.out.println("death ajouter au J2");
    	}
    }
    
    public void addNexusHpLost (int joueur, int dmg) {
    	
    	int joueurAdverse = (joueur == 0) ? 1 : 0;
    	if(joueurAdverse == 0) {
    		nexusHpLostJ1 += dmg;
    		System.out.println("nexus dmg ajouter au J1");
    	}else {
    		nexusHpLostJ2 += dmg;
    		System.out.println("nexus dmg ajouter au J2");
    	}
    }
    public void addTowerDestroyed(int joueur) {
    	if(joueur == 0) {
    		towersDestroyedJ1++;
    		System.out.println("tower kill ajouter au J1");
    	}else {
    		towersDestroyedJ2 ++;
    		System.out.println("tower kill ajouter au J2");
    	}
    }
    
    //=== Getters et setters ===
    public int getKillsJ1() {
        return killsJ1;
    }

    public void setKillsJ1(int killsJ1) {
        this.killsJ1 = killsJ1;
    }

    public int getKillsJ2() {
        return killsJ2;
    }

    public void setKillsJ2(int killsJ2) {
        this.killsJ2 = killsJ2;
    }

    public int getTowersDestroyedJ1() {
        return towersDestroyedJ1;
    }

    public void setTowersDestroyedJ1(int towersDestroyedJ1) {
        this.towersDestroyedJ1 = towersDestroyedJ1;
    }

    public int getTowersDestroyedJ2() {
        return towersDestroyedJ2;
    }

    public void setTowersDestroyedJ2(int towersDestroyedJ2) {
        this.towersDestroyedJ2 = towersDestroyedJ2;
    }

    public int getDeathsJ1() {
        return deathsJ1;
    }

    public void setDeathsJ1(int deathsJ1) {
        this.deathsJ1 = deathsJ1;
    }

    public int getDeathsJ2() {
        return deathsJ2;
    }

    public void setDeathsJ2(int deathsJ2) {
        this.deathsJ2 = deathsJ2;
    }

    public int getNexusHpLostJ1() {
        return nexusHpLostJ1;
    }

    public void setNexusHpLostJ1(int nexusHpLostJ1) {
        this.nexusHpLostJ1 = nexusHpLostJ1;
    }

    public int getNexusHpLostJ2() {
        return nexusHpLostJ2;
    }

    public void setNexusHpLostJ2(int nexusHpLostJ2) {
        this.nexusHpLostJ2 = nexusHpLostJ2;
    }

}


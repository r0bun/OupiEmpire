
package jeu_oupi;

import application.appLaunch;

public class GameManager {

	private static GameManager instance;
	private Game currentGame;
	private appLaunch gameWindow;
	private ZoneAnimationOupi zoneAnimation;
	String J1;
	String J2;
	
	private GameManager () {}
	
	public static GameManager getInstance() {
		if (instance == null) {
			instance = new GameManager();
		}
		return instance;
	}
	
	public void startNewGame(int screenWidth, int screenHeight) {
		
		// Créer une nouvelle instance de Game
        zoneAnimation = new ZoneAnimationOupi(screenWidth, screenHeight);
        currentGame = new Game(zoneAnimation.getJeuxOupi(), zoneAnimation);
        
        //Créer et afficher la fenêtre principale
        gameWindow = new appLaunch();
        gameWindow.setVisible(true);
        
	}
	
	public Game getCurrentGame() {
        return currentGame;
    }
	
	public appLaunch getGameWindow() {
	    return gameWindow;
	}
	
	public ZoneAnimationOupi getZoneAnimationOupi() {
	        return zoneAnimation;
	}
	
	public void setStringJ2(String string) {
		J2 = string;
	}
	
	public String getStringJ2() {
		return J2;
	}
	
	public void setStringJ1(String string) {
		J1 = string;
	}
	
	public String getStringJ1() {
		return J1;
	}
}

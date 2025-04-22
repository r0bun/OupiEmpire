
package jeu_oupi;

import application.appLaunch;

public class GameManager {

	private static GameManager instance;
	private Game currentGame;
	private appLaunch gameWindow;
	
	private GameManager () {}
	
	public static GameManager getInstance() {
		if (instance == null) {
			instance = new GameManager();
		}
		return instance;
	}
	
	public void startNewGame(int screenWidth, int screenHeight) {
		
		// Créer une nouvelle instance de Game
        ZoneAnimationOupi zoneAnimation = new ZoneAnimationOupi(screenWidth, screenHeight);
        currentGame = new Game(zoneAnimation.getJeuxOupi(), zoneAnimation);
        
        //Créer et afficher la fenêtre principale
        gameWindow = new appLaunch();
        gameWindow.setVisible(true);
        
        //Démarrer la partie
        currentGame.demarrer();
	}
	
	public Game getCurrentGame() {
        return currentGame;
    }
	
	public appLaunch getGameWindow() {
	    return gameWindow;
	}
	
	public ZoneAnimationOupi getZoneAnimationOupi() {
	    if (currentGame != null) {
	        return currentGame.getZoneAnimation();
	    }
	    return null;
	}
}

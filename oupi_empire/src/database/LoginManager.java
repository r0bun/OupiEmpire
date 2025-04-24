package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import jeu_oupi.GameManager;
//import org.mindrot.jbcrypt.BCrypt;

public class LoginManager {

	/**
	 * Cette fonction vérifie si les informations rentrées par l'utilisateur correspondent à celles
	 * présentes dans la base de données.
	 * @param username Nom d'utilisateur rentré par l'usager
	 * @param password Mot de passe rentré par l'usager
	 * @return True or False en fonction de si les informations correspondent
	 */
	public boolean verifiyLogin (String username, String password) {
		
		String dataRequest = "SELECT identifiant, mdp FROM usagers WHERE BINARY identifiant = ?";
		
        try (Connection connectionJdb = DatabaseConnector.connect();
               
        	   PreparedStatement statement = connectionJdb.prepareStatement(dataRequest)) {
        	  
               statement.setString(1, username);
               System.out.println(statement);
               
               ResultSet result = statement.executeQuery();
                    
               if(result.next()) {
            	   System.out.println("Data retrieved successfully!");
            	   String passwordHashed = result.getString("mdp");
            	   
            	   if (passwordHashed.startsWith("$2y$")) {
            		    passwordHashed = "$2a$" + passwordHashed.substring(4);
            		}

            	   if(BCrypt.checkpw(password, passwordHashed)){
            		   GameManager.getInstance().setStringJ1(username);
            		   return true;
            	   }
               }
               System.out.println("Aucun résultat pour ce username.");
               return false;
               
           } catch (SQLException e) {
               e.printStackTrace();
               return false;
           }
	}
}
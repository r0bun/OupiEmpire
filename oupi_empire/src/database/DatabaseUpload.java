package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseUpload {
	
	public  DatabaseUpload() {
		
	}
	
	/**
	 * Cette fonction sert à Insérer de nouvelles informations dans la base de donnée
	 * @param name
	 * @param age
	 */
	public static void insertData(String username, int kills, int deaths, int towersDestroyed, int dmgNexus, boolean victoire) {
        String sql = "INSERT INTO games (username, kills, deaths, towersDestroyed, dmgNexus, result) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connectionJdb = DatabaseConnector.connect();
             PreparedStatement statementt = connectionJdb.prepareStatement(sql)) {
        	
        	String result ="";
        	
        	if(victoire == true) {
        		result = "victoire";
        	}else {
        		result = "défaite";
        	}
             
            statementt.setString(1, username);
            statementt.setInt(2, kills);
            statementt.setInt(3, deaths);
            statementt.setInt(4, towersDestroyed);
            statementt.setInt(5, dmgNexus);
            statementt.setString(6, result);
            statementt.executeUpdate();
            System.out.println("Data inserted successfully!");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cette fonction sert à updater les informations d'une troupe dans la base de donnée
     * @param name
     * @param age
     */
    public static void updateData(String name, int age) {
        String sql = "INSERT INTO units (name, age) VALUES (?, ?)";

        try (Connection connectionJdb = DatabaseConnector.connect();
             PreparedStatement statementt = connectionJdb.prepareStatement(sql)) {
             
            statementt.setString(1, name);
            statementt.setInt(2, age);
            statementt.executeUpdate();
            System.out.println("Data inserted successfully!");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
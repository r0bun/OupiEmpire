package plateau;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Gestionnaire de textures qui met en cache les images pour éviter 
 * de les charger plusieurs fois.
 * 
 * @author YourName
 */
public class TextureManager {
    // Singleton instance
    private static TextureManager instance;
    
    // Cache de textures
    private Map<String, BufferedImage> textureCache;
    
    /**
     * Constructeur privé pour le singleton
     */
    private TextureManager() {
        textureCache = new HashMap<>();
    }
    
    /**
     * Obtenir l'instance unique du gestionnaire de textures
     * @return l'instance du TextureManager
     */
    public static TextureManager getInstance() {
        if (instance == null) {
            instance = new TextureManager();
        }
        return instance;
    }
    
    /**
     * Charge une texture à partir d'un chemin d'accès ou la retourne depuis le cache si déjà chargée
     * 
     * @param imagePath le chemin d'accès à l'image
     * @return l'image chargée ou null en cas d'erreur
     */
    public BufferedImage getTexture(String imagePath) {
        // Vérifier si l'image est déjà en cache
        if (!textureCache.containsKey(imagePath)) {
            try {
                // Charger l'image et la mettre en cache
                BufferedImage image = ImageIO.read(getClass().getResourceAsStream(imagePath));
                textureCache.put(imagePath, image);
                System.out.println("Texture chargée: " + imagePath);
            } catch (IOException e) {
                System.err.println("Erreur lors du chargement de la texture: " + imagePath);
                e.printStackTrace();
                return null;
            }
        }
        
        return textureCache.get(imagePath);
    }
    
    /**
     * Vide le cache de textures pour libérer de la mémoire
     */
    public void clearCache() {
        textureCache.clear();
    }
}

package editeur_carte;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Singleton class for managing textures used by the map editor
 */
public class TextureManager {
    private static TextureManager instance;
    private Map<String, BufferedImage> textureCache = new HashMap<>();
    
    private TextureManager() {
        // Private constructor for singleton
    }
    
    public static TextureManager getInstance() {
        if (instance == null) {
            instance = new TextureManager();
        }
        return instance;
    }
    
    /**
     * Loads a texture and caches it
     * 
     * @param path Path to the texture file
     * @return The loaded texture or null if loading failed
     */
    public BufferedImage getTexture(String path) {
        // Check if texture is already cached
        if (textureCache.containsKey(path)) {
            return textureCache.get(path);
        }
        
        try {
            // Try to load the texture
            BufferedImage texture = ImageIO.read(new File(path));
            textureCache.put(path, texture);
            return texture;
        } catch (IOException e) {
            System.err.println("Error loading texture " + path + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Clears the texture cache
     */
    public void clearCache() {
        textureCache.clear();
    }
    
    /**
     * Gets a terrain texture by type
     * 
     * @param terrainType The terrain type character (H, W, S, G)
     * @return The corresponding texture
     */
    public BufferedImage getTerrainTexture(char terrainType) {
        String path;
        
        switch (terrainType) {
            case 'H': path = "res/tuilesTexture/herbe.png"; break;
            case 'W': path = "res/tuilesTexture/eau.png"; break;
            case 'S': path = "res/tuilesTexture/sable.png"; break;
            case 'G': path = "res/tuilesTexture/gravier.png"; break;
            default: return null;
        }
        
        return getTexture(path);
    }
}

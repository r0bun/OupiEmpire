package editeur_carte;

import java.util.ArrayList;
import java.util.List;

public class MapData {
    private final int rows;
    private final int cols;
    private final int tileSize;
    private final TileType[][] tiles;
    private final List<Obstacle> obstacles;
    
    public MapData(int rows, int cols, int tileSize) {
        this.rows = rows;
        this.cols = cols;
        this.tileSize = tileSize;
        this.tiles = new TileType[rows][cols];
        this.obstacles = new ArrayList<>();
        
        // Initialize all tiles to grass
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                tiles[row][col] = TileType.GRASS;
            }
        }
    }
    
    public int getRows() {
        return rows;
    }
    
    public int getCols() {
        return cols;
    }
    
    public int getTileSize() {
        return tileSize;
    }
    
    public TileType getTile(int col, int row) {
        if (isValidPosition(col, row)) {
            return tiles[row][col];
        }
        return TileType.GRASS; // Default
    }
    
    public void setTile(int col, int row, TileType type) {
        if (isValidPosition(col, row)) {
            tiles[row][col] = type;
            
            // Remove any obstacle at this position
            obstacles.removeIf(o -> o.getCol() == col && o.getRow() == row);
        }
    }
    
    public Obstacle getObstacle(int col, int row) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.getCol() == col && obstacle.getRow() == row) {
                return obstacle;
            }
        }
        return null;
    }
    
    public void setObstacle(int col, int row, Obstacle.Type type) {
        if (isValidPosition(col, row)) {
            // Remove any existing obstacle at this position
            obstacles.removeIf(o -> o.getCol() == col && o.getRow() == row);
            
            // Add new obstacle
            if (type != null) {
                obstacles.add(new Obstacle(col, row, type));
            }
        }
    }
    
    public List<Obstacle> getObstacles() {
        return new ArrayList<>(obstacles);
    }
    
    private boolean isValidPosition(int col, int row) {
        return col >= 0 && col < cols && row >= 0 && row < rows;
    }
}

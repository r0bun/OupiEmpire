package editeur_carte;

public enum TileType {
    GRASS("Grass", 'H'),
    WATER("Water", 'W'),
    SAND("Sand", 'S'),
    GRAVEL("Gravel", 'G');
    
    private final String displayName;
    private final char symbol;
    
    TileType(String displayName, char symbol) {
        this.displayName = displayName;
        this.symbol = symbol;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public char getSymbol() {
        return symbol;
    }
    
    public static TileType fromSymbol(char symbol) {
        for (TileType type : values()) {
            if (type.symbol == symbol) {
                return type;
            }
        }
        return GRASS; // Default if not found
    }
}

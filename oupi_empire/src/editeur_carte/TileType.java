package editeur_carte;

public enum TileType {
    GRASS('H', "Grass"),
    WATER('W', "Water"),
    SAND('S', "Sand"),
    GRAVEL('G', "Gravel");
    
    private final char symbol;
    private final String displayName;
    
    TileType(char symbol, String displayName) {
        this.symbol = symbol;
        this.displayName = displayName;
    }
    
    public char getSymbol() {
        return symbol;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static TileType fromSymbol(char symbol) {
        for (TileType type : values()) {
            if (type.symbol == symbol) {
                return type;
            }
        }
        return GRASS; // Default
    }
}

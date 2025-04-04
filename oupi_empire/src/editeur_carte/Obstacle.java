package editeur_carte;

public class Obstacle {
    public enum Type {
        TREE("Tree", "arbre"),
        ROCK("Rock", "rocher"),
        BUSH("Bush", "buisson");
        
        private final String displayName;
        private final String exportName;
        
        Type(String displayName, String exportName) {
            this.displayName = displayName;
            this.exportName = exportName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getExportName() {
            return exportName;
        }
        
        public static Type fromExportName(String exportName) {
            for (Type type : values()) {
                if (type.exportName.equals(exportName)) {
                    return type;
                }
            }
            return null;
        }
    }
    
    private final int col;
    private final int row;
    private final Type type;
    
    public Obstacle(int col, int row, Type type) {
        this.col = col;
        this.row = row;
        this.type = type;
    }
    
    public int getCol() {
        return col;
    }
    
    public int getRow() {
        return row;
    }
    
    public Type getType() {
        return type;
    }
}

package editeur_carte;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MapBuilderApp app = new MapBuilderApp();
            app.setVisible(true);
        });
    }
}
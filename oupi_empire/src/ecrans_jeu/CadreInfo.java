package ecrans_jeu;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import troupe.Troupe;
import java.awt.Graphics2D;

public class CadreInfo extends JPanel {
    private JLabel lblNomJoueur;
    private JPanel panelUnites;
    private JLabel backgroundLabel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public CadreInfo(int screenWidth, int screenHeight) {
        setLayout(null);
        setPreferredSize(new Dimension(380, 250));
        setBackground(new Color(210, 180, 140)); // Fond général (brun clair)

        // Nom du joueur en haut
        lblNomJoueur = new JLabel(" ");
        lblNomJoueur.setFont(new Font("Arial", Font.BOLD, 24));
        lblNomJoueur.setForeground(Color.WHITE);
        lblNomJoueur.setHorizontalAlignment(SwingConstants.CENTER);
        lblNomJoueur.setBounds(94, 20, 200, 30); // Position du nom
        add(lblNomJoueur);
        
        backgroundLabel = new JLabel();
        backgroundLabel.setBounds(0, 0, 380, 250); // Taille du panneau Stats
        add(backgroundLabel);
        backgroundLabel.setIcon(new ImageIcon(scaleBackground("res/bak/cadre_rouge.png", 380, 250)));

        // Panel pour afficher les unités
        panelUnites = new JPanel();
        panelUnites.setLayout(null); // On retire le layout ici
        panelUnites.setOpaque(false);
        panelUnites.setBounds(10, 60, 380, 200); // Définir la taille du panel des unités
        add(panelUnites);
        
        setComponentZOrder(backgroundLabel, getComponentCount() - 1);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void updateCadreInfo(ArrayList<Troupe> liste, int equipeActuelle) {
        System.out.println("Updating cadreInfo, equipe = " + equipeActuelle);

        lblNomJoueur.setText("Joueur " + (equipeActuelle + 1));
        for(int i = 0; i < 3; i++) {
        	afficherUnitesManuellement(liste);
        }
        

        if (equipeActuelle == 0) {
            setBackground(new Color(255, 0, 0)); // Rouge
            backgroundLabel.setIcon(new ImageIcon(scaleBackground("res/bak/cadre_rouge.png", 380, 250)));
        } else if (equipeActuelle == 1) {
            setBackground(new Color(0, 0, 139)); // Bleu foncé
            backgroundLabel.setIcon(new ImageIcon(scaleBackground("res/bak/cadre_bleu.png", 380, 250)));
        }

        revalidate();
        repaint();
    }

    private void afficherUnitesManuellement(ArrayList<Troupe> liste) {
        panelUnites.removeAll(); // Effacer les éléments précédents
        int x = 20;
        int y = 10;
        int spacing = 10;
        int columnSpacing = 30;
        int boxWidth = 50;
        int boxHeight = 50;
        int maxRows = 3; // Nombre d'images par colonne

        Color fondBrunPale = new Color(222, 184, 135); // Fond brun pâle pour chaque unité

        // Parcours des troupes et placement
        for (int i = 0; i < liste.size(); i++) {
            Troupe troupe = liste.get(i);
            ImageIcon icon = new ImageIcon(scaleImage(troupe.getImage(), 48, 48));
            
            PanelTroupe panelTroupe = new PanelTroupe(troupe, icon);
            panelTroupe.setBounds(x, y, boxWidth, boxHeight);
            panelTroupe.setToolTipText(troupe.getNom());

            panelUnites.add(panelTroupe);

            y += boxHeight + spacing;

            if ((i + 1) % maxRows == 0) {
                y = 10;
                x += boxWidth + columnSpacing;
            }
        }


        panelUnites.revalidate();
        panelUnites.repaint();
    }

    // Méthode pour redimensionner l'image
    public Image scaleImage(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image image = icon.getImage();
        return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    private BufferedImage scaleImage(BufferedImage original, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaledImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, width, height, null);
        g.dispose();
        return scaledImage;
    }
    
 // Méthode pour charger et redimensionner une image depuis un chemin
    public Image scaleBackground(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image image = icon.getImage();
        return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
}


package jeu_oupi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JPanel;

import plateau.Tuile;
import troupe.Troupe;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * La classe {@code ZoneAnimationOupi} représente la zone d'animation pour le jeu Oupi.
 * Elle étend {@link JPanel} et implémente {@link Runnable} pour gérer l'animation du jeu.
 * 
 * @author Badr Rifki
 * 
 */
public class ZoneAnimationOupi extends JPanel implements Runnable {

    private static final long serialVersionUID = 1L;
    
    private JeuxOupi jeuxOupi;
    
    final int FPS = 27;

    Thread threadJeu;
    
    // Ajouter le support pour lancer des événements de type PropertyChange
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Constructeur de la classe {@code ZoneAnimationOupi}.
     * 
     * @param screenWidth la largeur de l'écran
     * @param screenHeight la hauteur de l'écran
     */
    public ZoneAnimationOupi(int screenWidth, int screenHeight) {
        jeuxOupi = new JeuxOupi(screenWidth, screenHeight);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                // Essayer de sélectionner une troupe d'abord
                Troupe cliquee = jeuxOupi.getTroupeA(x, y);
                if (cliquee != null) {
                    jeuxOupi.selectionnerTroupe(cliquee);
                    System.out.println("Troupe sélectionnée à : (" + cliquee.getCol() + "," + cliquee.getLig() + ")");
                    return;
                }

                // Si aucune troupe n'a été cliquée, gérer le clic sur une tuile
                int ligne = y / JeuxOupi.tailleTuile;
                int colonne = x / JeuxOupi.tailleTuile;

                // Vérifier si le clic est dans les limites du plateau
                if (ligne >= 0 && ligne < JeuxOupi.getNbTuiles() && colonne >= 0 && colonne < JeuxOupi.getNbTuiles()) {
                    Tuile tuileCliquee = jeuxOupi.getPlateau().getTuile(ligne, colonne);
                    System.out.println("Tuile cliquée : Ligne " + (ligne + 1) + ", Colonne " + (colonne + 1));
                }
            }
        });
        
        // Ajouter un écouteur de clavier pour le déplacement avec WASD
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                    case KeyEvent.VK_UP:
                        jeuxOupi.deplacerTroupeSelectionneeHaut();
                        break;
                    case KeyEvent.VK_A: 
                    case KeyEvent.VK_LEFT:
                        jeuxOupi.deplacerTroupeSelectionneeGauche();
                        break;
                    case KeyEvent.VK_S:  
                    case KeyEvent.VK_DOWN:
                        jeuxOupi.deplacerTroupeSelectionneeBas();
                        break;
                    case KeyEvent.VK_D: 
                    case KeyEvent.VK_RIGHT:
                        jeuxOupi.deplacerTroupeSelectionneeDroite();
                        break;
                    case KeyEvent.VK_R: // R
                        jeuxOupi.resetTroupeAct();
                        break;
                         
                }
                repaint();
            }
        });
    }

    /**
     * Méthode pour ajouter un écouteur de changement de propriété.
     * 
     * @param listener l'écouteur de changement de propriété
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    /**
     * Méthode exécutée par le thread de jeu pour gérer l'animation.
     */
    @Override
    public void run() {
        double intervaleDessin = 1000000000 / FPS;
        double delta = 0;
        long tempsAv = System.nanoTime();
        long tempsAct;
        
        while (threadJeu != null) {
            tempsAct = System.nanoTime();
            
            delta += (tempsAct - tempsAv) / intervaleDessin;
            tempsAv = tempsAct;
            
            if (delta >= 1) {
                miseAJour();
                repaint();
                delta--;
            }
        }
    }

    /**
     * Dessine le panneau.
     * 
     * @param g l'objet {@link Graphics} utilisé pour dessiner
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        jeuxOupi.dessiner(g2d);
    }

    /**
     * Met à jour l'état du jeu.
     */
    public void miseAJour() {
        // Implémenter la logique de mise à jour du jeu ici
    }

    /**
     * Démarre le thread de jeu.
     */
    public void demarer() {
        threadJeu = new Thread(this);
        threadJeu.start();
        requestFocus(); // Demander le focus pour garantir que les entrées du clavier fonctionnent
    }
}

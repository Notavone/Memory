package fr.notavone;

import javax.swing.*;

public class Menu extends JMenuBar {
    private final JMenuItem restart;
    private final JMenuItem scores;
    private final JMenuItem grid22;
    private final JMenuItem grid44;
    private final JMenuItem grid66;

    public Menu() {
        this.restart = new JMenuItem("Nouvelle partie");
        this.scores = new JMenuItem("Scores");
        this.grid22 = new JMenuItem("Grille 2x2");
        this.grid44 = new JMenuItem("Grille 4x4");
        this.grid66 = new JMenuItem("Grille 6x6");

        JMenu difficulties = new JMenu("Difficultés");
        difficulties.add(this.grid22);
        difficulties.add(this.grid44);
        difficulties.add(this.grid66);
        JMenu menu = new JMenu("Options");
        menu.add(this.restart);
        menu.add(this.scores);
        this.add(menu);
        this.add(difficulties);
    }

    public JMenuItem getRestart() {
        return this.restart;
    }

    public JMenuItem getScores() {
        return this.scores;
    }

    public JMenuItem getGrid22() {
        return this.grid22;
    }

    public JMenuItem getGrid44() {
        return this.grid44;
    }

    public JMenuItem getGrid66() {
        return this.grid66;
    }
}

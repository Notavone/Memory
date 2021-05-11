package fr.notavone;

import javax.swing.*;

public class Dialogs {
    public static class LoseDialog extends JOptionPane {
        public LoseDialog(Window window) {
            super();
            showMessageDialog(window.getContentPane(), "Tu à perdu(e), mais tu peux retenter ta chance !", "Perdu !", INFORMATION_MESSAGE);
        }
    }

    public static class WinDialog extends JOptionPane {
        public WinDialog(Window window, Model model) {
            super();
            String score = model.getChrono().getTime();
            String message = String.format("Bravo, tu à gagné en %s secondes !\n%s", score, model.getScoresDisplay());
            showMessageDialog(window.getContentPane(), message, "C'est gagné !", INFORMATION_MESSAGE);
        }
    }

    public static class ScoresDialog extends JOptionPane {
        public ScoresDialog(Window window, Model model) {
            super();
            String message = String.format("%s\n%s", "Voici les meilleurs scores :", model.getScoresDisplay());
            showMessageDialog(window.getContentPane(), message, "Scores", INFORMATION_MESSAGE);
        }
    }

}

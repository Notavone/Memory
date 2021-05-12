package fr.notavone.Memory;

import javax.swing.*;
import java.io.IOException;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        SwingUtilities.invokeLater(() -> {
            try {
                new Game.Controller(new Game.Model(16));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
package fr.notavone;
import javax.swing.*;

public class Window extends JFrame {
    public Window(String title) {
        this.setTitle(title);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
    }
}

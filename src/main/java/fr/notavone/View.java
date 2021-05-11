package fr.notavone;

import javax.swing.*;
import java.awt.*;

public class View extends JPanel {
    private final Model model;
    private JLabel tries;
    private JLabel time;

    public View(Model model) {
        this.model = model;
        this.instantiate();
    }

    private void instantiate() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.tries = new JLabel("", SwingConstants.CENTER);

        this.time = new JLabel(model.getChrono().getDisplayTime(), SwingConstants.CENTER);

        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new GridLayout((int) this.model.getColumnInfo().getWidth(), (int) this.model.getColumnInfo().getHeight()));
        for (JButton button : this.model.getButtons()) {
            imagePanel.add(button);
        }


        this.setTries(model.getTriesLeft());
        JPanel timePanel = new JPanel();
        timePanel.add(this.time);
        timePanel.setAlignmentX(CENTER_ALIGNMENT);

        JPanel triesPanel = new JPanel();
        triesPanel.add(this.tries);
        triesPanel.setAlignmentX(CENTER_ALIGNMENT);

        this.add(timePanel);
        this.add(imagePanel);
        this.add(triesPanel);
    }

    public void setTries(int triesLeft) {
        this.tries.setText("Essais restants : " + triesLeft);
    }

    public JLabel getTime() {
        return time;
    }
}
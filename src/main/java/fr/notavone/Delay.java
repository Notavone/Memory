package fr.notavone;

import javax.swing.*;
import java.awt.event.ActionListener;

public class Delay extends Timer {
    public Delay(int delay, ActionListener listener) {
        super(delay, listener);
        this.setRepeats(false);
        this.start();
    }
}

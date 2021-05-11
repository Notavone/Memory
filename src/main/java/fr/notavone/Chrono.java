package fr.notavone;

import javax.swing.*;
import java.text.DecimalFormat;

@SuppressWarnings({"BusyWait"})
public class Chrono extends Thread {
    private JLabel label;
    private float time;
    private boolean run;

    public Chrono() {
        this.time = 0;
        this.run = true;
    }

    @Override
    public void run() {
        while (run) {
            try {
                sleep(100);
                this.time += 0.1;
                label.setText(this.getDisplayTime());
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        this.run = false;
    }

    public void setLabel(JLabel label) {
        this.label = label;
        this.label.setText(this.getDisplayTime());
    }

    public String getTime() {
        DecimalFormat format = new DecimalFormat("#######0.0");
        this.interrupt();
        return format.format(this.time);
    }

    public String getDisplayTime() {
        DecimalFormat format = new DecimalFormat("#######0.0");
        return String.format("Temps écoulé : %ss", format.format(this.time));
    }
}
package fr.notavone;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;

public class Model {
    private static final ClassLoader cl = Model.class.getClassLoader();
    private static final String[] AVAILABLE_IMAGES = new String[]{"0.png", "1.png", "2.png", "3.png", "4.png", "5.png", "6.png", "7.png", "8.png", "9.png", "10.png", "11.png", "12.png", "13.png", "14.png", "15.png", "16.png", "17.png"};
    private static final String IMAGE_PATH = "images/";
    private static final Integer MAX_REGISTERED_SCORES = 3;
    private static File tempFile;

    static {
        try {
            tempFile = File.createTempFile("memory", ".tmp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final ArrayList<Float> scores = new ArrayList<>();

    private int size;
    private ArrayList<JButton> buttons;
    private Chrono chrono;
    private int triesLeft;
    private boolean gameStarted;

    public Model() {
        this.instantiate(16);
    }

    public Model(int size) {
        this.instantiate(size);
    }

    private void instantiate(int size) {
        this.size = size;
        this.buttons = new ArrayList<>();
        this.chrono = new Chrono();
        this.triesLeft = size / 2;
        this.gameStarted = false;

        try {
            FileInputStream in = new FileInputStream(tempFile.getPath());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            for (int i = 0; i < MAX_REGISTERED_SCORES; i++) {
                String s = br.readLine();
                if (s == null) {
                    s = "0.0";
                }
                float v = Float.parseFloat(s);
                if (scores.size() <= i) {
                    scores.add(v);
                } else {
                    scores.set(i, v);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Vector<Integer> v = new Vector<>();
        for (int i = 0; i < size; i++) {
            v.add(i % (size / 2));
        }

        for (int i = 0; i < size; i++) {
            int rand = (int) (Math.random() * v.size());
            String reference = AVAILABLE_IMAGES[v.elementAt(rand)];
            JButton button = new JButton();
            Color color = new Color(255, 255, 255);


            button.setPreferredSize(new Dimension(120, 120));
            button.setForeground(color);
            button.setBackground(color);
            try {
                ImageInputStream disabledIconInputStream = ImageIO.createImageInputStream(Objects.requireNonNull(cl.getResourceAsStream(IMAGE_PATH + "memory/" + reference)));
                Image disabledIconImage = ImageIO.read(disabledIconInputStream);
                ReferencedIcon disabledIcon = new ReferencedIcon(disabledIconImage, reference);

                ImageInputStream enabledIconInputStream = ImageIO.createImageInputStream(Objects.requireNonNull(cl.getResourceAsStream(IMAGE_PATH + "no_image.png")));
                Image enabledIconImage = ImageIO.read(enabledIconInputStream);
                ReferencedIcon enabledIcon = new ReferencedIcon(enabledIconImage, "no_image.png");

                button.setDisabledIcon(disabledIcon);
                button.setIcon(enabledIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }

            this.buttons.add(button);
            v.removeElementAt(rand);
        }
    }

    public void updateScores() {
        Chrono chrono = this.chrono;
        String score = chrono.getTime();
        DecimalFormat format = new DecimalFormat("#######0.0");

        float s = Float.parseFloat(format.format(Float.parseFloat(score)));
        for (int i = 0; i < MAX_REGISTERED_SCORES; i++) {
            if (this.scores.get(i) >= s || this.scores.get(i) == 0.0) {
                this.scores.add(i, s);
                break;
            }
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile.getPath()));
            for (int i = 0; i < MAX_REGISTERED_SCORES; i++) {
                bw.write("" + format.format(this.scores.get(i)));
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getScoresDisplay() {
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < MAX_REGISTERED_SCORES; i++) {
            strings.add((i + 1) + ". " + this.scores.get(i) + "s");
        }
        return String.join("\n", strings);
    }

    public ArrayList<JButton> getButtons() {
        return buttons;
    }

    public Chrono getChrono() {
        return chrono;
    }

    public int getTriesLeft() {
        return triesLeft;
    }

    public void decrementTries() {
        this.triesLeft--;
    }

    public Dimension getColumnInfo() {
        return new Dimension((int) Math.sqrt(this.size), (int) Math.sqrt(this.size));
    }

    public boolean isGameStarted() {
        return this.gameStarted;
    }

    public int getSize() {
        return size;
    }

    public void startGame() {
        this.gameStarted = true;
        this.chrono.start();
    }
}

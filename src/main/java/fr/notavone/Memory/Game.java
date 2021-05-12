package fr.notavone.Memory;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Vector;
import java.util.stream.Collectors;

public class Game {
    public static class Controller {
        private final JFrame window;
        private final Utilities.Menu menu;
        private Model model;
        private View view;

        public Controller(Model model) throws IOException {
            this.menu = new Utilities.Menu();
            this.window = new JFrame("Memory");

            this.window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            this.window.setResizable(false);
            this.window.setJMenuBar(menu);

            this.reset(model);
        }

        public void reset(Model model) {
            this.model = model;
            this.view = new View(model);

            this.window.setVisible(false);
            this.window.setContentPane(view);
            this.window.pack();
            this.window.setLocationRelativeTo(null);
            Utilities.timer(200, (ignored) -> this.window.setVisible(true));
            this.model.getIcon().ifPresent(window::setIconImage);

            for (JButton button : this.model.getButtons()) {
                button.addActionListener(new Utilities.CustomActionListener.ButtonActionListener(this));
            }

            menu.getRestart().addActionListener(new Utilities.CustomActionListener.ResetButtonActionListener(this));
            menu.getScores().addActionListener(new Utilities.CustomActionListener.ScoresButtonActionListener(this));
            menu.getGrid22().addActionListener(new Utilities.CustomActionListener.Grid22ActionListener(this));
            menu.getGrid33().addActionListener(new Utilities.CustomActionListener.Grid33ActionListener(this));
            menu.getGrid44().addActionListener(new Utilities.CustomActionListener.Grid44ActionListener(this));
            menu.getGrid55().addActionListener(new Utilities.CustomActionListener.Grid55ActionListener(this));
            menu.getGrid66().addActionListener(new Utilities.CustomActionListener.Grid66ActionListener(this));
        }

        public JFrame getWindow() {
            return this.window;
        }

        public Model getModel() {
            return this.model;
        }

        public View getView() {
            return this.view;
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static class Model {
        private static final String[] AVAILABLE_IMAGES = new String[]{"0.png", "1.png", "2.png", "3.png", "4.png", "5.png", "6.png", "7.png", "8.png", "9.png", "10.png", "11.png", "12.png", "13.png", "14.png", "15.png", "16.png", "17.png", "trap.png"};
        private static final Integer MAX_REGISTERED_SCORES = 3;
        private static final String TEMP_PATH;

        private final Optional<Image> icon;
        private final ArrayList<Float> scores;
        private final ArrayList<JButton> buttons;
        private final Utilities.Chrono chrono;
        private final int size;

        private int tries;
        private boolean gameStarted;

        static {
            String path = "";
            try {
                File file = File.createTempFile("memory", ".tmp");
                path = file.getPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
            TEMP_PATH = path;
        }

        public Model(int size) throws IOException {
            this.size = size;
            this.buttons = new ArrayList<>();
            this.chrono = new Utilities.Chrono();
            this.scores = new ArrayList<>();
            this.tries = this.size / 2;
            this.icon = Utilities.loadImage("images/icon.png");
            this.instantiate(this.size);
        }

        private void instantiate(int size) throws IOException {
            this.gameStarted = false;

            FileInputStream in = new FileInputStream(TEMP_PATH);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            for (int i = 0; i < MAX_REGISTERED_SCORES; i++) {
                String s = br.readLine();
                if (s == null) s = "0.0";
                float v = Float.parseFloat(s);
                if (scores.size() <= i) {
                    scores.add(v);
                } else {
                    scores.set(i, v);
                }
            }
            br.close();

            Vector<Integer> v = new Vector<>();
            for (int i = 0; i < size - size % 2; i++) {
                v.add(i % (size / 2));
            }

            if (size % 2 != 0) v.add(AVAILABLE_IMAGES.length - 1);

            for (int i = 0; i < size; i++) {
                int rand = (int) (Math.random() * v.size());
                String reference = AVAILABLE_IMAGES[v.elementAt(rand)];
                this.buttons.add(new Utilities.MemoryButton(reference));
                v.removeElementAt(rand);
            }
        }

        public void updateScores() throws IOException {
            Utilities.Chrono chrono = this.chrono;
            String score = chrono.getTime();
            DecimalFormat format = new DecimalFormat("#######0.0");

            float s = Float.parseFloat(format.format(Float.parseFloat(score)));
            for (int i = 0; i < MAX_REGISTERED_SCORES; i++) {
                if (this.scores.get(i) >= s || this.scores.get(i) == 0.0) {
                    this.scores.add(i, s);
                    break;
                }
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(TEMP_PATH));
            for (int i = 0; i < MAX_REGISTERED_SCORES; i++) {
                bw.write("" + format.format(this.scores.get(i)));
                bw.newLine();
            }
            bw.close();
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

        public Utilities.Chrono getChrono() {
            return chrono;
        }

        public int getTries() {
            return tries;
        }

        public void decrementTries() {
            this.tries--;
        }

        public int getSize() {
            return size;
        }

        public boolean isGameStarted() {
            return this.gameStarted;
        }

        public void startGame() {
            this.gameStarted = true;
            this.chrono.start();
        }

        public Optional<Image> getIcon() {
            return icon;
        }
    }

    public static class View extends JPanel {
        private final Model model;
        private JLabel tries;

        public View(Model model) {
            this.model = model;
            this.instantiate();
        }

        private void instantiate() {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.tries = new JLabel("", SwingConstants.CENTER);
            JLabel time = new JLabel("", SwingConstants.CENTER);

            model.getChrono().setLabel(time);

            JPanel imagePanel = new JPanel();
            int s = (int) Math.sqrt(this.model.getSize());
            imagePanel.setLayout(new GridLayout(s, s));
            for (JButton button : this.model.getButtons()) {
                imagePanel.add(button);
            }


            this.setTries(model.getTries());
            JPanel timePanel = new JPanel();
            timePanel.add(time);
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
    }

    public static class Utilities {
        private static final ClassLoader cl = Utilities.class.getClassLoader();

        public static void timer(int delay, ActionListener listener) {
            Timer t = new Timer(delay, listener);
            t.setRepeats(false);
            t.start();
        }

        public static Optional<Image> loadImage(String s) throws IOException {
            Image image = null;
            ImageInputStream imageStream;
            InputStream resourceStream;

            resourceStream = cl.getResourceAsStream(s);
            if (resourceStream != null) {
                imageStream = ImageIO.createImageInputStream(resourceStream);
                image = ImageIO.read(imageStream);
            }
            return Optional.ofNullable(image);
        }

        public static class ReferencedIcon extends ImageIcon {
            private final String reference;

            public ReferencedIcon(Image image, String reference) {
                super(image.getScaledInstance(120, 120, Image.SCALE_SMOOTH));
                this.reference = reference;
            }

            public String getReference() {
                return reference;
            }
        }

        public static class MemoryButton extends JButton {
            private static final String IMAGE_PATH = "images/memory/";
            private static final Image NO_IMAGE;

            static {
                Image image = null;
                try {
                    Optional<Image> optional = loadImage("images/no_image.png");
                    if (optional.isPresent()) {
                        image = optional.get();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                NO_IMAGE = image;
            }

            public MemoryButton(String reference) throws IOException {
                Optional<Image> optional = loadImage(IMAGE_PATH + reference);
                Dimension dimension = new Dimension(120, 120);

                this.setPreferredSize(dimension);
                this.setIcon(new ImageIcon(NO_IMAGE));
                optional.ifPresent(image -> this.setDisabledIcon(new ReferencedIcon(image, reference)));
            }
        }

        public static class Dialogs {
            public static class LoseDialog extends JOptionPane {
                public LoseDialog(JFrame window) {
                    super();
                    showMessageDialog(window.getContentPane(), "Tu à perdu(e), mais tu peux retenter ta chance !", "Perdu !", INFORMATION_MESSAGE);
                }
            }

            public static class WinDialog extends JOptionPane {
                public WinDialog(JFrame window, Model model) {
                    super();
                    String score = model.getChrono().getTime();
                    String message = String.format("Bravo, tu à gagné en %s secondes !\n%s", score, model.getScoresDisplay());
                    showMessageDialog(window.getContentPane(), message, "C'est gagné !", INFORMATION_MESSAGE);
                }
            }

            public static class ScoresDialog extends JOptionPane {
                public ScoresDialog(JFrame window, Model model) {
                    super();
                    String message = String.format("%s\n%s", "Voici les meilleurs scores :", model.getScoresDisplay());
                    showMessageDialog(window.getContentPane(), message, "Scores", INFORMATION_MESSAGE);
                }
            }

        }

        public static class CustomActionListener {
            public static class ButtonActionListener implements ActionListener {
                private final Controller controller;
                private final Model model;
                private final View view;
                private final JFrame window;

                private static int disabledButtonCount = 0;
                private static JButton lastDisabledButton = null;
                private static final Image TRAP_IMAGE;

                static {
                    Image image = new ImageIcon().getImage();
                    try {
                        Optional<Image> optional = loadImage("images/memory/trap.png");
                        if (optional.isPresent()) {
                            image = optional.get();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    TRAP_IMAGE = image;
                }

                private final ReferencedIcon trap;

                public ButtonActionListener(Controller controller) {
                    this.controller = controller;
                    this.model = controller.getModel();
                    this.view = controller.getView();
                    this.window = controller.getWindow();
                    this.trap = new ReferencedIcon(TRAP_IMAGE, "trap.png");
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton button = (JButton) e.getSource();
                    if (!model.isGameStarted()) {
                        model.startGame();
                    }

                    button.setEnabled(false);
                    disabledButtonCount++;

                    ReferencedIcon thisIcon = (ReferencedIcon) button.getDisabledIcon();
                    boolean isTrap = thisIcon.getReference().equals(this.trap.getReference());
                    if (isTrap) {
                        model.decrementTries();
                        view.setTries(model.getTries());
                        if (lastDisabledButton != null) {
                            JButton lastButton = lastDisabledButton;
                            lastDisabledButton = null;
                            timer(1000, (ignored) -> lastButton.setEnabled(true));
                        }

                        disabledButtonCount = 0;
                    }

                    if (disabledButtonCount == 2) {
                        ReferencedIcon thatIcon = (ReferencedIcon) lastDisabledButton.getDisabledIcon();
                        boolean isPair = thisIcon.getReference().equals(thatIcon.getReference());

                        if (!isPair) {
                            model.decrementTries();
                            view.setTries(model.getTries());
                            JButton lastButton = lastDisabledButton;
                            timer(1000, ((ignored) -> {
                                button.setEnabled(true);
                                lastButton.setEnabled(true);
                            }));
                        }
                        disabledButtonCount = 0;
                    }

                    ArrayList<JButton> enabledButtons = (ArrayList<JButton>) model.getButtons().stream().filter(Component::isEnabled).collect(Collectors.toList());
                    if (enabledButtons.size() == 0) {
                        try {
                            model.updateScores();
                            controller.reset(new Model(controller.getModel().getSize()));
                            new Dialogs.WinDialog(window, model);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }

                    lastDisabledButton = button;

                    if (model.getTries() == 0) {
                        try {
                            model.getChrono().interrupt();
                            controller.reset(new Model(controller.getModel().getSize()));
                            new Dialogs.LoseDialog(window);
                            timer(1000, (ignored) -> {
                                for (JButton btn : model.getButtons()) {
                                    btn.setEnabled(false);
                                }
                            });
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }
            }

            public static class ResetButtonActionListener implements ActionListener {
                private final Controller controller;

                public ResetButtonActionListener(Controller controller) {
                    this.controller = controller;
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        this.controller.reset(new Model(controller.getModel().getSize()));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }

            public static class ScoresButtonActionListener implements ActionListener {
                private final Controller controller;

                public ScoresButtonActionListener(Controller controller) {
                    this.controller = controller;
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    JFrame window = this.controller.getWindow();
                    Model model = this.controller.getModel();
                    new Dialogs.ScoresDialog(window, model);
                }
            }

            public static class Grid22ActionListener implements ActionListener {
                private final Controller controller;

                public Grid22ActionListener(Controller controller) {
                    this.controller = controller;
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        this.controller.reset(new Model(4));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }

            public static class Grid33ActionListener implements ActionListener {
                private final Controller controller;

                public Grid33ActionListener(Controller controller) {
                    this.controller = controller;
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        this.controller.reset(new Model(9));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }

            public static class Grid44ActionListener implements ActionListener {
                private final Controller controller;

                public Grid44ActionListener(Controller controller) {
                    this.controller = controller;
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        this.controller.reset(new Model(16));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }

            public static class Grid55ActionListener implements ActionListener {
                private final Controller controller;

                public Grid55ActionListener(Controller controller) {
                    this.controller = controller;
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        this.controller.reset(new Model(25));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }


            public static class Grid66ActionListener implements ActionListener {
                private final Controller controller;

                public Grid66ActionListener(Controller controller) {
                    this.controller = controller;
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        this.controller.reset(new Model(36));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        }

        @SuppressWarnings({"BusyWait"})
        public static class Chrono extends Thread {
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
                return format.format(this.time);
            }

            public String getDisplayTime() {
                return String.format("Temps écoulé : %ss", this.getTime());
            }
        }

        public static class Menu extends JMenuBar {
            private final JMenuItem restart;
            private final JMenuItem scores;
            private final JMenuItem grid22;
            private final JMenuItem grid33;
            private final JMenuItem grid44;
            private final JMenuItem grid55;
            private final JMenuItem grid66;

            public Menu() {
                this.restart = new JMenuItem("Nouvelle partie");
                this.scores = new JMenuItem("Scores");
                this.grid22 = new JMenuItem("Grille 2x2");
                this.grid33 = new JMenuItem("Grille 3x3");
                this.grid44 = new JMenuItem("Grille 4x4");
                this.grid55 = new JMenuItem("Grille 5x5");
                this.grid66 = new JMenuItem("Grille 6x6");

                JMenu difficulties = new JMenu("Difficultés");
                difficulties.add(this.grid22);
                difficulties.add(this.grid33);
                difficulties.add(this.grid44);
                difficulties.add(this.grid55);
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

            public JMenuItem getGrid33() {
                return this.grid33;
            }

            public JMenuItem getGrid44() {
                return this.grid44;
            }

            public JMenuItem getGrid55() {
                return this.grid55;
            }

            public JMenuItem getGrid66() {
                return this.grid66;
            }
        }
    }
}

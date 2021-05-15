import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;
import java.util.stream.Collectors;

public class Game {
    public static class Controller {
        private final JFrame window;
        private Model model;
        private View view;

        public Controller(Model model) {
            this.window = new JFrame("Memory");
            this.window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            this.window.setResizable(false);
            this.window.setJMenuBar(new Menu(this, Model.getSizes()));
            this.reset(model);
        }

        public void reset(Model model) {
            this.model = model;
            this.view = new View(model);
            this.window.setVisible(false);
            this.window.setContentPane(view);
            this.window.pack();
            this.window.setLocationRelativeTo(null);
            this.window.setIconImage(Model.getIcon());
            for (JButton button : this.model.getButtons()) {
                button.addActionListener(new ButtonActionListener(this));
            }

            Utilities.timer(200, (ignored) -> this.window.setVisible(true));
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

    public static class Model {
        private static final String[] AVAILABLE_IMAGES = new String[]{"0.png", "1.png", "2.png", "3.png", "4.png", "5.png", "6.png", "7.png", "8.png", "9.png", "10.png", "11.png", "12.png", "13.png", "14.png", "15.png", "16.png", "17.png", "trap.png"};
        private static final Integer MAX_REGISTERED_SCORES = 3;
        private static final String TEMP_PATH = Utilities.createTempFile();
        private static final Image ICON = Utilities.loadImage("images/icon.png");
        private static final int[] SIZES = new int[]{3, 4, 5, 6};

        private final ArrayList<Float> scores;
        private final ArrayList<JButton> buttons;
        private final Chrono chrono;
        private final int columns;

        private int tries;
        private boolean gameStarted;

        public Model(int columns) {
            this.columns = columns;
            this.buttons = new ArrayList<>();
            this.chrono = new Chrono();
            this.scores = new ArrayList<>();
            this.tries = 1000;
            this.gameStarted = false;
            this.fetchScores();

            int numberOfImage = columns * columns;
            Vector<Integer> v = new Vector<>();
            for (int i = 0; i < numberOfImage - numberOfImage % 2; i++) {
                v.add(i % (numberOfImage / 2));
            }

            if (numberOfImage % 2 != 0) v.add(AVAILABLE_IMAGES.length - 1);

            for (int i = 0; i < numberOfImage; i++) {
                int rand = (int) (Math.random() * v.size());
                String reference = AVAILABLE_IMAGES[v.elementAt(rand)];
                this.buttons.add(new MemoryButton(reference));
                v.removeElementAt(rand);
            }
        }

        public void fetchScores() {
            try {
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
            } catch (IOException e) {
                e.printStackTrace();
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
                BufferedWriter bw = new BufferedWriter(new FileWriter(TEMP_PATH));
                for (int i = 0; i < MAX_REGISTERED_SCORES; i++) {
                    bw.write("" + format.format(this.scores.get(i)));
                    bw.newLine();
                }
                bw.close();
            } catch (IOException e) {
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

        public int getColumns() {
            return columns;
        }

        public ArrayList<JButton> getButtons() {
            return buttons;
        }

        public Chrono getChrono() {
            return chrono;
        }

        public int getTries() {
            return tries;
        }

        public void decrementTries() {
            this.tries--;
        }

        public boolean isGameStarted() {
            return this.gameStarted;
        }

        public void startGame() {
            this.gameStarted = true;
            this.chrono.start();
        }

        public static Image getIcon() {
            return ICON;
        }

        public static int[] getSizes() {
            return SIZES;
        }
    }

    public static class View extends JPanel {
        private final JLabel tries;

        public View(Model model) {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.tries = new JLabel("", SwingConstants.CENTER);
            JLabel time = new JLabel("", SwingConstants.CENTER);

            model.getChrono().setLabel(time);

            JPanel imagePanel = new JPanel();
            int columns = model.getColumns();
            imagePanel.setLayout(new GridLayout(columns, columns));
            for (JButton button : model.getButtons()) {
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
            this.tries.setText("Tries left : " + triesLeft);
        }
    }


    public static class ReferencedIcon extends ImageIcon {
        private final String reference;

        public ReferencedIcon(Image image, String reference) {
            super(image);
            this.reference = reference;
        }

        public String getReference() {
            return reference;
        }
    }


    public static class MemoryButton extends JButton {
        private static final String IMAGE_PATH = "images/memory/";
        private static final Image NO_IMAGE = Utilities.loadImage("images/no_image.png");

        public MemoryButton(String reference) {
            Image image = Utilities.loadImage(IMAGE_PATH + reference);
            Dimension dimension = new Dimension(120, 120);

            this.setPreferredSize(dimension);
            this.setIcon(new ImageIcon(NO_IMAGE));
            this.setDisabledIcon(new ReferencedIcon(image, reference));
        }
    }

    public static class Dialogs {
        public static void showLoseDialog(JFrame window) {
            JOptionPane.showMessageDialog(window, "You lost, try again !", "You lost !", JOptionPane.INFORMATION_MESSAGE);
        }

        public static void showWinDialog(JFrame window, Model model) {
            String score = model.getChrono().getTime();
            String message = String.format("Congrats ! you won in %s seconds\n%s", score, model.getScoresDisplay());
            JOptionPane.showMessageDialog(window.getContentPane(), message, "You won !", JOptionPane.INFORMATION_MESSAGE);
        }

        public static void showScoresDialog(JFrame window, Model model) {
            String message = String.format("%s\n%s", "Best scores :", model.getScoresDisplay());
            JOptionPane.showMessageDialog(window.getContentPane(), message, "Scores", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    public static class ButtonActionListener implements ActionListener {
        private final Controller controller;
        private final Model model;
        private final View view;
        private final JFrame window;

        private static int disabledButtonCount = 0;
        private static JButton lastDisabledButton = null;
        private static final Image TRAP_IMAGE = Utilities.loadImage("images/memory/trap.png");

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
            button.setEnabled(false);
            ReferencedIcon thisIcon = (ReferencedIcon) button.getDisabledIcon();
            disabledButtonCount++;

            if (!model.isGameStarted()) {
                model.startGame();
            }

            if (thisIcon.getReference().equals(this.trap.getReference())) {
                model.decrementTries();
                view.setTries(model.getTries());
                if (lastDisabledButton != null) {
                    JButton lastButton = lastDisabledButton;
                    Utilities.timer(1000, (ignored) -> lastButton.setEnabled(true));
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
                    Utilities.timer(500, ((ignored) -> {
                        button.setEnabled(true);
                        lastButton.setEnabled(true);
                    }));
                }
                disabledButtonCount = 0;
            }

            ArrayList<JButton> enabledButtons = (ArrayList<JButton>) model.getButtons().stream().filter(Component::isEnabled).collect(Collectors.toList());
            if (enabledButtons.size() == 0) {
                model.updateScores();
                controller.reset(new Model(controller.getModel().getColumns()));
                Dialogs.showWinDialog(window, model);
            }

            lastDisabledButton = button;

            if (model.getTries() == 0) {
                model.getChrono().interrupt();
                controller.reset(new Model(controller.getModel().getColumns()));
                Dialogs.showLoseDialog(window);
                Utilities.timer(1000, (ignored) -> model.getButtons().forEach(btn -> btn.setEnabled(false)));
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
            return String.format("Time : %ss", this.getTime());
        }
    }

    public static class Menu extends JMenuBar {
        public Menu(Controller controller, int[] sizes) {
            JMenuItem restart = new JMenuItem("New game");
            restart.addActionListener((ignored) -> controller.reset(new Model(controller.getModel().getColumns())));
            JMenuItem scores = new JMenuItem("Scores");
            scores.addActionListener((ignored) -> Dialogs.showScoresDialog(controller.getWindow(), controller.getModel()));

            JMenu difficulties = new JMenu("Grid sizes");
            for (int size : sizes) {
                JMenuItem item = new JMenuItem(String.format("%dx%s", size, size));
                item.addActionListener((ignored) -> controller.reset(new Model(size)));
                difficulties.add(item);
            }

            JMenu options = new JMenu("Options");
            options.add(restart);
            options.add(scores);

            this.add(options);
            this.add(difficulties);
        }
    }

    public static class Utilities {
        private static final ClassLoader cl = Utilities.class.getClassLoader();

        public static String createTempFile() {
            String path = "";
            try {
                File file = File.createTempFile("memory", ".tmp");
                path = file.getPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return path;
        }

        public static void timer(int delay, ActionListener listener) {
            Timer t = new Timer(delay, listener);
            t.setRepeats(false);
            t.start();
        }

        public static Image loadImage(String s) {
            Image image = null;
            try {
                InputStream resourceStream = cl.getResourceAsStream(s);
                if (resourceStream != null) {
                    ImageInputStream imageStream = ImageIO.createImageInputStream(resourceStream);
                    image = ImageIO.read(imageStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return image;
        }
    }
}

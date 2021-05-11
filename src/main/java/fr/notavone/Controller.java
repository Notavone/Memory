package fr.notavone;

import javax.swing.*;

public class Controller {
    private final Window window;
    private Model model;
    private View view;

    public Controller(Model model) {
        this.window = new Window("Memory");
        this.reset(model);
    }

    public void reset(Model model) {
        this.model = model;
        this.view = new View(model);
        new ViewController(this);
        new MenuController(this);

        this.window.setVisible(false);
        this.window.setContentPane(view);
        this.window.setLocationRelativeTo(null);
        this.window.pack();
        new Delay(200, (ignored) -> this.window.setVisible(true));
        model.getChrono().setLabel(view.getTime());
    }

    public Window getWindow() {
        return window;
    }

    public Model getModel() {
        return model;
    }

    public View getView() {
        return view;
    }

    private static class ViewController {
        public ViewController(Controller controller) {
            Window window = controller.getWindow();
            Model model = controller.getModel();
            View view = controller.getView();

            window.setContentPane(view);

            for (JButton button : model.getButtons()) {
                button.addActionListener(new CustomActionListener.ButtonActionListener(controller));
            }
        }
    }

    private static class MenuController {
        public MenuController(Controller controller) {
            Window window = controller.getWindow();
            Menu menu = new Menu();

            window.setJMenuBar(menu);
            menu.getRestart().addActionListener(new CustomActionListener.ResetButtonActionListener(controller));
            menu.getScores().addActionListener(new CustomActionListener.ScoresButtonActionListener(controller));
            menu.getGrid22().addActionListener(new CustomActionListener.Grid22ActionListener(controller));
            menu.getGrid44().addActionListener(new CustomActionListener.Grid44ActionListener(controller));
            menu.getGrid66().addActionListener(new CustomActionListener.Grid66ActionListener(controller));
        }
    }
}

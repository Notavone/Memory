package fr.notavone;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class CustomActionListener {
    public static class ButtonActionListener implements ActionListener {
        private final Controller controller;
        private final Model model;
        private final View view;
        private final Window window;

        private static int disabledButtonCount = 0;
        private static JButton lastDisabledButton = null;

        public ButtonActionListener(Controller controller) {
            this.controller = controller;
            this.model = controller.getModel();
            this.view = controller.getView();
            this.window = controller.getWindow();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            if (!model.isGameStarted()) {
                model.startGame();
            }

            button.setEnabled(false);
            disabledButtonCount++;

            if (disabledButtonCount == 2) {
                ReferencedIcon thatIcon = (ReferencedIcon) lastDisabledButton.getDisabledIcon();
                ReferencedIcon thisIcon = (ReferencedIcon) button.getDisabledIcon();
                boolean isPair = thisIcon.getReference().equals(thatIcon.getReference());

                if (!isPair) {
                    model.decrementTries();
                    view.setTries(model.getTriesLeft());
                    if (model.getTriesLeft() == 0) {
                        model.getChrono().interrupt();
                        new Delay(1000, (ignored) -> {
                            for (JButton btn : model.getButtons()) {
                                btn.setEnabled(false);
                            }
                        });
                        new Dialogs.LoseDialog(window);
                        controller.reset(new Model(controller.getModel().getSize()));
                    } else {
                        JButton lastButton = lastDisabledButton;
                        new Delay(1000, ((ignored) -> {
                            button.setEnabled(true);
                            lastButton.setEnabled(true);
                        }));
                    }
                } else {
                    ArrayList<JButton> enabledButtons = (ArrayList<JButton>) model.getButtons().stream().filter(Component::isEnabled).collect(Collectors.toList());
                    if (enabledButtons.size() == 0) {
                        model.updateScores();
                        new Dialogs.WinDialog(window, model);
                        controller.reset(new Model(controller.getModel().getSize()));
                    }
                }
                disabledButtonCount = 0;
            }
            lastDisabledButton = button;
        }
    }

    public static class ResetButtonActionListener implements ActionListener {
        private final Controller controller;

        public ResetButtonActionListener(Controller controller) {
            this.controller = controller;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.controller.reset(new Model(controller.getModel().getSize()));
        }
    }

    public static class ScoresButtonActionListener implements ActionListener {
        private final Controller controller;

        public ScoresButtonActionListener(Controller controller) {
            this.controller = controller;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Window window = this.controller.getWindow();
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
            this.controller.reset(new Model(4));
        }
    }

    public static class Grid44ActionListener implements ActionListener {
        private final Controller controller;

        public Grid44ActionListener(Controller controller) {
            this.controller = controller;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.controller.reset(new Model(16));
        }
    }


    public static class Grid66ActionListener implements ActionListener {
        private final Controller controller;

        public Grid66ActionListener(Controller controller) {
            this.controller = controller;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            this.controller.reset(new Model(36));
        }
    }
}

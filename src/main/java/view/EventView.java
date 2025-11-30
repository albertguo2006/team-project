package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import interface_adapter.events.EventOutcomeController;
import interface_adapter.events.EventState;
import interface_adapter.events.EventViewModel;
import interface_adapter.ViewManagerModel;

public class EventView extends JPanel implements ActionListener, PropertyChangeListener {
    private final String viewName = "Event";
    private final EventViewModel eventViewModel;
    private final ViewManagerModel viewManagerModel;

    private static final Color BACKGROUND_COLOUR = new Color(20, 20, 30);
    private static final Color TITLE_COLOUR = new Color(255, 215, 0);  // Gold
    private static final Color BUTTON_COLOUR = new Color(60, 60, 80);
    private static final Color BUTTON_HOVER_COLOUR = new Color(80, 80, 120);

    private EventOutcomeController eventOutcomeController;

    private final JLabel eventName;
    private final JLabel eventDescription;
    private final JPanel choicesPanel;
    private final JButton dismissButton;
    private final JButton nextButton;
    private final ArrayList<JButton> choiceButtons = new ArrayList<>();
    private final ArrayList<JLabel> choiceLabels = new ArrayList<>();

    private GamePanel gamePanel;
    private boolean showingOutcome = false;  // Track if we're showing outcome vs choices

    public EventView(EventViewModel eventviewModel, ViewManagerModel viewManagerModel) {
        this.eventViewModel = eventviewModel;
        this.eventViewModel.addPropertyChangeListener(this);
        this.viewManagerModel = viewManagerModel;

        this.setBackground(BACKGROUND_COLOUR);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(new EmptyBorder(50, 100, 50, 100));

        final JLabel title = new JLabel("Random Event!");
        title.setFont(new Font("Arial", Font.PLAIN, 30));
        title.setForeground(TITLE_COLOUR);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.add(title);
        this.add(Box.createRigidArea(new Dimension(0, 40)));

        eventName = new JLabel();
        eventName.setFont(new Font("Arial", Font.BOLD, 48));
        eventName.setAlignmentX(Component.CENTER_ALIGNMENT);
        eventName.setForeground(Color.WHITE);

        this.add(eventName);
        this.add(Box.createRigidArea(new Dimension(0, 20)));

        eventDescription = new JLabel();
        eventDescription.setFont(new Font("Arial", Font.PLAIN, 20));
        eventDescription.setForeground(Color.WHITE);
        eventDescription.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.add(eventDescription);
        this.add(Box.createRigidArea(new Dimension(0, 40)));

        // Panel for choice buttons
        choicesPanel = new JPanel();
        choicesPanel.setBackground(BACKGROUND_COLOUR);
        choicesPanel.setLayout(new BoxLayout(choicesPanel, BoxLayout.Y_AXIS));
        choicesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create 3 choice labels (max outcomes)
        for (int i = 0; i < 3; i++) {
            JLabel choiceLabel = createChoiceLabel(i);
            choiceLabels.add(choiceLabel);
            choicesPanel.add(choiceLabel);
            choicesPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        this.add(choicesPanel);
        this.add(Box.createRigidArea(new Dimension(0, 30)));

        // Dismiss button (hidden initially, shown after outcome)
        dismissButton = new JButton("Exit");
        dismissButton.setFont(new Font("Arial", Font.BOLD, 20));
        dismissButton.setForeground(Color.WHITE);
        dismissButton.setBackground(BUTTON_COLOUR);
        dismissButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        dismissButton.setMaximumSize(new Dimension(200, 50));
        dismissButton.setVisible(false);

        dismissButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dismissButton.setBackground(BUTTON_HOVER_COLOUR);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dismissButton.setBackground(BUTTON_COLOUR);
            }
        });

        dismissButton.addActionListener(e -> {
            clearAll();
            returnToGame();
        });

        // Next buttom (shown initially, hidden after outcome)
        nextButton = new JButton("Continue");
        nextButton.setFont(new Font("Arial", Font.BOLD, 20));
        nextButton.setForeground(Color.WHITE);
        nextButton.setBackground(BUTTON_COLOUR);
        nextButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        nextButton.setMaximumSize(new Dimension(200, 50));
        nextButton.setVisible(true);

        nextButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                nextButton.setBackground(BUTTON_HOVER_COLOUR);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                nextButton.setBackground(BUTTON_COLOUR);
            }
        });

        nextButton.addActionListener(e -> {
            final EventState state = eventViewModel.getState();
            eventOutcomeController.execute(state.getOutcomes());
            selectChoice();
        });

        this.add(dismissButton);
        this.add(nextButton);

        // Add escape key binding to exit the event
        InputMap inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exitEvent");

        ActionMap actionMap = this.getActionMap();
        actionMap.put("exitEvent", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitEvent();
            }
        });
    }

    private JLabel createChoiceLabel(int index) {
        JLabel label = new JLabel();
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        label.setForeground(Color.WHITE);
        label.setBackground(BUTTON_COLOUR);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 10, 0, 0));
        label.setMaximumSize(new Dimension(600, 50));
        label.setVisible(false);
        label.setOpaque(true);

        return label;
    }
    private void selectChoice() {
        for (JLabel label: choiceLabels) {
            label.setVisible(false);
        }
        showingOutcome = true;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        // Not used anymore - buttons have their own listeners
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("Event")) {
            // Pauses the game and stops movement
            gamePanel.pauseGame();
            gamePanel.stopMovement();

            final EventState state = (EventState) evt.getNewValue();
            eventName.setText(state.getName());
            eventDescription.setText(state.getDescription());

            // Show choice buttons based on number of outcomes
            showingOutcome = false;
            dismissButton.setVisible(false);
            nextButton.setVisible(true);

            for (int i = 0; i < choiceLabels.size(); i++) {
                if (i < state.getOutcomeCount()) {
                    choiceLabels.get(i).setText(state.getOutcomeName(i));
                    choiceLabels.get(i).setVisible(true);
                } else {
                    choiceLabels.get(i).setVisible(false);
                }
            }
        }
        else if (evt.getPropertyName().equals("Outcome")) {
            // Outcome has been applied - show the result
            final EventState state = (EventState) evt.getNewValue();
            eventDescription.setText(state.getDescription());

            // Show dismiss button
            dismissButton.setVisible(true);
            nextButton.setVisible(false);
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setActivateRandomOutcomeController(EventOutcomeController eventOutcomeController) {
        this.eventOutcomeController = eventOutcomeController;
    }

    private void returnToGame() {
        viewManagerModel.setState("game");
        viewManagerModel.firePropertyChange();
        gamePanel.resumeGame();
    }

    private void exitEvent() {
        clearAll();
        returnToGame();
    }

    private void clearAll() {
        for (JLabel label : choiceLabels) {
            label.setText("");
            label.setVisible(false);
            label.setBackground(BUTTON_COLOUR);
        }
        eventName.setText(null);
        eventDescription.setText(null);
        dismissButton.setVisible(false);
        showingOutcome = false;
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }
}

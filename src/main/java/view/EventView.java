package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
    private final JButton nextButton;
    private final JButton dismissButton;

    private GamePanel gamePanel;
    private boolean showingOutcome = false;  // Track if we're showing outcome vs initial event

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

        // Next button - shown initially to proceed to outcome
        nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.BOLD, 20));
        nextButton.setForeground(Color.WHITE);
        nextButton.setBackground(BUTTON_COLOUR);
        nextButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        nextButton.setMaximumSize(new Dimension(200, 50));
        nextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nextButton.setVisible(false);

        nextButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (nextButton.isVisible()) {
                    nextButton.setBackground(BUTTON_HOVER_COLOUR);
                }
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                nextButton.setBackground(BUTTON_COLOUR);
            }
        });

        nextButton.addActionListener(e -> {
            if (!showingOutcome) {
                triggerRandomOutcome();
            }
        });

        this.add(nextButton);
        this.add(Box.createRigidArea(new Dimension(0, 30)));

        // Dismiss button (hidden initially, shown after outcome)
        dismissButton = new JButton("Continue");
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

        this.add(dismissButton);

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

    private void triggerRandomOutcome() {
        // Hide the next button
        nextButton.setVisible(false);

        // Execute random outcome selection (no user choice)
        final EventState state = eventViewModel.getState();
        eventOutcomeController.execute(state.getOutcomes());

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

            // Show the next button to proceed
            showingOutcome = false;
            dismissButton.setVisible(false);
            nextButton.setVisible(true);
        }
        else if (evt.getPropertyName().equals("Outcome")) {
            // Outcome has been applied - show the result
            final EventState state = (EventState) evt.getNewValue();
            eventDescription.setText(state.getDescription());

            // Hide next button, show dismiss button
            nextButton.setVisible(false);
            dismissButton.setVisible(true);
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
        eventName.setText(null);
        eventDescription.setText(null);
        nextButton.setVisible(false);
        nextButton.setBackground(BUTTON_COLOUR);
        dismissButton.setVisible(false);
        showingOutcome = false;
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }
}

package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private final JButton toOutcome;

    private static final Color BACKGROUND_COLOUR = new Color(20, 20, 30);
    private static final Color TITLE_COLOUR = new Color(255, 215, 0);  // Gold
    private static final Color BUTTON_COLOUR = new Color(60, 60, 80);
    private static final Color BUTTON_HOVER_COLOUR = new Color(80, 80, 120);

    private EventOutcomeController eventOutcomeController;

    private boolean eventNextState = true;

    private final JLabel eventName;
    private final JLabel eventDescription;

    private final ArrayList<JLabel> outcomeList = new ArrayList<>();
    private GamePanel gamePanel;

    public EventView(EventViewModel eventviewModel, ViewManagerModel viewManagerModel) {
        this.eventViewModel = eventviewModel;
        this.eventViewModel.addPropertyChangeListener(this);
        this.viewManagerModel = viewManagerModel;

        this.setBackground(BACKGROUND_COLOUR);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(new EmptyBorder(50, 100, 50, 100));

        final JLabel title = new JLabel("Event");
        title.setFont(new Font("Arial",  Font.PLAIN, 30));
        title.setForeground(TITLE_COLOUR);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.add(title);
        this.add(Box.createRigidArea(new Dimension(0, 40)));

        eventName = new JLabel();
        eventName.setFont(new Font("Arial",  Font.BOLD, 60));
        eventName.setAlignmentX(Component.LEFT_ALIGNMENT);
        eventName.setForeground(Color.WHITE);

        this.add(eventName);
        this.add(Box.createRigidArea(new Dimension(0, 20)));


        eventDescription = new JLabel();
        eventDescription.setFont(new Font("Arial",  Font.PLAIN, 40));
        eventDescription.setForeground(Color.WHITE);

        this.add(eventDescription);
        this.add(Box.createRigidArea(new Dimension(0, 40)));

        final JPanel outcomes = new JPanel();
        JLabel outcomeOne = new JLabel();
        outcomeOne.setFont(new Font("Arial", Font.PLAIN, 30));
        outcomeOne.setForeground(Color.WHITE);
        JLabel outcomeTwo = new JLabel();
        outcomeTwo.setFont(new Font("Arial", Font.PLAIN, 30));
        outcomeTwo.setForeground(Color.WHITE);
        JLabel outcomeThree = new JLabel();
        outcomeThree.setFont(new Font("Arial", Font.PLAIN, 30));
        outcomeThree.setForeground(Color.WHITE);
        outcomes.setBackground(BACKGROUND_COLOUR);

        outcomes.add(outcomeOne);
        outcomes.add(Box.createRigidArea(new Dimension(0, 20)));
        outcomes.add(outcomeTwo);
        outcomes.add(Box.createRigidArea(new Dimension(0, 20)));
        outcomes.add(outcomeThree);

        outcomeList.add(outcomeOne);
        outcomeList.add(outcomeTwo);
        outcomeList.add(outcomeThree);

        this.add(outcomes);
        this.add(Box.createRigidArea(new Dimension(0, 80)));
        outcomes.setLayout(new BoxLayout(outcomes, BoxLayout.Y_AXIS));

        final JPanel eventNext = new JPanel();
        toOutcome = new JButton("Next");
        toOutcome.setFont(new Font("Arial", Font.BOLD, 20));
        toOutcome.setForeground(Color.BLUE);
        toOutcome.setBackground(BUTTON_COLOUR);

        toOutcome.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                toOutcome.setBackground(BUTTON_HOVER_COLOUR);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt){
                toOutcome.setBackground(BUTTON_COLOUR);
            }
        }
        );

        eventNext.add(toOutcome);
        toOutcome.addActionListener(this);
        eventNext.setBackground(BACKGROUND_COLOUR);
        this.add(eventNext);

    }
    public void actionPerformed(ActionEvent evt) {
            if (evt.getSource().equals(toOutcome)) {
                if (eventNextState) {
                    final EventState state = eventViewModel.getState();
                    eventOutcomeController.execute(state.getOutcomes());
                    eventNextState = false;
                }
                else {
                    eventNextState = true;
                    clearAll();
                    returnToGame();
                }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("Event")) {
            // Pauses the game and stops movement to prevent the player from moving while the event is open
            gamePanel.pauseGame();
            gamePanel.stopMovement();
            final EventState state = (EventState) evt.getNewValue();
            eventName.setText(state.getName());
            eventDescription.setText(state.getDescription());
            for (int i = 0; i < state.getOutcomeCount(); i++){
                outcomeList.get(i).setText(state.getOutcomeName(i));
            }

        }
        else if (evt.getPropertyName().equals("Outcome")) {
            final EventState state = (EventState) evt.getNewValue();
            eventDescription.setText(state.getDescription());
            outcomeList.get(state.getIndex()).setBorder(BorderFactory.createEtchedBorder());
        }

    }
    public String getViewName(){return viewName;}

    public void setActivateRandomOutcomeController(EventOutcomeController eventOutcomeController){
        this.eventOutcomeController = eventOutcomeController;
    }

    private void returnToGame() {
        viewManagerModel.setState("game");
        viewManagerModel.firePropertyChange();
        gamePanel.resumeGame();
    }
    private void clearAll() {
        for (JLabel jLabel : outcomeList) {
            jLabel.setBorder(null);
            jLabel.setText(null);
        }
        eventName.setText(null);
        eventDescription.setText(null);
    }
    public void setGamePanel(GamePanel gamePanel){
        this.gamePanel = gamePanel;
    }
 }

// Code for testing
//    public static void main(String[] args) {
//        JFrame frame = new JFrame();
//        EventViewModel eventViewModel = new EventViewModel("Event");
//        ViewManagerModel viewManagerModel = new ViewManagerModel();
//        EventView eventView = new EventView(eventViewModel, viewManagerModel);
//        Player player = new Player("Test");
//        EventDataAccessObject eventDataAccessObject = new EventDataAccessObject();
//        eventDataAccessObject.setPlayer(player);
//
//        ActivateOutcomePresenter activateOutcomePresenter = new ActivateOutcomePresenter(eventViewModel);
//        ActivateRandomOutcomeInteractor activateRandomOutcomeInteractor = new ActivateRandomOutcomeInteractor(eventDataAccessObject, activateOutcomePresenter);
//        EventOutcomeController eventOutcomeController = new EventOutcomeController(activateRandomOutcomeInteractor);
//        eventView.setActivateRandomOutcomeController(eventOutcomeController);
//
//        StartEventController startEventController = getStartEventController(eventViewModel, viewManagerModel, eventDataAccessObject);
//        startEventController.execute();
//        frame.add(eventView);
//        frame.setVisible(true);
//    }
//
//    private static StartEventController getStartEventController(EventViewModel eventViewModel, ViewManagerModel viewManagerModel, EventDataAccessObject eventDataAccessObject) {
//        eventDataAccessObject.createEventList();
//        StartEventPresenter startEventPresenter = new StartEventPresenter(eventViewModel, viewManagerModel);
//        StartRandomEventInputBoundary startRandomEventInputBoundary = new StartRandomEventInteractor(
//                eventDataAccessObject, startEventPresenter, 200);
//        return new StartEventController(startRandomEventInputBoundary);
//    }


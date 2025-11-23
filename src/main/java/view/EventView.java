package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import data_access.EventDataAccessObject;
import entity.Player;
import interface_adapter.events.ViewManagerModel;
import interface_adapter.events.*;
import use_case.events.ActivateRandomOutcome.ActivateRandomOutcomeInteractor;
import use_case.events.StartRandomEvent.StartRandomEventInputBoundary;
import use_case.events.StartRandomEvent.StartRandomEventInteractor;

public class EventView extends JPanel implements ActionListener, PropertyChangeListener {
    private final String viewName = "Event";
    private final EventViewModel eventViewModel;
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

    public EventView(EventViewModel eventviewModel) {
        this.eventViewModel = eventviewModel;
        this.eventViewModel.addPropertyChangeListener(this);

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
        toOutcome.setForeground(Color.WHITE);
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
                    /// code for exiting the view: to be implemented
                }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("Event")) {
            final EventState state = (EventState) evt.getNewValue();
            eventName.setText(state.getName());
            eventDescription.setText(state.getDescription());
            for (int i = 0; i < state.getOutcomeCount(); i++){
                outcomeList.get(i).setText(state.getOutcomeName(i));
            }

        }
        if (evt.getPropertyName().equals("Outcome")) {
            final EventState state = (EventState) evt.getNewValue();
            eventDescription.setText(state.getDescription());
            outcomeList.get(state.getIndex()).setBorder(BorderFactory.createEtchedBorder());
        }

    }
    public String getViewName(){return viewName;}

    public void setActivateRandomOutcomeController(EventOutcomeController eventOutcomeController){
        this.eventOutcomeController = eventOutcomeController;
    }
// Code for testing
//    public static void main(String[] args) {
//        JFrame frame = new JFrame();
//        EventViewModel eventViewModel = new EventViewModel("Event");
//        EventView eventView = new EventView(eventViewModel);
//        ViewManagerModel viewManagerModel = new ViewManagerModel();
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
}

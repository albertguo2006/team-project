package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import interface_adapter.events.StartEventController;

public class EventView extends JPanel implements ActionListener{

    private final JButton toOutcome;
    private static final Color BACKGROUND_COLOUR = Color.DARK_GRAY;
    private StartEventController startEventController;

    public EventView() {

        this.setBackground(BACKGROUND_COLOUR);

        final JPanel eventNext = new JPanel();
        toOutcome = new JButton("Next");
        eventNext.add(toOutcome);

        toOutcome.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        if (evt.getSource().equals(toOutcome)) {


                        }
                    }
                }
        );
        this.add(eventNext);
    }
    public void actionPerformed(ActionEvent e) {
    }
}

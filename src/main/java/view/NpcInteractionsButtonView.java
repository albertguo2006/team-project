// A View class should not construct DAOs, Interactors, Presenters, etc
// Should have a GameApplication builder
package view;

import view.components.CircularButton;
import data_access.NPCDataAccessObject;
import interface_adapter.events.npc_interactions.*;
import use_case.npc_interactions.*;

import javax.swing.*;

public class NpcInteractionsButtonView {

    public NpcInteractionsButtonView(JFrame frame, int x_cord, int y_cord) {

        // Data access
        NpcInteractionsUserDataAccessInterface npcDataAccess =
                new NPCDataAccessObject();

        // View model
        NpcInteractionsViewModel viewModel = new NpcInteractionsViewModel();

        // Presenter
        NpcInteractionsOutputBoundary presenter =
                new NpcInteractionsPresenter(viewModel);

        // Interactor
        NpcInteractionsInputBoundary interactor =
                new NpcInteractionsInteractor(npcDataAccess, presenter);

        // Controller
        NpcInteractionsController controller =
                new NpcInteractionsController(interactor);

        // Button
        CircularButton button = new CircularButton("Talk to NPC");
        button.setBounds(x_cord, y_cord, 200, 200);

        button.addActionListener(e ->
                NpcInteractionsView.show(controller, viewModel, npcDataAccess)
        );

        frame.add(button);
        frame.setVisible(true);
    }
}

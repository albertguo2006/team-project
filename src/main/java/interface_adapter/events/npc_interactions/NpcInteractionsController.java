package interface_adapter.events.npc_interactions;

import use_case.npc_interactions.NpcInteractionsInputBoundary;
import use_case.npc_interactions.NpcInteractionsInputData;

public class NpcInteractionsController {

    private final NpcInteractionsInputBoundary interactor;

    public NpcInteractionsController(NpcInteractionsInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void handleUserMessage(String npcName, String message) {
        NpcInteractionsInputData inputData =
                new NpcInteractionsInputData(npcName, message);

        interactor.execute(inputData);
    }
}
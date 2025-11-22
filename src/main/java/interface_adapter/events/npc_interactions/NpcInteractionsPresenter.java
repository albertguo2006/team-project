package interface_adapter.events.npc_interactions;

import use_case.npc_interactions.NpcInteractionsOutputBoundary;
import use_case.npc_interactions.NpcInteractionsOutputData;

public class NpcInteractionsPresenter implements NpcInteractionsOutputBoundary {

    private final NpcInteractionsViewModel viewModel;

    public NpcInteractionsPresenter(NpcInteractionsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(NpcInteractionsOutputData outputData) {
        viewModel.setNpcName(outputData.getNpcName());
        viewModel.setAiResponse(outputData.getAiResponse());

        viewModel.fireUpdate();
    }
}

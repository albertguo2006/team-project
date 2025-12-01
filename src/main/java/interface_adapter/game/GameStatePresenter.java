package interface_adapter.game;

import entity.Player;
import entity.Zone;
import use_case.movement.PlayerMovementInputBoundary;
import use_case.proximity.ProximityDetectionInputBoundary;
import use_case.zone_transition.ZoneTransitionOutputBoundary;
import use_case.zone_transition.ZoneTransitionOutputData;

/**
 * GameStatePresenter updates the GameStateViewModel based on use case outputs.
 *
 * This presenter follows Clean Architecture by implementing the output boundary
 * and translating use case data into view model updates.
 */
public class GameStatePresenter implements ZoneTransitionOutputBoundary {

    private final GameStateViewModel viewModel;

    /**
     * Constructs a GameStatePresenter.
     *
     * @param viewModel the view model to update
     */
    public GameStatePresenter(GameStateViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentZoneTransition(ZoneTransitionOutputData outputData) {
        viewModel.setCurrentZoneName(outputData.getNewZoneName());
        viewModel.setZoneBackgroundColor(outputData.getBackgroundColor());
        viewModel.setZoneBackgroundMusicPath(outputData.getBackgroundMusicPath());
    }

    /**
     * Updates the view model with current player state.
     *
     * @param movementUseCase the movement use case to read player state from
     */
    public void updatePlayerState(PlayerMovementInputBoundary movementUseCase) {
        Player player = movementUseCase.getPlayer();
        viewModel.setPlayerX(player.getX());
        viewModel.setPlayerY(player.getY());
        viewModel.setPlayerBalance(player.getBalance());
        viewModel.setPlayerHealth(player.getHealth());
        viewModel.setCurrentDayProgress(player.getCurrentDay().getProgressString());
        viewModel.setMoving(movementUseCase.isMoving());
        viewModel.setCurrentDirection(movementUseCase.getCurrentDirection());
        viewModel.setInventory(player.getInventory());
    }

    /**
     * Updates the view model with current proximity state.
     *
     * @param proximityUseCase the proximity use case to read state from
     */
    public void updateProximityState(ProximityDetectionInputBoundary proximityUseCase) {
        viewModel.setNearbyNPC(proximityUseCase.getNearbyNPC());
        viewModel.setNearbyWorldItem(proximityUseCase.getNearbyWorldItem());
        viewModel.setInSleepZone(proximityUseCase.isInSleepZone());
        viewModel.setInStockTradingZone(proximityUseCase.isInStockTradingZone());
        viewModel.setInMailboxZone(proximityUseCase.isInMailboxZone());
    }

    /**
     * Updates the view model with initial zone state.
     *
     * @param zone the current zone
     */
    public void updateZoneState(Zone zone) {
        if (zone != null) {
            viewModel.setCurrentZoneName(zone.getName());
            viewModel.setZoneBackgroundColor(zone.getBackgroundColor());
            viewModel.setZoneBackgroundImagePath(zone.getBackgroundImagePath());
            viewModel.setZoneBackgroundMusicPath(zone.getBackgroundMusicPath());
        }
    }
}

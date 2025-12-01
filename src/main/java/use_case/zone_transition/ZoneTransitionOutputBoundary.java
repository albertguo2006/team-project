package use_case.zone_transition;

/**
 * ZoneTransitionOutputBoundary defines the output port for zone transition notifications.
 *
 * Presenters implement this interface to receive notifications about zone transitions
 * and update the view accordingly.
 */
public interface ZoneTransitionOutputBoundary {

    /**
     * Called when a zone transition occurs.
     *
     * @param outputData the data about the zone transition
     */
    void presentZoneTransition(ZoneTransitionOutputData outputData);
}

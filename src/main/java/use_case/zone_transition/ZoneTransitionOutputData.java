package use_case.zone_transition;

import java.awt.Color;

/**
 * ZoneTransitionOutputData contains the data about a completed zone transition.
 *
 * This is a simple data transfer object (DTO) that carries information
 * from the use case to the presenter.
 */
public class ZoneTransitionOutputData {

    private final String newZoneName;
    private final Color backgroundColor;
    private final String backgroundMusicPath;

    /**
     * Constructs a ZoneTransitionOutputData with the given zone information.
     *
     * @param newZoneName the name of the new zone
     * @param backgroundColor the background color of the new zone
     * @param backgroundMusicPath the background music path for the new zone
     */
    public ZoneTransitionOutputData(String newZoneName, Color backgroundColor, String backgroundMusicPath) {
        this.newZoneName = newZoneName;
        this.backgroundColor = backgroundColor;
        this.backgroundMusicPath = backgroundMusicPath;
    }

    public String getNewZoneName() {
        return newZoneName;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public String getBackgroundMusicPath() {
        return backgroundMusicPath;
    }
}

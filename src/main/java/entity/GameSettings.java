package entity;

/**
 * GameSettings holds configuration data for the game.
 * This includes audio volume and display resolution preferences.
 * 
 * This class follows the Entity pattern in Clean Architecture,
 * representing game configuration as a domain concept.
 */
public class GameSettings {
    
    // Audio settings (0.0 to 1.0)
    private float masterVolume;
    
    // Display settings
    private int renderWidth;
    private int renderHeight;
    
    // Available resolution presets
    public static final int[][] RESOLUTION_PRESETS = {
        {1280, 800},   // 0: Small (16:10)
        {1600, 1000},  // 1: Medium (16:10)
        {1920, 1200},  // 2: Large (16:10) - Default
        {2560, 1600}   // 3: Extra Large (16:10)
    };
    
    /**
     * Creates default game settings.
     * Default volume: 0.5 (50%)
     * Default resolution: 1920x1200
     */
    public GameSettings() {
        this.masterVolume = 0.5f;
        this.renderWidth = 1920;
        this.renderHeight = 1200;
    }
    
    /**
     * Creates game settings with specified values.
     * 
     * @param masterVolume the master volume (0.0 to 1.0)
     * @param renderWidth the render width in pixels
     * @param renderHeight the render height in pixels
     */
    public GameSettings(float masterVolume, int renderWidth, int renderHeight) {
        this.masterVolume = clampVolume(masterVolume);
        this.renderWidth = renderWidth;
        this.renderHeight = renderHeight;
    }
    
    /**
     * Gets the master volume.
     * @return volume level from 0.0 (muted) to 1.0 (full)
     */
    public float getMasterVolume() {
        return masterVolume;
    }
    
    /**
     * Sets the master volume.
     * @param volume volume level from 0.0 (muted) to 1.0 (full)
     */
    public void setMasterVolume(float volume) {
        this.masterVolume = clampVolume(volume);
    }
    
    /**
     * Gets the render width.
     * @return width in pixels
     */
    public int getRenderWidth() {
        return renderWidth;
    }
    
    /**
     * Gets the render height.
     * @return height in pixels
     */
    public int getRenderHeight() {
        return renderHeight;
    }
    
    /**
     * Sets the render resolution.
     * @param width width in pixels
     * @param height height in pixels
     */
    public void setResolution(int width, int height) {
        this.renderWidth = width;
        this.renderHeight = height;
    }
    
    /**
     * Sets the resolution using a preset index.
     * @param presetIndex index into RESOLUTION_PRESETS array
     */
    public void setResolutionPreset(int presetIndex) {
        if (presetIndex >= 0 && presetIndex < RESOLUTION_PRESETS.length) {
            this.renderWidth = RESOLUTION_PRESETS[presetIndex][0];
            this.renderHeight = RESOLUTION_PRESETS[presetIndex][1];
        }
    }
    
    /**
     * Gets the current resolution preset index, or -1 if custom.
     * @return preset index or -1 if not a preset
     */
    public int getResolutionPresetIndex() {
        for (int i = 0; i < RESOLUTION_PRESETS.length; i++) {
            if (RESOLUTION_PRESETS[i][0] == renderWidth && 
                RESOLUTION_PRESETS[i][1] == renderHeight) {
                return i;
            }
        }
        return -1;  // Custom resolution
    }
    
    /**
     * Gets a formatted string for the current resolution.
     * @return resolution string like "1920x1200"
     */
    public String getResolutionString() {
        return renderWidth + "x" + renderHeight;
    }
    
    /**
     * Clamps volume to valid range.
     */
    private float clampVolume(float volume) {
        return Math.max(0.0f, Math.min(1.0f, volume));
    }
    
    /**
     * Creates a copy of these settings.
     * @return a new GameSettings instance with the same values
     */
    public GameSettings copy() {
        return new GameSettings(masterVolume, renderWidth, renderHeight);
    }
    
    @Override
    public String toString() {
        return String.format("GameSettings[volume=%.2f, resolution=%dx%d]", 
                           masterVolume, renderWidth, renderHeight);
    }
}
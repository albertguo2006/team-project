package entity;

/**
 * Represents a day of the work week in the game.
 * The game runs from Monday through Friday.
 */
public enum Day {
    MONDAY("Monday", 1),
    TUESDAY("Tuesday", 2),
    WEDNESDAY("Wednesday", 3),
    THURSDAY("Thursday", 4),
    FRIDAY("Friday", 5);
    
    private final String displayName;
    private final int dayNumber;
    
    /**
     * Constructs a Day with display name and number.
     * 
     * @param displayName the human-readable name
     * @param dayNumber the day number (1-5)
     */
    Day(String displayName, int dayNumber) {
        this.displayName = displayName;
        this.dayNumber = dayNumber;
    }
    
    /**
     * Gets the display name of this day.
     * @return the display name (e.g., "Monday")
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the day number (1-5).
     * @return the day number
     */
    public int getDayNumber() {
        return dayNumber;
    }
    
    /**
     * Gets the next day in the week.
     * Returns null if this is Friday (end of week).
     * 
     * @return the next Day, or null if this is the last day
     */
    public Day getNext() {
        switch (this) {
            case MONDAY:
                return TUESDAY;
            case TUESDAY:
                return WEDNESDAY;
            case WEDNESDAY:
                return THURSDAY;
            case THURSDAY:
                return FRIDAY;
            case FRIDAY:
                return null;  // End of week
            default:
                return null;
        }
    }
    
    /**
     * Checks if this is the last day of the week.
     * @return true if this is Friday, false otherwise
     */
    public boolean isLastDay() {
        return this == FRIDAY;
    }
    
    /**
     * Gets a formatted string showing day and progress.
     * @return formatted string like "Monday - Day 1/5"
     */
    public String getProgressString() {
        return displayName + " - Day " + dayNumber + "/5";
    }
}
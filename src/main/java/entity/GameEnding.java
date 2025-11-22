package entity;

/**
 * Represents a game ending based on the player's performance.
 * Different endings are shown based on final balance after Friday.
 */
public class GameEnding {
    
    /**
     * Types of endings based on player's financial success.
     */
    public enum EndingType {
        WEALTHY("The Wealthy Executive", "You've mastered the art of financial success! " +
                "Your portfolio is impressive and your future looks bright."),
        COMFORTABLE("The Comfortable Professional", "You made it through the week with money to spare. " +
                "You're on the right track to financial stability."),
        STRUGGLING("The Struggling Worker", "You survived the week, but barely. " +
                "Every dollar counts when you're living paycheck to paycheck."),
        BROKE("The Defeated", "The city has defeated you... for now. " +
                "With negative or minimal balance, the challenges ahead look daunting.");
        
        private final String title;
        private final String message;
        
        EndingType(String title, String message) {
            this.title = title;
            this.message = message;
        }
        
        public String getTitle() {
            return title;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    private final EndingType type;
    private final double finalBalance;
    
    /**
     * Constructs a GameEnding.
     * 
     * @param type the type of ending achieved
     * @param finalBalance the player's final balance
     */
    public GameEnding(EndingType type, double finalBalance) {
        this.type = type;
        this.finalBalance = finalBalance;
    }
    
    /**
     * Gets the ending type.
     * @return the EndingType
     */
    public EndingType getType() {
        return type;
    }
    
    /**
     * Gets the final balance.
     * @return the final balance
     */
    public double getFinalBalance() {
        return finalBalance;
    }
    
    /**
     * Gets the title for this ending.
     * @return the ending title
     */
    public String getTitle() {
        return type.getTitle();
    }
    
    /**
     * Gets the message for this ending.
     * @return the ending message
     */
    public String getMessage() {
        return type.getMessage();
    }
    
    /**
     * Determines which ending the player gets based on their final balance.
     * 
     * Thresholds:
     * - WEALTHY: >= $5000
     * - COMFORTABLE: >= $2000
     * - STRUGGLING: >= $1000
     * - BROKE: < $1000
     * 
     * @param balance the player's final balance
     * @return a GameEnding object for the appropriate tier
     */
    public static GameEnding determineEnding(double balance) {
        EndingType type;
        
        if (balance >= 5000.0) {
            type = EndingType.WEALTHY;
        } else if (balance >= 2000.0) {
            type = EndingType.COMFORTABLE;
        } else if (balance >= 1000.0) {
            type = EndingType.STRUGGLING;
        } else {
            type = EndingType.BROKE;
        }
        
        return new GameEnding(type, balance);
    }
    
    @Override
    public String toString() {
        return String.format("GameEnding[type=%s, balance=$%.2f]", type, finalBalance);
    }
}
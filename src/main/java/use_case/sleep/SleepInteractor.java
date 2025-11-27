package use_case.sleep;

import java.util.List;

import data_access.Paybill.PaybillDataAccessObject;
import entity.Bill;
import entity.Day;
import entity.DaySummary;
import entity.GameEnding;
import entity.Player;

/**
 * Interactor for the sleep use case.
 * Handles the business logic for player sleeping and day advancement.
 */
public class SleepInteractor implements SleepInputBoundary {
    private final SleepOutputBoundary presenter;
    private final SleepDataAccessInterface dataAccess;
    private final PaybillDataAccessObject billDataAccess;

    /**
     * Constructs a SleepInteractor.
     *
     * @param presenter the output boundary for presenting results
     * @param dataAccess the data access interface for persistence
     */
    public SleepInteractor(SleepOutputBoundary presenter, SleepDataAccessInterface dataAccess) {
        this.presenter = presenter;
        this.dataAccess = dataAccess;
        this.billDataAccess = new PaybillDataAccessObject();
    }

    /**
     * Constructs a SleepInteractor with a custom bill data access object.
     * This constructor is primarily for testing purposes.
     *
     * @param presenter the output boundary for presenting results
     * @param dataAccess the data access interface for persistence
     * @param billDataAccess the bill data access object for bill management
     */
    public SleepInteractor(SleepOutputBoundary presenter, SleepDataAccessInterface dataAccess,
                          PaybillDataAccessObject billDataAccess) {
        this.presenter = presenter;
        this.dataAccess = dataAccess;
        this.billDataAccess = billDataAccess;
    }
    
    /**
     * Executes the sleep use case.
     * Validates the player can sleep, restores health, creates day summary,
     * and either advances to next day or shows game ending.
     *
     * @param inputData the input data containing the player
     */
    @Override
    public void execute(SleepInputData inputData) {
        Player player = inputData.getPlayer();

        if (player == null){
            presenter.presentSleepError("Player not found.");
            return;
        }
        
        // Validate: check if player has already slept today
        if (player.hasSleptToday()) {
            presenter.presentSleepError("You've already slept today. Come back tomorrow!");
            return;
        }
        
        // Get current day before advancing
        Day completedDay = player.getCurrentDay();

        // Calculate new balance
        double currentBalance = player.getBalance();
        double dailyEarnings = player.getDailyEarnings();
        double dailySpendings = player.getDailySpending();
        double newBalance = currentBalance + dailyEarnings - dailySpendings;
        
        // Restore health to 100
        player.setHealth(100);
        
        // Mark that player has slept today
        player.setHasSleptToday(true);
        
        // Create day summary
        DaySummary summary = dataAccess.createDaySummary(
            completedDay,
            player.getDailyEarnings(),
            player.getDailySpending(),
            newBalance
        );

        // Set player's balance
        player.setBalance(newBalance);
        
        // Check if this was Friday (end of week)
        if (completedDay.isLastDay()) {
            // Calculate total unpaid debt with interest
            List<Bill> unpaidBills = billDataAccess.getUnpaidBills();
            double totalDebt = 0.0;
            for (Bill bill : unpaidBills) {
                totalDebt += bill.getAmount();
            }
            
            // Deduct unpaid debt from balance
            double finalBalance = newBalance - totalDebt;
            player.setBalance(finalBalance);
            
            System.out.println("=== END OF WEEK ===");
            System.out.println("Balance before debt: $" + String.format("%.2f", newBalance));
            System.out.println("Total unpaid debt: $" + String.format("%.2f", totalDebt));
            System.out.println("Final balance: $" + String.format("%.2f", finalBalance));
            
            // Week complete - determine ending
            GameEnding ending = GameEnding.determineEnding(finalBalance);
            
            // Save player state
            dataAccess.savePlayerState(player);
            
            // Present game ending
            presenter.presentGameEnding(ending);
        } else {
            // Not Friday - advance to next day
            boolean advanced = player.advanceDay();
            
            if (advanced) {
                // Apply interest to all unpaid bills for the new day
                List<Bill> allBills = billDataAccess.getAllBills();
                for (Bill bill : allBills) {
                    if (!bill.getPaid()) {
                        bill.applyDailyInterest();
                        billDataAccess.saveBill(bill);
                    }
                }
                
                // Generate new bills for the new week day
                billDataAccess.advanceToNextWeek();
                System.out.println("=== NEW DAY: " + player.getCurrentDay().getDisplayName() + " ===");
                System.out.println("New bills generated and delivered to mailbox");
                System.out.println("Interest applied to all unpaid bills");
                
                // Reset daily financials for new day
                player.resetDailyFinancials();
                
                // Save player state
                dataAccess.savePlayerState(player);
                
                // Create output data with new day
                SleepOutputData outputData = new SleepOutputData(
                    summary,
                    player.getCurrentDay(),
                    false
                );
                
                // Present day summary
                presenter.presentDaySummary(outputData);
            } else {
                // This shouldn't happen if isLastDay() check worked correctly
                presenter.presentSleepError("Unable to advance to next day.");
            }
        }
    }
}

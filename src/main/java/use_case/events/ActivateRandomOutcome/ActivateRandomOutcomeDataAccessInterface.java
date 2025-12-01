package use_case.events.ActivateRandomOutcome;

public interface ActivateRandomOutcomeDataAccessInterface {
    double getBalance();
    void setBalance(double balance);
    void addDailyEarnings(double earnings);
    void addDailySpending(double spendings);
}

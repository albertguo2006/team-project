package use_case.events.ActivateRandomOutcome;

public interface ActivateRandomOutcomeDataAccessInterface {
    double getBalance();
    void setBalance(double balance);
    void addDailySpending(double spending);
    void addDailyEarnings(double earnings);
}

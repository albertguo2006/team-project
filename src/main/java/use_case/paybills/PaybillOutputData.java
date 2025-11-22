package use_case.paybills;

import entity.Bill;

import java.util.List;

public class PaybillOutputData {
    private final double amount;
    private final String message;
    private final boolean success;

    public PaybillOutputData(boolean success, String message, double amount) {
        this.success = success;
        this.message = message;
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isSuccess() {
        return success;
    }
}

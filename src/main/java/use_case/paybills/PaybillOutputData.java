package use_case.paybills;

import entity.Bill;

import java.util.List;

public class PaybillOutputData {
    private final double amount;
    private final String message;
    private final boolean sucess;

    public PaybillOutputData(boolean sucess, String message) {
        this.sucess = sucess;
        this.message = message;
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isSucess() {
        return sucess;
    }
}

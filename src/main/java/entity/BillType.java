package entity;

public enum BillType {
    RENT(false, 50.0, 0.05),
    CREDIT_CARD(false, 21.0, 0.0),
    INSURANCE(false, 24.0, 0.03),
    TAX(false, 30.0, 0.01),
    SUBSCRIPTION(false, 10.0, 0.0),
    MORTGAGE(false, 75.0, 0.04),
    ELECTRICITY(false, 12.0, 0.0),
    INTERNET(true, 13.0, 0.0),
    CELLPHONE(true, 17.0, 0.0),
    WATER(false, 18.0, 0.0),
    GAS(false, 19.0, 0.0),
    LOAN(false, 20.0, 0.006);

    private final boolean recurring;
    private final double baseLateFee;
    private final double dailyLatePercentage;

    BillType(boolean recurring, double baseLateFee, double dailyLatePercentage) {
        this.recurring = recurring;
        this.baseLateFee = baseLateFee;
        this.dailyLatePercentage = dailyLatePercentage;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public double getBaseLateFee() {
        return baseLateFee;
    }

    public double getDailyLatePercentage() {
        return dailyLatePercentage;
    }
}

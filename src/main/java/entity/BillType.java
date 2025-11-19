package entity;

public enum BillType {
    RENT(false),
    CREDIT_CARD(false),
    INSURANCE(false),
    TAX(false),
    SUBSCRIPTION(false),
    MORTGAGE(false),
    ELECTRICITY(false),
    INTERNET(true),
    CELLPHONE(true),
    WATER(false),
    GAS(false),
    LOAN(false);

    private final boolean recurring;

    BillType(boolean recurring) {
        this.recurring = recurring;
    }

    public boolean isRecurring() {
        return recurring;
    }
}

package entity;

import java.util.Date;

public class Bill {
    private final String id; // each bill has a unique id
    private final double originalAmount; // Original bill amount
    private double currentAmount; // Current amount with interest
    private final String name;
    private final Date dueDate;
    private final Date createdDate; // When the bill was created
    private Boolean isPaid;
    private final BillType type;
    private int daysOverdue; // Number of days overdue for interest calculation

    public enum BillType {
        RENT, CREDIT_CARD, INSURANCE, TAX, SUBSCRIPTION, MORTGAGE, ELECTRICITY, INTERNET, CELLPHONE, WATER,
        GAS, LOAN
    }

    // Interest rate per day for overdue bills (e.g., 0.01 = 1% per day)
    private static final double DAILY_INTEREST_RATE = 0.02; // 2% per day

    public Bill(String id, double amount, String name, Date dueDate, Boolean isPaid, BillType type) {
        this.id = id;
        this.originalAmount = amount;
        this.currentAmount = amount;
        this.name = name;
        this.dueDate = dueDate;
        this.createdDate = new Date(); // Current date/time
        this.isPaid = isPaid;
        this.type = type;
        this.daysOverdue = 0;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public Date getDueDate() {
        return dueDate;
    }
    
    public Date getCreatedDate() {
        return createdDate;
    }
    
    public Boolean getPaid() {
        return isPaid;
    }
    
    public double getAmount() {
        return currentAmount;
    }
    
    public double getOriginalAmount() {
        return originalAmount;
    }
    
    public BillType getBillType() {
        return type;
    }
    
    public void setIsPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }
    
    public int getDaysOverdue() {
        return daysOverdue;
    }
    
    /**
     * Applies interest for one day being overdue.
     * Only applies if the bill is unpaid.
     */
    public void applyDailyInterest() {
        if (!isPaid) {
            daysOverdue++;
            currentAmount = originalAmount * Math.pow(1 + DAILY_INTEREST_RATE, daysOverdue);
            System.out.println("Bill " + name + " now " + daysOverdue + " days overdue. Amount: $" +
                             String.format("%.2f", currentAmount));
        }
    }
    
    /**
     * Gets the total interest accrued on this bill.
     */
    public double getInterestAmount() {
        return currentAmount - originalAmount;
    }
}

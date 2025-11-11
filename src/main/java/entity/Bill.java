package entity;

import java.util.Date;

public class Bill {
    private String id; // each bill has a unique id
    private double amount; // Individual bill amount
    private String name;
    private Date dueDate;
    private Boolean isPaid;


    public Bill(String id, double amount, String name, Date dueDate, Boolean isPaid) {
        this.id = id;
        this.amount = amount;
        this.name = name;
        this.dueDate = dueDate;
        this.isPaid = isPaid;
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
    public Boolean getPaid() {
        return isPaid;
    }
    public double getAmount() {
        return amount;
    }
    public void setIsPaid(boolean isPaid){
        this.isPaid = isPaid;
    }

}

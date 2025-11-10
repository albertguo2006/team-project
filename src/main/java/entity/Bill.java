package entity;

import java.util.Date;

public class Bill {
    private String id;
    private double amount; // Individual bill amount
    private String name;
    private Date dueDate;
    private Boolean isPaid;
    private static double total = 0; // Total of all unpaid bills amount

    public Bill(String id, double amount, String name, Date dueDate, Boolean isPaid) {
        this.id = id;
        this.amount = amount;
        this.name = name;
        this.dueDate = dueDate;
        this.isPaid = isPaid;

        if (!isPaid)
            total += amount;
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
    public double getTotal(){
        return total;
    }
    public void setIsPaid(boolean isPaid){
        if (!this.isPaid && isPaid){
            total -= amount;
        }
        this.isPaid = isPaid;
    }
    public void addtototal(){
        total += amount;
    }
    public void removefromtotal(){
        total -= amount;
    }

}

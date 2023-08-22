package com.example.bank3;


import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "Transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Sender_ID")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "Recipient_ID")
    private User recipient;

    @Column(name = "Amount")
    private double amount;

    @Column(name = "Currency")
    private String currency;

    @Column(name = "Date")
    private java.sql.Date date;

    public Transaction( User sender, User recipient, double amount, String currency, Date date) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
    }

    public Transaction() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", sender=" + sender +
                ", recipient=" + recipient +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", date=" + date +
                '}';
    }
}
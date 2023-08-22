package com.example.bank3;


import javax.persistence.*;

@Entity
@Table(name = "Accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;


    @OneToOne
    @JoinColumn(name = "User_ID")
    private User user;
    private double UAH;

    private double USD;

    private double EUR;

    public Account(User user, double UAH, double USD, double EUR) {
        this.user = user;
        this.UAH = UAH;
        this.USD = USD;
        this.EUR = EUR;
    }
    public Account( double UAH, double USD, double EUR) {
        this.UAH = UAH;
        this.USD = USD;
        this.EUR = EUR;
    }

    public Account() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setEUR(double EUR) {
        this.EUR = EUR;
    }

    public Double getEUR() {
        return EUR;
    }

    public double getUAH() {
        return UAH;
    }

    public void setUAH(double UAH) {
        this.UAH = UAH;
    }

    public double getUSD() {
        return USD;
    }

    public void setUSD(double USD) {
        this.USD = USD;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", user=" + user +
                ", UAH=" + UAH +
                ", USD=" + USD +
                ", EUR=" + EUR +
                '}';
    }
}
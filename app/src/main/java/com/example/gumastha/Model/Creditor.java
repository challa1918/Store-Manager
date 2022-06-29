package com.example.gumastha.Model;

import java.io.Serializable;

public class Creditor implements Serializable {
    public String name,number;
    public double amount;

    public Creditor(String name, String number, double amount) {
        this.name = name;
        this.number = number;
        this.amount = amount;
    }
    public Creditor(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Creditor{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", amount=" + amount +
                '}';
    }
}

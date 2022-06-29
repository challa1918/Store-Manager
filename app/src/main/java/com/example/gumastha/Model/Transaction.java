package com.example.gumastha.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Transaction implements Serializable, Comparable<Transaction>{


    public ArrayList<BillProduct>list;
   public String transactionMode;
    public String transactionDate;
    public Integer billAmount;
    public long seconds;
    public Transaction(){

    }

    public Transaction(ArrayList<BillProduct> list, String transactionMode, String transactionDate, int billAmount, Integer TID, String generated_By) {
        this.list = list;
        this.transactionMode = transactionMode;
        this.transactionDate = transactionDate;
        this.billAmount = billAmount;
        this.TID = TID;
        Generated_By = generated_By;
    }

    public Integer TID;
    public String Generated_By;

    @Override
    public int compareTo(Transaction o) {
        return this.transactionDate.compareTo(this.transactionDate);
    }
}

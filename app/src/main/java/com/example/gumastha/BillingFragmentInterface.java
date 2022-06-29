package com.example.gumastha;

import com.example.gumastha.Model.Transaction;

import java.util.ArrayList;

public interface BillingFragmentInterface {
    public void setTransactionList(ArrayList<Transaction> list);
    public void setSales(ArrayList<Transaction> list);
}

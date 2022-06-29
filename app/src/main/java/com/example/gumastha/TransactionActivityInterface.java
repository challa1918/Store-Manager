package com.example.gumastha;

import com.example.gumastha.Model.Transaction;

import java.util.ArrayList;

public interface TransactionActivityInterface {
    public void setTransactionList(ArrayList<Transaction> list);
    public void NoTransactionsAvailableError();
}

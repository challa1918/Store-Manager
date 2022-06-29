package com.example.gumastha;

import com.example.gumastha.Model.Creditor_Transaction;

import java.util.ArrayList;

public interface CreditAccountInterface {
    public void setRecyclerview(ArrayList<Creditor_Transaction> list);
    public void creditorsAmount(String status);
    public void deleteStatus(String status);
}

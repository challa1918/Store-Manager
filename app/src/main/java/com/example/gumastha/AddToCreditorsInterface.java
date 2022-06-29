package com.example.gumastha;

import com.example.gumastha.Model.Creditor;

import java.util.ArrayList;

public interface AddToCreditorsInterface {
    public void addingStatus(String status);
    public void setRecyclerView(ArrayList<Creditor>list);
    public void finishActivity();
}

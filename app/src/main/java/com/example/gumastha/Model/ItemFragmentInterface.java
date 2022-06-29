package com.example.gumastha.Model;

import java.util.ArrayList;
import java.util.TreeMap;

public interface ItemFragmentInterface {
    public void setRecyclerView(TreeMap<Integer, ArrayList<Product>> list);
    public void noDataAvailableUnderCategory(String category);
}

package com.example.gumastha;

import com.example.gumastha.Model.BillProduct;
import com.example.gumastha.Model.Product;

import java.util.ArrayList;

public interface CartBuildingInterface {
    public void sheetBottom(Product product);
    public void setRecyclerView(ArrayList<BillProduct> list);
    public void noProductAvailableWithSuchCode(String code);
}

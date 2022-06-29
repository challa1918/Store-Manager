package com.example.gumastha.Controller;

import com.example.gumastha.CartBuildingInterface;
import com.example.gumastha.Model.Creditor;
import com.example.gumastha.Model.Product;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public interface FirebaseOperations {
    public FirebaseUser getCurrentUser();
    public ArrayList<Product>getProducts();
    public void getCartProducts(String activity);
    public  void getProductUsingCode(String code, CartBuildingInterface cartBuildingInterface);
    public void getFilteredCategories(String category);
    public void getTransactions(String category);
    public void addCreditor(Creditor contact,String activity);
    public void getCreditors(String activity);
    public void getCreditorTransactions(String creditor);
    public void  getcreditamount(String creditor);
   public  void getProfitDetails();
    public void deleteCreditorAccount(String name);
}

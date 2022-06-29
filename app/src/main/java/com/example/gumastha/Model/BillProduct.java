package com.example.gumastha.Model;

import java.io.Serializable;

public class BillProduct implements Serializable {
    public BillProduct(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    public BillProduct() {
    }

    public Product product;
   public int quantity;

    @Override
    public String toString() {
        return "BillProduct{" +
                "product=" + product +
                ", quantity='" + quantity + '\'' +
                '}';
    }
}

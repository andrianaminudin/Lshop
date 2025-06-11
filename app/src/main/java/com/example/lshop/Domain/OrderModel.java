package com.example.lshop.Domain;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderModel implements Serializable {
    public long timestamp;
    public double subtotal, tax, delivery, total;
    public ArrayList<ItemModel> items;

    public OrderModel() {}  // Diperlukan untuk Firebase

    public long getTimestamp() {
        return timestamp;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getTax() {
        return tax;
    }

    public double getDelivery() {
        return delivery;
    }

    public double getTotal() {
        return total;
    }

    public ArrayList<ItemModel> getItems() {
        return items;
    }
}

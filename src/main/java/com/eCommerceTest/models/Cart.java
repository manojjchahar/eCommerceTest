package com.ecommercefull.models;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<Product> items = new ArrayList<>();

    public Cart() {}

    public Cart(List<Product> items) {
        this.items = items;
    }

    public List<Product> getItems() { return items; }
    public void setItems(List<Product> items) { this.items = items; }

    public int getCount() { return items == null ? 0 : items.size(); }
}

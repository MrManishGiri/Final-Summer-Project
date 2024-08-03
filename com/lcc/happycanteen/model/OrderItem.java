package com.lcc.happycanteen.model;

public class OrderItem {

    private String id;
    private long orderNumber;
    private String phoneNumber;
    private String name;
    private double price;
    private String foodItemName;  // New field

    public OrderItem() {
    }

    public OrderItem(String id, long orderNumber, String phoneNumber, String name, double price, String foodItemName) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.price = price;
        this.foodItemName = foodItemName;  // Initialize the new field
    }

    // getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getFoodItemName() {
        return foodItemName;
    }

    public void setFoodItemName(String foodItemName) {
        this.foodItemName = foodItemName;
    }
}
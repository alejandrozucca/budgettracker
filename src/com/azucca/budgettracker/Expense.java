package com.azucca.budgettracker;

public class Expense {

    private int id;
    private String productName;
    private String method;
    private float price;
    private String date;

    public Expense(int id, String productName, String method, float price, String date) {
        this.id = id;
        this.productName = productName;
        this.method = method;
        this.price = price;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", method='" + method + '\'' +
                ", price=" + price +
                ", date='" + date + '\'' +
                '}';
    }

}

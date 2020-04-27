package com.example.wishlist.Class;

import android.graphics.Bitmap;

public class Product {
    private String name;
    private Bitmap photo;
    private String description;
    private String[] category;
    private int weight;
    private int price;
    private int desire;
    private String dimensions;
    private int rating; //useful?
    private int amount;
    private int purchased;

    public Product(String name, Bitmap photo, String description, String[] category, int weight, int price, int desire, String dimensions, int rating, int amount, int purchased) {
        this.name = name;
        this.photo = photo;
        this.description = description;
        this.category = category;
        this.weight = weight;
        this.price = price;
        this.desire = desire;
        this.dimensions = dimensions;
        this.rating = rating;
        this.amount = amount;
        this.purchased = purchased;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getCategory() {
        return category;
    }

    public void setCategory(String[] category) {
        this.category = category;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getDesire() {
        return desire;
    }

    public void setDesire(int desire) {
        this.desire = desire;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getPurchased() {
        return purchased;
    }

    public void setPurchased(int purchased) {
        this.purchased = purchased;
    }
}
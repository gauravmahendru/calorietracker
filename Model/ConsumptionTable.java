package com.example.calorietracker.Model;

public class ConsumptionTable {

    private double quantity;
    private UserTable userId;
    private FoodTable foodId;

    public ConsumptionTable(double quantity, UserTable userId, FoodTable foodId) {
        this.quantity = quantity;
        this.userId = userId;
        this.foodId = foodId;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public UserTable getUserId() {
        return userId;
    }

    public void setUserId(UserTable userId) {
        this.userId = userId;
    }

    public FoodTable getFoodId() {
        return foodId;
    }

    public void setFoodId(FoodTable foodId) {
        this.foodId = foodId;
    }
}

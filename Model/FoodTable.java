package com.example.calorietracker.Model;

public class FoodTable {

    private Integer foodId;
    private String foodName;

    public FoodTable() {

    }

    public FoodTable(Integer foodId, String foodName, String category, double calorie, String servingUnit, String servingAmount, double fat) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.category = category;
        this.calorie = calorie;
        this.servingUnit = servingUnit;
        this.servingAmount = servingAmount;
        this.fat = fat;
    }

    public Integer getFoodId() {
        return foodId;
    }

    public void setFoodId(Integer foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getCalorie() {
        return calorie;
    }

    public void setCalorie(double calorie) {
        this.calorie = calorie;
    }

    public String getServingUnit() {
        return servingUnit;
    }

    public void setServingUnit(String servingUnit) {
        this.servingUnit = servingUnit;
    }

    public String getServingAmount() {
        return servingAmount;
    }

    public void setServingAmount(String servingAmount) {
        this.servingAmount = servingAmount;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    private String category;
    private double calorie;
    private String servingUnit;
    private String servingAmount;
    private double fat;

}

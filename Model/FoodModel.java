package com.example.calorietracker.Model;

public class FoodModel {
    private String food_name;
    private String food_calories;
    private String food_fat;

    public FoodModel(String food_name, String food_calories, String food_fat){

        this.food_name = food_name;
        this.food_calories = food_calories;
        this.food_fat = food_fat;
    }

    public String getFood_name() {
        return  food_name; }

    public void setName(String food_name) {
        this.food_name = food_name;
    }

    public String getFood_calories() {
        return food_calories;
    }

    public void setCalories(String food_calories) {
        this.food_calories = food_calories;
    }

    public String getFood_fat() {
        return food_fat;
    }

    public void setFood_fat(String food_fat) {
        this.food_fat = food_fat;
    }
}

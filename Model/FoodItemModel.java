package com.example.calorietracker.Model;

public class FoodItemModel {
    private String food_name;
    private String food_id;

    public FoodItemModel(String food_name, String food_id) {
        this.food_name = food_name;
        this.food_id = food_id;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getFood_id() {
        return food_id;
    }

    public void setFood_id(String food_id) {
        this.food_id = food_id;
    }
}

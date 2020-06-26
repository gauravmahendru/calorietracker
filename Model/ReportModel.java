package com.example.calorietracker.Model;

import com.example.calorietracker.User;

public class ReportModel {

    private double calorieConsumed;
    private double calorieBurned;
    private int stepsTaken;
    private double calorieGoal;
    private User userId;

    public ReportModel(double calorieConsumed, double calorieBurned, int stepsTaken, double calorieGoal, User userId) {
        this.calorieConsumed = calorieConsumed;
        this.calorieBurned = calorieBurned;
        this.stepsTaken = stepsTaken;
        this.calorieGoal = calorieGoal;
        this.userId = userId;
    }

    public double getCalorieConsumed() {
        return calorieConsumed;
    }

    public void setCalorieConsumed(double calorieConsumed) {
        this.calorieConsumed = calorieConsumed;
    }

    public double getCalorieBurned() {
        return calorieBurned;
    }

    public void setCalorieBurned(double calorieBurned) {
        this.calorieBurned = calorieBurned;
    }

    public int getStepsTaken() {
        return stepsTaken;
    }

    public void setStepsTaken(int stepsTaken) {
        this.stepsTaken = stepsTaken;
    }

    public double getCalorieGoal() {
        return calorieGoal;
    }

    public void setCalorieGoal(double calorieGoal) {
        this.calorieGoal = calorieGoal;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

}


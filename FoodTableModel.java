package com.example.calorietracker;

public class FoodTableModel {

        private Double calorie;
        private String category;
        private double fat;
        private int foodid;
        private String foodname;
        private String servingamount;
        private String servingunit;

        public FoodTableModel() {
        }

        public FoodTableModel(Double calorie, String category, double fat, int foodid, String foodname, String servingamount, String servingunit) {
            this.calorie = calorie;
            this.category = category;
            this.fat = fat;
            this.foodid = foodid;
            this.foodname = foodname;
            this.servingamount = servingamount;
            this.servingunit = servingunit;
        }

        public Double getCalorie() {
            return calorie;
        }

        public void setCalorie(Double calorie) {
            this.calorie = calorie;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public double getFat() {
            return fat;
        }

        public void setFat(double fat) {
            this.fat = fat;
        }

        public int getFoodid() {
            return foodid;
        }

        public void setFoodid(int foodid) {
            this.foodid = foodid;
        }

        public String getFoodname() {
            return foodname;
        }

        public void setFoodname(String foodname) {
            this.foodname = foodname;
        }

        public String getServingamount() {
            return servingamount;
        }

        public void setServingamount(String servingamount) {
            this.servingamount = servingamount;
        }

        public String getServingunit() {
            return servingunit;
        }

        public void setServingunit(String servingunit) {
            this.servingunit = servingunit;
        }
    }


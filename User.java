package com.example.calorietracker;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    public int userId;
    @ColumnInfo(name = "stepsTaken")
    public int stepsTaken;
    @ColumnInfo(name = "timeEntered")
    public String timeEntered;

    public User(int stepsTaken, String timeEntered) {
        this.stepsTaken = stepsTaken;
        this.timeEntered = timeEntered;
    }

    public int getUid() {
        return userId;
    }

    public void setUid(int uid) {
        this.userId = uid;
    }

    public int getStepsTaken() {
        return stepsTaken;
    }

    public void setStepsTaken(int stepsTaken) {
        this.stepsTaken = stepsTaken;
    }

    public String getTimeEntered() {
        return timeEntered;
    }

    public void setTimeEntered(String timeEntered) {
        this.timeEntered = timeEntered;
    }
}



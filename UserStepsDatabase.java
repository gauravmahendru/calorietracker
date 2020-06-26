package com.example.calorietracker;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


    @Database(entities = {User.class}, version = 1, exportSchema = false)

    public abstract class UserStepsDatabase extends RoomDatabase {

        public abstract UserDAO userDAO();

        private static volatile UserStepsDatabase INSTANCE;

        static UserStepsDatabase getDatabase(final Context context) {
            if (INSTANCE == null) {
                synchronized (UserStepsDatabase.class) {
                    if (INSTANCE == null) {
                        INSTANCE =
                                Room.databaseBuilder(context.getApplicationContext(),
                                        UserStepsDatabase.class, "user_database")
                                        .build();
                    }
                }
            }
            return INSTANCE;
        }
    }







package com.example.calorietracker;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDAO {
    @Query("SELECT * FROM User")
      List<User> getAll();

        @Insert
        long insert(User user);

        @Delete
        void delete(User user);

        @Update(onConflict = REPLACE)
        public void updateUsers(User... users);

        @Query("DELETE FROM User")
        void deleteAll();
}



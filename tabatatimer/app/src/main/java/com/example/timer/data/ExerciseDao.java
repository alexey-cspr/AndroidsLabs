package com.example.timer.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.timer.model.Exercise;

import java.util.List;

@Dao
public interface ExerciseDao {

    @Query("SELECT * FROM Exercise WHERE :uid == training_id")
    List<Exercise> getAll(int uid);

    @Query("SELECT * FROM Exercise")
    LiveData<List<Exercise>> getAllLiveData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)//вставка новой сущности в старую
    void insert(Exercise training);

    @Update
    void update(Exercise training);

    @Delete
    void delete(Exercise training);

}

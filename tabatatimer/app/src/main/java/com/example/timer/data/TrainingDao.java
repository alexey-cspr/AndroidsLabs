package com.example.timer.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.timer.model.Training;

import java.util.List;

@Dao
public interface TrainingDao {

    @Query("SELECT * FROM Training")
    List<Training> getAll();

    @Query("SELECT * FROM Training")
    LiveData<List<Training>> getAllLiveData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)//вставка новой сущности в старую
    void insert(Training training);

    @Update
    void update(Training training);

    @Delete
    void delete(Training training);
}

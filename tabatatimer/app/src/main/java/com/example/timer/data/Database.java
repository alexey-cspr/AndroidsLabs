package com.example.timer.data;

import androidx.room.RoomDatabase;

import com.example.timer.model.Exercise;
import com.example.timer.model.Training;

@androidx.room.Database(entities = {Training.class, Exercise.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {
    public abstract TrainingDao trainingDao();
    public abstract ExerciseDao exerciseDao();
}

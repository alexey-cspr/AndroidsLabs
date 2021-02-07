package com.example.timer;

import android.app.Application;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.timer.data.Database;
import com.example.timer.data.ExerciseDao;
import com.example.timer.data.TrainingDao;

public class App extends Application {

    private Database database;
    private TrainingDao trainingDao;
    private ExerciseDao exerciseDao;

    private static App instance;

    public static App getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(getApplicationContext(), Database.class, "timer-db").allowMainThreadQueries().build();
        trainingDao = database.trainingDao();
        exerciseDao = database.exerciseDao();
    }

    public Database getDatabase(){
        return database;
    }

    public void setDatabase(Database database){
        this.database = database;
    }

    public TrainingDao getTrainingDao(){
        return trainingDao;
    }

    public void setTrainingDao(TrainingDao trainingDao){
        this.trainingDao = trainingDao;
    }

    public ExerciseDao getExerciseDao(){
        return exerciseDao;
    }

}

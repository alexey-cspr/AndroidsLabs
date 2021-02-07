package com.example.timer.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.timer.App;
import com.example.timer.model.Exercise;

import java.util.List;

public class ExerciseViewModel extends ViewModel {

    private LiveData<List<Exercise>> exerciseLiveData = App.getInstance().getExerciseDao().getAllLiveData();

    public LiveData<List<Exercise>> getExerciseLiveData(){
        return exerciseLiveData;
    }

    public List<Exercise> getExercisesById (int id){
        return App.getInstance().getExerciseDao().getAll(id);
    }

}

package com.example.timer.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.timer.App;
import com.example.timer.model.Training;

import java.util.List;

public class MainViewModel extends ViewModel {
    private LiveData<List<Training>> trainingLiveData = App.getInstance().getTrainingDao().getAllLiveData();

    public LiveData<List<Training>> getTrainingLiveData(){
        return trainingLiveData;
    }
}

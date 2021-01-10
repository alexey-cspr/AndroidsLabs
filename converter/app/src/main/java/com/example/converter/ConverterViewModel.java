package com.example.converter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ConverterViewModel extends ViewModel {
    private MutableLiveData<String> currentCategory = new MutableLiveData<String>("");
    private MutableLiveData<String> InputData = new MutableLiveData<String>("");
    private MutableLiveData<String> OutputData = new MutableLiveData<String>();
    private MutableLiveData<String> currentFromCategory = new MutableLiveData<String>();
    private MutableLiveData<String> currentToCategory = new MutableLiveData<String>();
    private Convert converter;

    public ConverterViewModel(){
        converter = new Convert();
    }

    public void SetCurCategory(String category){
        if(currentCategory == null){
            currentCategory = new MutableLiveData<String>();
        }
        currentCategory.setValue(category);
    }

    public LiveData<String> GetCurCategory(){
        if(currentCategory == null){
            currentCategory = new MutableLiveData<String>();
        }
        return currentCategory;
    }

    public LiveData<String> GetInputData(){
        if(InputData == null){
            InputData = new MutableLiveData<String>();
        }
        return InputData;
    }

    public LiveData<String> GetOutputData(){
        if(OutputData == null){
            OutputData = new MutableLiveData<String>();
        }
        return OutputData;
    }

    public LiveData<String> GetCurrentFromCategory(){
        if(currentFromCategory == null){
            currentFromCategory = new MutableLiveData<String>();
        }
        return currentFromCategory;
    }

    public LiveData<String> GetCurrentToCategory(){
        if(currentToCategory == null){
            currentToCategory = new MutableLiveData<String>();
        }
        return currentToCategory;
    }

    public ArrayList<String> GetCategoriesNames(String category){
        return converter.getNames(category);
    }

    public void SetCurFromCategory(String category){
        if(currentFromCategory == null){
            currentFromCategory = new MutableLiveData<String>();
        }
        currentFromCategory.setValue(category);
    }

    public void SetCurToCategory(String category){
        if(currentToCategory == null){
            currentToCategory = new MutableLiveData<String>();
        }
        currentToCategory.setValue(category);
    }

    public void setOutputData(String data){
        if(OutputData == null){
            OutputData = new MutableLiveData<String>();
        }
        OutputData.setValue(data);
    }

    public void setInputData(String data){
        if (data.charAt(data.length()-1) == '⌫'){
            data = "";
        }
        InputData.setValue(data);
        setOutputData(Converting(data));
    }

    private String Converting(String data) {
        double co_one, co_two;
        co_one = converter.getCoefficient(currentCategory.getValue(), GetCurrentFromCategory().getValue());
        co_two = converter.getCoefficient(currentCategory.getValue(), GetCurrentToCategory().getValue());
        String result = converter.Converting(data, co_one, co_two);
        return result;
    }

}
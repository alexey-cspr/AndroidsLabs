package com.example.converter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
public class MainActivity extends AppCompatActivity {

    ConverterViewModel cvModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cvModel = new ViewModelProvider(this).get(ConverterViewModel.class);
        cvModel.SetCurCategory("Distance");
    }

    public void time_clicked(View view) {
        cvModel.SetCurCategory("Time");
    }
    public void distance_clicked(View view) {
        cvModel.SetCurCategory("Distance");
    }
    public void weight_clicked(View view) {
        cvModel.SetCurCategory("Weight");
    }
    public void temperature_clicked(View view){ cvModel.SetCurCategory("Temperature");}

}
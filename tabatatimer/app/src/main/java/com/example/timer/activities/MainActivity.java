package com.example.timer.activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.timer.App;
import com.example.timer.R;
import com.example.timer.model.Training;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        float font_size = Float.parseFloat(sharedPreferences.getString("font", "1.0"));
        String language = sharedPreferences.getString("language", "RU");
        Configuration configuration = new Configuration();
        configuration.fontScale = font_size;
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        configuration.locale = locale;
        this.getResources().updateConfiguration(configuration, null);

        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.trainingList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        TrainingAdapter adapter = new TrainingAdapter();
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Training training = new Training();
                training.name = "New azhumanie";
                Random random = new Random();
                training.line_colour = Color.argb(78, random.nextInt(79),random.nextInt(79),random.nextInt(79));
                App.getInstance().getTrainingDao().insert(training);
            }
        });

        MainViewModel mainVM = ViewModelProviders.of(this).get(MainViewModel.class);

        mainVM.getTrainingLiveData().observe(this, new Observer<List<Training>>() {
            @Override
            public void onChanged(List<Training> trainings) {
                adapter.setItems(trainings);
            }
        });
    }

    public void settings_button(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
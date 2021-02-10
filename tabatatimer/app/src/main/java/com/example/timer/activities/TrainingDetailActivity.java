package com.example.timer.activities;

import androidx.lifecycle.Observer;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timer.App;
import com.example.timer.R;
import com.example.timer.model.Exercise;
import com.example.timer.model.Training;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class TrainingDetailActivity extends AppCompatActivity {

    private static final String EXTRA_TRAINING = "TrainingDetailActivity.EXTRA_TRAINING";

    private RecyclerView recyclerView;
    private Training training;
    private EditText editText;
    private Button deleteButton;
    private ImageButton saveButton;
    private ImageButton addButton;

    public static void start(Activity caller, Training training){
        Intent intent = new Intent(caller, TrainingDetailActivity.class);
        if (training != null){ intent.putExtra(EXTRA_TRAINING, training); }
        caller.startActivity(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SettingsFragment.setConfiguration(this);

        setContentView(R.layout.activity_training_detail);

        recyclerView = findViewById(R.id.exerciseList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ExerciseAdapter adapter = new ExerciseAdapter();
        recyclerView.setAdapter(adapter);

        editText = findViewById(R.id.text_training);
        saveButton = findViewById(R.id.save_training);
        addButton = findViewById(R.id.add_exercise);
        deleteButton = findViewById(R.id.delete_training_1);

        if(getIntent().hasExtra(EXTRA_TRAINING)){
            training = getIntent().getParcelableExtra(EXTRA_TRAINING);
            editText.setText(training.name);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                training.name = editText.getText().toString();
                if(getIntent().hasExtra(EXTRA_TRAINING)){
                    App.getInstance().getTrainingDao().update(training);
                }
                else{
                    Random random = new Random();
                    training.line_colour = Color.argb(78, random.nextInt(79),random.nextInt(79),random.nextInt(79));
                    App.getInstance().getTrainingDao().insert(training);
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Exercise exercise = new Exercise();
                exercise.name = "New exercise";
                exercise.time = 7;
                exercise.training_id = training.uid;
                App.getInstance().getExerciseDao().insert(exercise);
            }
        });

        deleteButton.setOnClickListener(view -> {
            App.getInstance().getTrainingDao().delete(training);
            finish();
        });

        ExerciseViewModel exerciseVM = ViewModelProviders.of(this).get(ExerciseViewModel.class);
        exerciseVM.getExerciseLiveData().observe(this, new Observer<List<Exercise>>(){
            @Override
            public void onChanged(List<Exercise> exercises ) {
                List<Exercise> exercises1 = exerciseVM.getExercisesById(training.uid);
                adapter.setItems(exercises1);
            }
        });
    }
    public void start_timer(View view){
        TimerActivity.start(this,training);
    }
}

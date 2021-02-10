package com.example.timer.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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

import static android.Manifest.permission.FOREGROUND_SERVICE;

public class TimerActivity extends AppCompatActivity implements SoundPool.OnLoadCompleteListener{

    private int remain;
    private TextView exercise_time;
    private TextView exercise_name;
    public List<Exercise> exercises;
    private BroadcastReceiver broadcastRec;
    private Training training;
    private static final String EXTRA_T = "Training";
    private RecyclerView recyclerView;
    private SoundPool sound;
    private int soundId;
    private boolean timerIsRunning = false;
    private boolean paused = false;

    public static void start(Activity caller, Training training){
        Intent intent = new Intent(caller, TimerActivity.class);
        if (training != null){ intent.putExtra(EXTRA_T, training); }
        caller.startActivity(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SettingsFragment.setConfiguration(this);

        setContentView(R.layout.timer_activity);

        sound = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        sound.setOnLoadCompleteListener(this);
        soundId = sound.load(this, R.raw.sound, 1);

        recyclerView = findViewById(R.id.timerExerciseList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        TimerAdapter adapter = new TimerAdapter();
        recyclerView.setAdapter(adapter);

        this.training = getIntent().getParcelableExtra(EXTRA_T);
        exercises = App.getInstance().getExerciseDao().getAll(training.uid);
        adapter.setItems(exercises);

        exercise_time = findViewById(R.id.current_exercise_time);
        exercise_name = findViewById(R.id.current_exercise_name);

        ActivityCompat.requestPermissions(this, new String[]{FOREGROUND_SERVICE}, PackageManager.PERMISSION_GRANTED);

        IntentFilter filter = new IntentFilter();
        filter.addAction("timer");

        broadcastRec = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Long time = intent.getLongExtra("Remaining", 0);
                exercise_time.setText(time.toString());

                if (intent.hasExtra("Finished")){
                    sound.play(soundId, 2,2,0,0,1);
                    exercises.remove(0);
                    adapter.setItems(exercises);
                    startExercise();
                }
            }
        };
        registerReceiver(broadcastRec, filter);
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

    }

    public void startExercise(){
        if(exercises.size() != 0){
            Intent service = new Intent(this, MyService.class);
            exercise_name.setText(exercises.get(0).name);
            service.putExtra("TimeValue",exercises.get(0).time);
            startService(service);
        }
        else{
            timerIsRunning = false;
            unregisterReceiver(broadcastRec);
            finish();
        }
    }
//
    public void pause_button(View view){
        if(timerIsRunning && !paused){
            paused = true;
            remain = Integer.valueOf(exercise_time.getText().toString());
            Intent local = new Intent();
            local.setAction("Paused");
            sendBroadcast(local);
        }
    }
//
    public void play_button(View view){
        if(timerIsRunning == false && paused == false){
            timerIsRunning = true;
            startExercise();
        }
        else{
            if (paused){
                paused = false;
                Intent service = new Intent(this, MyService.class);
                service.putExtra("TimeValue", remain);
                startService(service);
            }
        }
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        unregisterReceiver(broadcastRec);
        exercises.clear();
        finish();
    }

}


package com.example.timer.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.timer.R;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }
}

package com.example.timer.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.Locale;


public class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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

    }
}

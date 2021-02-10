package com.example.timer.activities;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.example.timer.R;

import java.util.Locale;

import static android.content.Context.ACTIVITY_SERVICE;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        SwitchPreferenceCompat switchPreferenceCompatMode = findPreference("theme");


        switchPreferenceCompatMode.setOnPreferenceChangeListener((preference, newValue) -> {
            if ((boolean)newValue)
            {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
            }
            else
            {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
            }
            return true;
        });

        ListPreference listPreferenceFont = findPreference("font");
        listPreferenceFont.setOnPreferenceChangeListener((preference, newValue) -> {
            getActivity().recreate();
            return true;
        });

        ListPreference listPreferenceLanguage = findPreference("language");
        listPreferenceLanguage.setOnPreferenceChangeListener((preference, newValue) -> {
            preference.setDefaultValue(newValue);
            Locale locale = new Locale(newValue.toString());
            Locale.setDefault(locale);
            Configuration configuration = new Configuration();
            configuration.locale = locale;
            getActivity().getResources().updateConfiguration(configuration, null);
            getActivity().recreate();
            return true;
        });

        Preference clear_data = findPreference("clear_data");

        clear_data.setOnPreferenceClickListener(preference -> {

            new AlertDialog.Builder(getActivity())
                    .setTitle("Удаление данных")
                    .setMessage("Удалить все пользовательские данные?")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int whichButton)
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                ((ActivityManager)requireActivity().getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
                            }
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Нет", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int whichButton)
                        {
                            dialog.dismiss();
                        }
                    }).show();
            return true;
        });
    }

    public static void setConfiguration(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        float font_size = Float.parseFloat(sharedPreferences.getString("font", "1.0"));
        String language = sharedPreferences.getString("language", "RU");
        Configuration configuration = new Configuration();
        configuration.fontScale = font_size;
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        configuration.locale = locale;
        context.getResources().updateConfiguration(configuration, null);
    }

}
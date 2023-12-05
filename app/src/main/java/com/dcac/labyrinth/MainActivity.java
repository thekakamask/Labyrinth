package com.dcac.labyrinth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.dcac.labyrinth.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String lastAppliedTheme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySelectedTheme();
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("AppSettingsPrefs", MODE_PRIVATE);
        String currentTheme = prefs.getString("SelectedTheme", "BaseTheme");

        if (lastAppliedTheme == null) {
            lastAppliedTheme = currentTheme;
        } else if (!lastAppliedTheme.equals(currentTheme)) {
            lastAppliedTheme = currentTheme;
            recreate(); // Recrée l'activité si le thème a changé
        }
    }

    private void applySelectedTheme() {
        SharedPreferences prefs = getSharedPreferences("AppSettingsPrefs", MODE_PRIVATE);
        String themeName = prefs.getString("SelectedTheme", "BaseTheme");

        if (themeName.equals("LightTheme")) {
            setTheme(R.style.LightTheme);
        } else if (themeName.equals("DarkTheme")) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.BaseTheme);
        }
    }
}
package com.dcac.labyrinth.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.dcac.labyrinth.R;
import com.dcac.labyrinth.databinding.ActivityMainBinding;
import com.dcac.labyrinth.ui.activities.fragments.WelcomeFragment;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private String lastAppliedTheme;

    protected ActivityMainBinding getViewBinding(){
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        applySelectedTheme();
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new WelcomeFragment())
                    .commit();
        }
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
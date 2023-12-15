package com.dcac.labyrinth.ui.parameters;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ExpandableListView;

import com.dcac.labyrinth.data.ParametersItem;
import com.dcac.labyrinth.R;
import com.dcac.labyrinth.databinding.ActivityParametersBinding;
import com.dcac.labyrinth.ui.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParametersActivity extends BaseActivity<ActivityParametersBinding> implements ParametersActivityAdapter.OnThemeChangeListener {

    private ExpandableListView expandableListView;

    private ParametersActivityAdapter parametersActivityAdapter;
    private List<String> listTabsTitle;

    private HashMap<String, List<ParametersItem>> listTabOngletsData;


    protected ActivityParametersBinding getViewBinding(){
        return ActivityParametersBinding.inflate(getLayoutInflater());
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        applySelectedTheme();
        super.onCreate(savedInstanceState);

        prepareListData(); // Préparez vos données ici

        expandableListView = findViewById(R.id.expandableListView);
        parametersActivityAdapter = new ParametersActivityAdapter(this,listTabsTitle, listTabOngletsData, this);
        expandableListView.setAdapter(parametersActivityAdapter);


    }

    private void prepareListData() {
        SharedPreferences prefs = getSharedPreferences("AppSettingsPrefs", MODE_PRIVATE);
        int selectedThemeIndex = prefs.getInt("SelectedThemeIndex", 0); // 0 est l'index par défaut pour BaseTheme

        listTabsTitle = new ArrayList<>();
        listTabOngletsData = new HashMap<>();

        List<ParametersItem> themes = new ArrayList<>();
        List<ParametersItem> userCommands = new ArrayList<>();
        List<ParametersItem> thirdGroup = new ArrayList<>();

        for (int i = 0; i < themes.size(); i++) {
            themes.get(i).setChecked(i == selectedThemeIndex);
        }

        listTabsTitle.add("Themes");
        listTabsTitle.add("Global title example 2");
        listTabsTitle.add("Global title example 3");

        themes.add(new ParametersItem("Base theme", "this theme is the basic theme", selectedThemeIndex == 0));
        themes.add(new ParametersItem("Light theme", "this theme is for the day", selectedThemeIndex == 1));
        themes.add(new ParametersItem("Dark theme", "this theme is for the night", selectedThemeIndex == 2));

        userCommands.add(new ParametersItem("Title example 1", "Description example 1", false));
        userCommands.add(new ParametersItem("Title example 2", "Description example 2", false));
        userCommands.add(new ParametersItem("Title example 3", "Description example 3", false));

        thirdGroup.add(new ParametersItem("Title example 1", "Description example 1", false));
        thirdGroup.add(new ParametersItem("Title example 2", "Description example 2", false));
        thirdGroup.add(new ParametersItem("Title example 3", "Description example 3", false));


        listTabOngletsData.put(listTabsTitle.get(0), themes);
        listTabOngletsData.put(listTabsTitle.get(1), userCommands);
        listTabOngletsData.put(listTabsTitle.get(2), thirdGroup);
    }

    private void applySelectedTheme() {
        SharedPreferences prefs = getSharedPreferences("AppSettingsPrefs", MODE_PRIVATE);
        String themeName = prefs.getString("SelectedTheme", "BaseTheme"); // "BaseTheme" est le thème par défaut

        if (themeName.equals("LightTheme")) {
            setTheme(R.style.LightTheme);
        } else if (themeName.equals("DarkTheme")) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.BaseTheme);
        }
    }
    @Override
    public void onThemeChanged(String themeName) {
        changeTheme(themeName);
    }

    public void changeTheme(String themeName) {
        SharedPreferences prefs = getSharedPreferences("AppSettingsPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("SelectedTheme", themeName);
        editor.apply();

        recreate(); // Redémarrer l'activité pour appliquer le thème
    }
}
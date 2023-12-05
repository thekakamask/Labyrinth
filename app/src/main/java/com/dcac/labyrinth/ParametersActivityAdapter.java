package com.dcac.labyrinth;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.constraintlayout.widget.Group;

import java.util.HashMap;
import java.util.List;

public class ParametersActivityAdapter extends BaseExpandableListAdapter {

    private Context context;

    private List<String> listTabsTitle;
    private HashMap<String, List<ParametersItem>> listTabOngletsData;

    private OnThemeChangeListener onThemeChangeListener;

    public ParametersActivityAdapter(Context context, List<String> listGroupTitles,
                                                   HashMap<String, List<ParametersItem>> listChildData, OnThemeChangeListener onThemeChangeListener) {
        this.context = context;
        this.listTabsTitle = listGroupTitles;
        this.listTabOngletsData = listChildData;
        this.onThemeChangeListener = onThemeChangeListener;
    }

    @Override
    public int getGroupCount() {
        return listTabsTitle.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listTabOngletsData.get(this.listTabsTitle.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listTabsTitle.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listTabOngletsData.get(this.listTabsTitle.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String groupTitle=(String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.tabs, null);
        }
        TextView tvGroup = convertView.findViewById(R.id.tabs_onglets);
        tvGroup.setText(groupTitle);
        return convertView;
    }

    static class ViewHolder {
        TextView element_child_title;
        TextView element_child_description;
        CheckBox element_child_checkbox;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder;

        final ParametersItem childItem = (ParametersItem) getChild(groupPosition, childPosition);

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.tabs_elements, parent, false);
            holder.element_child_title= convertView.findViewById(R.id.element_child_title);
            holder.element_child_description= convertView.findViewById(R.id.element_child_description);
            holder.element_child_checkbox= convertView.findViewById(R.id.elements_child_checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.element_child_title.setText(childItem.getTitle());
        holder.element_child_description.setText(childItem.getDescription());
        holder.element_child_checkbox.setChecked(childItem.isChecked());

        holder.element_child_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Décocher toutes les autres CheckBox du groupe
                for (int i = 0; i < getChildrenCount(groupPosition); i++) {
                    ParametersItem item = listTabOngletsData.get(listTabsTitle.get(groupPosition)).get(i);
                    item.setChecked(i == childPosition);
                }

                // Cocher la CheckBox actuelle
                //childItem.setChecked(true);
                notifyDataSetChanged(); // Rafraîchir la vue

                if (groupPosition == 0) { // Si c'est le groupe des thèmes
                    String themeName = null;
                    int themeIndex = -1;
                    switch (childItem.getTitle()) {
                        case "Light theme":
                            themeName = "LightTheme";
                            themeIndex = 1;
                            break;
                        case "Dark theme":
                            themeName = "DarkTheme";
                            themeIndex = 2;
                            break;
                        default:
                            themeName = "BaseTheme";
                            themeIndex = 0;
                            break;
                    }
                    SharedPreferences prefs = context.getSharedPreferences("AppSettingsPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("SelectedThemeIndex", themeIndex);
                    editor.apply();

                    onThemeChangeListener.onThemeChanged(themeName);
                }
            }
        });

        holder.element_child_checkbox.setChecked(childItem.isChecked());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public interface OnThemeChangeListener {
        void onThemeChanged(String themeName);
    }

}

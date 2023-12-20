package com.dcac.labyrinth.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dcac.labyrinth.R;
import com.dcac.labyrinth.viewModels.UserManager;
import com.dcac.labyrinth.databinding.ActivityAccountBinding;
import com.google.firebase.auth.FirebaseUser;

public class AccountActivity extends BaseActivity<ActivityAccountBinding> {

    // RAJOUTER LES THEMES
    private UserManager userManager = UserManager.getInstance();

    private String lastAppliedTheme;

    protected ActivityAccountBinding getViewBinding(){
        return ActivityAccountBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applySelectedTheme();
        setupListeners();
        updateUIWithUserData();

        binding.buttonChangeUsername.setEnabled(true);
        binding.buttonDeconnection.setEnabled(true);
        binding.buttonDeleteAccount.setEnabled(true);

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

    private void updateUIWithUserData() {
        if (userManager.isCurrentUserLogged()) {
            FirebaseUser user = userManager.getCurrentUser();

            if (user.getPhotoUrl() != null) {
                setProfilePicture(user.getPhotoUrl());
            }
            setTextUserData(user);
        }
    }

    private void setProfilePicture(Uri profilePictureUrl) {
        Glide.with(this)
                .load(profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.profileImage);
    }

    private void setTextUserData(FirebaseUser user){

        //GET EMAIL AND USERNAME FROM USER
        String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.info_no_email_found) : user.getEmail();
        String username = TextUtils.isEmpty(user.getDisplayName()) ? getString(R.string.info_no_username_found) : user.getDisplayName();

        //UPDATE VIEW WITH DATA
        binding.userIdNameInput.setText(username);
        binding.userMail.setText(email);
    }


    private void setupListeners() {
        binding.buttonChangeUsername.setOnClickListener(view -> {
            binding.userIdNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        });

        binding.buttonDeconnection.setOnClickListener(view -> {
            userManager.signOut(this).addOnSuccessListener(aVoid -> {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            });
        });

        // DELETE BUTTON
        binding.buttonDeleteAccount.setOnClickListener(view -> {

            new AlertDialog.Builder(this)
                    .setMessage(R.string.popup_message_confirmation_delete_account)
                    .setPositiveButton(R.string.popup_message_choice_yes, (dialogInterface, i) ->
                            userManager.deleteUser(AccountActivity.this)
                                    .addOnSuccessListener(aVoid -> {
                                        Intent returnIntent = new Intent();
                                        setResult(Activity.RESULT_OK, returnIntent);
                                        finish();
                                            }
                                    )
                    )
                    .setNegativeButton(R.string.popup_message_choice_no, null)
                    .show();

        });
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
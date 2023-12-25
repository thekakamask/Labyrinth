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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dcac.labyrinth.R;
import com.dcac.labyrinth.data.models.User;
import com.dcac.labyrinth.viewModels.UserManager;
import com.dcac.labyrinth.databinding.ActivityAccountBinding;
import com.google.firebase.auth.FirebaseUser;

public class AccountActivity extends BaseActivity<ActivityAccountBinding> {

    // RAJOUTER LES THEMES
    private UserManager userManager = UserManager.getInstance();

    private String lastAppliedTheme;
    private boolean isEditingUsername = false;

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
            FirebaseUser firebaseUser = userManager.getCurrentUser();

            userManager.getUserData(firebaseUser.getUid()).addOnSuccessListener(documentSnapshot -> {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    displayUserInfo(user);
                }
            }).addOnFailureListener( e -> {
                Toast.makeText(this, "Error with the recuperation tentative", Toast.LENGTH_SHORT).show();
            });

            /*if (user.getPhotoUrl() != null) {
                setProfilePicture(user.getPhotoUrl());
            }
            setTextUserData(user);*/
        }
    }

    private void setProfilePicture(Uri profilePictureUrl) {
        Glide.with(this)
                .load(profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.profileImage);
    }

    /*private void setTextUserData(FirebaseUser user){

        //GET EMAIL AND USERNAME FROM USER
        String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.info_no_email_found) : user.getEmail();
        String username = TextUtils.isEmpty(user.getDisplayName()) ? getString(R.string.info_no_username_found) : user.getDisplayName();

        //UPDATE VIEW WITH DATA
        binding.userIdNameInput.setText(username);
        binding.userMail.setText(email);
    }*/


    private void setupListeners() {
        binding.buttonChangeUsername.setOnClickListener(view -> {
            if (!isEditingUsername) {
                binding.userIdNameInput.setEnabled(true);
                binding.userIdNameInput.requestFocus();
                binding.buttonChangeUsername.setText(R.string.confirm_change_username);
                isEditingUsername = true;
            } else {
                String newUsername = binding.userIdNameInput.getText().toString();
                updateUserName(newUsername);

                binding.userIdNameInput.setEnabled(false);
                binding.buttonChangeUsername.setText(R.string.update_username);

                isEditingUsername = false;
            }

            binding.userIdNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String newUserName = s.toString();
                updateUserName(newUserName);
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

    private void displayUserInfo(User user) {
        if (user.getUrlPicture() != null && !user.getUrlPicture().isEmpty()) {
            setProfilePicture(Uri.parse(user.getUrlPicture()));
        }

        // Afficher le nom d'utilisateur et l'e-mail
        binding.userIdNameInput.setText(user.getUserName());
        binding.userMail.setText(user.getEmail());
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

    private void updateUserName(String newUserName) {
        if (!TextUtils.isEmpty(newUserName)) {
            FirebaseUser firebaseUser = userManager.getCurrentUser();
            if (firebaseUser != null) {
                String uid = firebaseUser.getUid();
                userManager.updateUserName(uid, newUserName);
            }
        }
    }

}
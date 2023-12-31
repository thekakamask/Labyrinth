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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dcac.labyrinth.R;
import com.dcac.labyrinth.data.models.User;
import com.dcac.labyrinth.viewModels.UserManager;
import com.dcac.labyrinth.databinding.ActivityAccountBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class AccountActivity extends BaseActivity<ActivityAccountBinding> {

    // RAJOUTER LES THEMES
    private UserManager userManager = UserManager.getInstance();

    private String lastAppliedTheme;
    private boolean isEditingUsername = false;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        if (uri != null) {
            setProfilePicture(uri);
            uploadImageToFirestore(uri);
        }
    });



    protected ActivityAccountBinding getViewBinding(){
        return ActivityAccountBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        applySelectedTheme();
        super.onCreate(savedInstanceState);
        setupListeners();
        updateUIWithUserData();

        binding.buttonChangeUsername.setEnabled(true);
        binding.buttonDeconnection.setEnabled(true);
        binding.buttonDeleteAccount.setEnabled(true);
        binding.buttonChangeImage.setEnabled(true);

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
            recreate();
        }
    }

    private void updateUIWithUserData() {
        if (userManager.isCurrentUserLogged()) {
            FirebaseUser firebaseUser = userManager.getCurrentUser();

            userManager.getUserData(firebaseUser.getUid()).addOnSuccessListener(documentSnapshot -> {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    displayUserInfo(user);
                    //displayUserScore(user.getScore());
                }
            }).addOnFailureListener( e -> Toast.makeText(this, "Error with the recuperation tentative", Toast.LENGTH_SHORT).show());

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

        binding.buttonDeconnection.setOnClickListener(view -> userManager.signOut(this).addOnSuccessListener(aVoid -> {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }));

        // DELETE BUTTON
        binding.buttonDeleteAccount.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setMessage(R.string.popup_message_confirmation_delete_account)
                .setPositiveButton(R.string.popup_message_choice_yes, (dialogInterface, i) -> {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {

                        String uid = user.getUid();

                        user.delete().addOnSuccessListener(aVoid-> {
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(AccountActivity.this, R.string.error_deleting_account, Toast.LENGTH_SHORT).show();
                        });

                        FirebaseFirestore.getInstance().collection("users").document(uid).delete().addOnSuccessListener(aVoid -> {

                        }).addOnFailureListener(e -> {
                            Toast.makeText(AccountActivity.this, R.string.error_deleting_storage, Toast.LENGTH_SHORT).show();
                        });

                        FirebaseStorage.getInstance().getReference().child("profileImages/" + uid).delete().addOnSuccessListener(aVoid -> {

                        }).addOnFailureListener(e -> {
                            Toast.makeText(AccountActivity.this, R.string.error_deleting_image, Toast.LENGTH_SHORT).show();

                        });



                        /*FirebaseFirestore.getInstance().collection("users").document(uid).delete()
                                .addOnSuccessListener(aVoid -> {

                                    FirebaseStorage.getInstance().getReference().child("profileImages/" + uid).delete()
                                            .addOnSuccessListener(aVoidStorage -> {

                                                user.delete().addOnSuccessListener(aVoidAuth -> {

                                                    Intent returnIntent = new Intent();
                                                    setResult(Activity.RESULT_OK, returnIntent);
                                                    finish();
                                                }).addOnFailureListener(e -> {
                                                    Toast.makeText(AccountActivity.this, R.string.error_deleting_account, Toast.LENGTH_SHORT).show();
                                                });
                                            }).addOnFailureListener(e -> {
                                                Toast.makeText(AccountActivity.this, R.string.error_deleting_storage, Toast.LENGTH_SHORT).show();
                                            });
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(AccountActivity.this, R.string.error_deleting_firestore, Toast.LENGTH_SHORT).show();
                                });*/
                    }
                })
                .setNegativeButton(R.string.popup_message_choice_no, null)
                .show());

        binding.buttonChangeImage.setOnClickListener(view -> mGetContent.launch("image/*"));

    }



    private void displayUserInfo(User user) {
        if (user.getUrlPicture() != null && !user.getUrlPicture().isEmpty()) {
            setProfilePicture(Uri.parse(user.getUrlPicture()));
        }

        // Afficher le nom d'utilisateur et l'e-mail
        binding.userIdNameInput.setText(user.getUserName());
        binding.userMail.setText(user.getEmail());
        binding.userScore.setText(String.valueOf(user.getScore()));
    }

    /*private void displayUserScore(int score) {
        binding.userScore.setText(String.valueOf(score));
    }*/



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
            FirebaseUser currentUser = userManager.getCurrentUser();
            if (currentUser != null) {
                String uid = currentUser.getUid();
                userManager.updateUserName(uid, newUserName);
            }
        }
    }

    private void uploadImageToFirestore(Uri imageUri) {

        FirebaseUser currentUser = userManager.getCurrentUser();
        if (currentUser != null && imageUri != null) {
            String uid = currentUser.getUid();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profileImages/" + uid);

            storageReference.putFile(imageUri)
                    .continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }
                        return storageReference.getDownloadUrl();
                    })
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            userManager.updateUrlPicture(uid, downloadUri.toString());
                        } else {
                            // Gérer l'erreur de téléchargement
                            Toast.makeText(AccountActivity.this, R.string.upload_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
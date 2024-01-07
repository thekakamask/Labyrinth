package com.dcac.labyrinth.ui.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dcac.labyrinth.R;
import com.dcac.labyrinth.data.models.User;
import com.dcac.labyrinth.data.utils.Resource;
import com.dcac.labyrinth.injection.UserViewModelFactory;
import com.dcac.labyrinth.viewModels.UserViewModel;
import com.dcac.labyrinth.databinding.ActivityAccountBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class AccountActivity extends BaseActivity<ActivityAccountBinding> {

    // RAJOUTER LES THEMES
    private UserViewModel userViewModel;

    private String lastAppliedTheme;
    private boolean isEditingUsername = false;

    private static final int MIN_USERNAME_LENGTH = 3;

    private FirebaseAuth.AuthStateListener authStateListener;

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
        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                updateUIWithUserData();
            } else {
                redirectToWelcomeFragment("");
            }
        };
        super.onCreate(savedInstanceState);
        UserViewModelFactory factory = UserViewModelFactory.getInstance(getApplicationContext());
        userViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);
        setupListeners();


        binding.buttonChangeUsername.setEnabled(true);
        binding.buttonDeconnection.setEnabled(true);
        binding.buttonDeleteAccount.setEnabled(true);
        binding.buttonChangeImage.setEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
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
            recreate();
        }

    }



    private void updateUIWithUserData() {
        userViewModel.getCurrentUser().observe(this, resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS) {
                FirebaseUser firebaseUser = resource.data;
                if (firebaseUser != null) {
                    String uid = firebaseUser.getUid();
                    updateUserDetails(uid);
                }
            } else if (resource != null && resource.status == Resource.Status.ERROR) {
                // Ne pas afficher de Toast si l'utilisateur est déconnecté
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    Toast.makeText(this, getString(R.string.error_fetching_user_data), Toast.LENGTH_SHORT).show();
                }
                redirectToWelcomeFragment("");
            }
        });
    }

    private void updateUserDetails(String uid) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userViewModel.getUserData(uid).observe(this, userDataResource -> {
                if (userDataResource != null && userDataResource.status == Resource.Status.SUCCESS) {
                    DocumentSnapshot documentSnapshot = userDataResource.data;
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        displayUserInfo(user);
                    } else {
                        Toast.makeText(this, R.string.user_data_not_found, Toast.LENGTH_SHORT).show();
                    }
                } else if (userDataResource != null && userDataResource.status == Resource.Status.ERROR) {
                    Toast.makeText(this, R.string.error_fetching_user_data, Toast.LENGTH_SHORT).show();
                }
            });
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
                if (!TextUtils.isEmpty(newUsername) && newUsername.length() >= MIN_USERNAME_LENGTH) {
                    userViewModel.updateUserName(FirebaseAuth.getInstance().getCurrentUser().getUid(), newUsername)
                            .observe(this, updateResource -> {
                                if (updateResource != null && updateResource.status == Resource.Status.SUCCESS) {
                                    //Toast.makeText(this, R.string.username_updated_successfully, Toast.LENGTH_SHORT).show();
                                    showSnackBar(getString(R.string.username_changed));
                                } else if (updateResource != null && updateResource.status == Resource.Status.ERROR) {
                                    Toast.makeText(this, R.string.failed_to_update_username, Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(this, R.string.invalid_username, Toast.LENGTH_SHORT).show();
                }
                binding.userIdNameInput.setEnabled(false);
                binding.buttonChangeUsername.setText(R.string.update_username);
                isEditingUsername = false;
            }
        });

        binding.buttonChangeImage.setOnClickListener(view -> {
            // Lancer l'intent pour choisir une image
            mGetContent.launch("image/*");
        });

        binding.buttonDeconnection.setOnClickListener(view ->
                userViewModel.signOut().observe(this, signOutResource -> {
                    if (signOutResource != null) {
                        if (signOutResource.status == Resource.Status.SUCCESS) {
                            //showSnackBar(getString(R.string.logged_out));
                            redirectToWelcomeFragment("LoggedOut");
                        } else if (signOutResource.status == Resource.Status.ERROR) {
                            Toast.makeText(this, R.string.deconnection_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
        );

        // DELETE BUTTON
        binding.buttonDeleteAccount.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setMessage(R.string.popup_message_confirmation_delete_account)
                .setPositiveButton(R.string.popup_message_choice_yes, (dialogInterface, i) -> userViewModel.deleteUser().observe(this, deleteResource -> {
                    if (deleteResource != null && deleteResource.status == Resource.Status.SUCCESS) {
                        //showSnackBar(getString(R.string.account_deleted_success));
                        redirectToWelcomeFragment("AccountDeleted");
                    } else if (deleteResource != null && deleteResource.status == Resource.Status.ERROR) {
                        Toast.makeText(this, R.string.delete_user_failed, Toast.LENGTH_SHORT).show();
                    }
                }))
                .setNegativeButton(R.string.popup_message_choice_no, null)
                .show());
    }

    private void redirectToWelcomeFragment(String action) {
        SharedPreferences prefs = getSharedPreferences("AppSettingsPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("LastAction", action);
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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

    /*private void updateUserName(String newUserName) {
        if (!TextUtils.isEmpty(newUserName) && newUserName.length() >= MIN_USERNAME_LENGTH) {
            userViewModel.getCurrentUser().observe(this, currentUserResource -> {
                if (currentUserResource != null && currentUserResource.status == Resource.Status.SUCCESS) {
                    FirebaseUser currentUser = currentUserResource.data;
                    if (currentUser != null) {
                        String uid = currentUser.getUid();
                        userViewModel.updateUserName(uid, newUserName).observe(this, updateResource -> {
                            if (updateResource != null && updateResource.status == Resource.Status.SUCCESS) {
                                Toast.makeText(this, R.string.username_updated_successfully, Toast.LENGTH_SHORT).show();
                            } else if (updateResource != null && updateResource.status == Resource.Status.ERROR) {
                                Toast.makeText(this, R.string.failed_to_update_username, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        } else {
            Toast.makeText(this, R.string.invalid_username, Toast.LENGTH_SHORT).show();
        }
    }*/

    private void uploadImageToFirestore(Uri imageUri) {
        Log.d("UploadImage", "Attempting to upload image: " + imageUri);
        if (imageUri != null) {
            userViewModel.getCurrentUser().observe(this, currentUserResource -> {
                if (currentUserResource != null && currentUserResource.status == Resource.Status.SUCCESS) {
                    FirebaseUser currentUser = currentUserResource.data;
                    if (currentUser != null) {
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
                                        userViewModel.updateUrlPicture(uid, downloadUri.toString()).observe(this, updateResource -> {
                                            if (updateResource != null && updateResource.status == Resource.Status.SUCCESS) {
                                                showSnackBar(getString(R.string.profile_image_changed));
                                            } else if (updateResource != null && updateResource.status == Resource.Status.ERROR) {
                                                Toast.makeText(AccountActivity.this, R.string.failed_to_update_image, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Log.e("UploadImage", "Failed to upload image", task.getException());
                                        Toast.makeText(AccountActivity.this, R.string.upload_failed, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            });
        } else {
            Toast.makeText(this, R.string.invalid_image_url, Toast.LENGTH_SHORT).show();
        }
    }

    private void showSnackBar(String message) {
        Snackbar.make(binding.accountContainer, message, Snackbar.LENGTH_SHORT).show();
    }
}
package com.dcac.labyrinth.ui.activities.fragments;

import static android.app.appsearch.AppSearchResult.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dcac.labyrinth.R;
import com.dcac.labyrinth.viewModels.UserManager;
import com.dcac.labyrinth.databinding.FragmentWelcomeBinding;
import com.dcac.labyrinth.ui.activities.AccountActivity;
import com.dcac.labyrinth.ui.activities.ParametersActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeFragment extends Fragment {

    private ActivityResultLauncher<Intent> accountActivityLauncher;
    private FragmentWelcomeBinding binding;
    //private static final int RC_SIGN_IN = 123;

    private UserManager userManager = UserManager.getInstance();

    private ActivityResultLauncher<Intent> signInOrUpLauncher;


    public static WelcomeFragment newInstance() {
        WelcomeFragment fragment = new WelcomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        signInOrUpLauncher = registerForActivityResult(
                //regarder plus precisement son fontionnement
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // TREATMENT AFTER A SUCCESSFUL CONNECTION
                        //userManager.createUser();

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {

                            userManager.createUser(user.getUid());
                            showSnackBar(getString(R.string.connection_succeed));
                            updateLoginStatus();
                            /*String uid = user.getUid();
                            userManager.getUserData(uid).addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    updateLoginStatus();
                                    showSnackBar(getString(R.string.connection_succeed));
                                } else {
                                    userManager.createUser(uid);
                                    updateLoginStatus();
                                    showSnackBar(getString(R.string.account_created_successfully));
                                }
                            });*/
                            // UPDATE TEXTBUTTON AND TEXTVIEW
                            /*binding.buttonLogin.setText(R.string.disconnect);
                            binding.buttonAccount.setEnabled(true);
                            binding.buttonAccount.setText(user.getEmail());
                            binding.buttonLogin.setEnabled(false);
                            int backgroundColor = getThemeColor(getContext(), androidx.appcompat.R.attr.colorPrimary);
                            binding.buttonAccount.setBackgroundColor(backgroundColor);*/
                           // binding.buttonAccount.setBackgroundColor(16767117);
                        }
                    } else {
                        IdpResponse response = IdpResponse.fromResultIntent(result.getData());
                        // ERROR TRAITMENT
                        if (response == null) {
                            showSnackBar(getString(R.string.error_authentication_canceled));
                        } else if (response.getError() != null) {
                            if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                                showSnackBar(getString(R.string.error_no_internet));
                            } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                                showSnackBar(getString(R.string.error_unknown_error));
                            }
                        }
                    }
                }
        );

        accountActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        binding.buttonLogin.setText(R.string.log_in);
                        binding.buttonAccount.setEnabled(false);
                        binding.buttonAccount.setText(R.string.not_connected);
                        binding.buttonLogin.setEnabled(true);
                        binding.buttonAccount.setBackgroundColor(0);
                        //binding.buttonAccount.setBackgroundColor();
                    }

                }
        );

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false);
        int logoResId = getThemeLogo(getContext());
        binding.filesExplorerImage.setImageResource(logoResId);
        return binding.getRoot();


    }

    public static int getThemeLogo(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.application_logo, typedValue, true);
        return typedValue.resourceId;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int backgroundColor = getThemeColor(getContext(), androidx.appcompat.R.attr.colorPrimary);
        int connectionColor = ContextCompat.getColor(getContext(), R.color.google_blue);
        binding.buttonGame.setEnabled(true);
        binding.buttonGame.setBackgroundColor(backgroundColor);
        binding.buttonScore.setEnabled(true);
        binding.buttonScore.setBackgroundColor(backgroundColor);
        binding.imageParameterButton.setEnabled(true);
        binding.buttonLogin.setBackgroundColor(connectionColor);
        binding.buttonLogin.setEnabled(true);
        binding.buttonAccount.setEnabled(false);
        binding.buttonAccount.setBackgroundColor(0);
        //binding.buttonLoginFacebook.setEnabled(true);

        updateLoginStatus();



        binding.buttonAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AccountActivity.class);
            accountActivityLauncher.launch(intent);
        });

        binding.imageParameterButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ParametersActivity.class);
            startActivity(intent);
        });

        binding.buttonGame.setOnClickListener(v -> {
            FragmentManager fragmentManager= getParentFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            GameFragment gameFragment= GameFragment.newInstance();
            fragmentTransaction.add(R.id.fragment_container, gameFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();


        });

        binding.buttonScore.setOnClickListener(v -> {
            FragmentManager fragmentManager= getParentFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            ScoreFragment scoreFragment= ScoreFragment.newInstance();
            fragmentTransaction.add(R.id.fragment_container, scoreFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        binding.buttonLogin.setOnClickListener(v -> {
            startMailAndGoogleInActivity();
            /*String[] options = {"E-mail", "Google"};
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getString(R.string.connection_method))
                    .setItems(options, (dialog, which) -> {
                        switch (which) {
                            case 0: // E-MAIL
                                startMailSignInOrUpActivity();
                                break;
                            case 1: // GOOGLE

                                startGoogleSignInActivity();
                                break;
                            *//*case 2: // FACEBOOK
                                startFacebookSignInActivity();
                                break;*//*
                        }
                    });
            builder.show();*/

        });

        /*binding.buttonLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFacebookSignInActivity();
            }
        });*/

    }

    public static int getThemeColor(Context context, int attributeColor) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attributeColor, typedValue, true);
        return typedValue.data;
    }

    private void startMailAndGoogleInActivity() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        Intent MailAndGoogleIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false,true)
                .setLogo(R.drawable.labyrinth_logo_black)
                .build();

        signInOrUpLauncher.launch(MailAndGoogleIntent);
    }

    /*private void startMailSignInOrUpActivity() {

        //CHOOSE AUTH PROVIDERS
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        // LAUNCH CONNECTION ACTIVITY
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.LoginTheme)
                .setLogo(R.drawable.account_icon_button)
                .build();

        signInOrUpLauncher.launch(signInIntent);
    }*/

    /*private void startGoogleSignInActivity() {
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        Intent googleSignInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false,true)
                .setLogo(R.drawable.account_icon_button)
                .build();

        signInOrUpLauncher.launch(googleSignInIntent);
    }*/

    /*private void startFacebookSignInActivity(){
        List<AuthUI.IdpConfig> providers = Collections.singletonList(new AuthUI.IdpConfig.FacebookBuilder().build());

        Intent facebookSignInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false,true)
                .setLogo(R.drawable.account_icon_button)
                .build();

        signInLauncher.launch(facebookSignInIntent);
    }*/

    private void updateLoginStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // USER CONNECTED
            binding.buttonLogin.setText(R.string.disconnect);
            binding.buttonAccount.setEnabled(true);
            binding.buttonAccount.setText(user.getEmail());
            binding.buttonLogin.setEnabled(false);
            int backgroundColor = getThemeColor(getContext(), androidx.appcompat.R.attr.colorPrimary);
            binding.buttonAccount.setBackgroundColor(backgroundColor);

            /*TypedValue value = new TypedValue();
            getContext().getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
            binding.buttonAccount.setBackgroundColor(value.data);*/
        } else {
            // USER NOT CONNECTED
            binding.buttonLogin.setText(R.string.log_in);
            binding.buttonAccount.setEnabled(false);
            binding.buttonAccount.setText(R.string.not_connected);
            binding.buttonLogin.setEnabled(true);
            binding.buttonAccount.setBackgroundColor(0);

        }
    }

    private void showSnackBar(String message) {
        Snackbar.make(binding.welcomeFragmentMainLayout, message, Snackbar.LENGTH_SHORT).show();
    }

}


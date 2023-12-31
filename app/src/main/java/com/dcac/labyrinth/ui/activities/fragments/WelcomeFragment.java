package com.dcac.labyrinth.ui.activities.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeFragment extends Fragment {

    private ActivityResultLauncher<Intent> accountActivityLauncher;
    private FragmentWelcomeBinding binding;

    private UserManager userManager = UserManager.getInstance();

    //private ActivityResultLauncher<Intent> signInOrUpLauncher;


    public static WelcomeFragment newInstance() {
        WelcomeFragment fragment = new WelcomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*signInOrUpLauncher = registerForActivityResult(
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
        );*/

        accountActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        binding.buttonLogin.setText(R.string.log_in);
                        binding.buttonAccount.setEnabled(false);
                        binding.buttonAccount.setText(R.string.not_connected);
                        binding.buttonLogin.setEnabled(true);
                        binding.buttonAccount.setBackgroundColor(0);
                    }

                }
        );

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false);
        //int logoResId = getThemeLogo(getContext());
        //binding.labyrinthImage.setImageResource(logoResId);
        return binding.getRoot();


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

        binding.buttonAccount.setEnabled(true);
        binding.buttonAccount.setBackgroundColor(0);

        updateLoginStatus();



        binding.buttonAccount.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                openAccountFragment();
            } else {
                loginShowSnackBar(getString(R.string.you_must_have_an_account_to_see_your_profile));
            }
        });

        binding.imageParameterButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ParametersActivity.class);
            startActivity(intent);
        });

        binding.buttonGame.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                openGameFragment();
            } else {
                loginShowSnackBar(getString(R.string.you_need_to_have_an_account_to_play));
            }

        });

        binding.buttonScore.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                openScoreFragment();
            } else {
                loginShowSnackBar(getString(R.string.you_need_to_have_an_account_to_see_scores));
            }
        });

        binding.buttonLogin.setOnClickListener(v -> {

            if (isUserLoggedIn()) {
                FirebaseAuth.getInstance().signOut();
                updateLoginStatus();
                loginShowSnackBar(getString(R.string.logged_out));
            } else {
                openSignInFragment();
            }

        });


    }

    @Override
    public void onResume() {
        super.onResume();
        updateLoginStatus();
    }



    public static int getThemeColor(Context context, int attributeColor) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attributeColor, typedValue, true);
        return typedValue.data;
    }

    /*public static int getThemeLogo(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.application_logo, typedValue, true);
        return typedValue.resourceId;
    }*/

    /*private void startMailAndGoogleInActivity() {


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
    }*/



    private void updateLoginStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // USER CONNECTED
            binding.buttonLogin.setText(R.string.log_off);
            binding.buttonLogin.setCompoundDrawablesWithIntrinsicBounds(R.drawable.account_deconnection_button, 0, 0, 0); // Remplacez ic_log_off par votre icône de déconnexion
            //binding.buttonAccount.setEnabled(true);
            binding.buttonAccount.setText(user.getEmail());
            //binding.buttonLogin.setEnabled(false);
            //binding.buttonGame.setEnabled(true);
            //binding.buttonScore.setEnabled(true);
            int backgroundColor = getThemeColor(getContext(), androidx.appcompat.R.attr.colorPrimary);
            binding.buttonAccount.setBackgroundColor(backgroundColor);

            /*TypedValue value = new TypedValue();
            getContext().getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
            binding.buttonAccount.setBackgroundColor(value.data);*/
        } else {
            // USER NOT CONNECTED
            binding.buttonLogin.setText(R.string.log_in);
            binding.buttonLogin.setCompoundDrawablesWithIntrinsicBounds(R.drawable.account_icon_button, 0, 0, 0); // Remplacez ic_log_off par votre icône de déconnexion
            //binding.buttonAccount.setEnabled(false);
            binding.buttonAccount.setText(R.string.not_connected);
            binding.buttonLogin.setEnabled(true);
            //binding.buttonScore.setEnabled(false);
            //binding.buttonGame.setEnabled(false);
            binding.buttonAccount.setBackgroundColor(0);

        }
    }

    private boolean isUserLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    private void openScoreFragment(){
        FragmentManager fragmentManager= getParentFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        ScoreFragment scoreFragment= ScoreFragment.newInstance();
        fragmentTransaction.add(R.id.fragment_container, scoreFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openGameFragment() {
        FragmentManager fragmentManager= getParentFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        GameFragment gameFragment= GameFragment.newInstance();
        fragmentTransaction.add(R.id.fragment_container, gameFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openAccountFragment() {
        Intent intent = new Intent(getActivity(), AccountActivity.class);
        accountActivityLauncher.launch(intent);
    }

    private void openSignInFragment() {
        FragmentManager fragmentManager= getParentFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        SignInFragment signInFragment= SignInFragment.newInstance();
        fragmentTransaction.add(R.id.fragment_container, signInFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void loginShowSnackBar(String message) {
        Snackbar.make(binding.welcomeFragmentMainLayout, message, Snackbar.LENGTH_SHORT).show();
    }

}


package com.dcac.labyrinth.ui.welcome;

import android.app.Activity;
import android.app.AlertDialog;
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
import com.dcac.labyrinth.databinding.FragmentWelcomeBinding;
import com.dcac.labyrinth.ui.account.AccountActivity;
import com.dcac.labyrinth.ui.game.GameFragment;
import com.dcac.labyrinth.ui.parameters.ParametersActivity;
import com.dcac.labyrinth.ui.score.ScoreFragment;
import com.facebook.FacebookSdk;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeFragment extends Fragment {

    private FragmentWelcomeBinding binding;
    private static final int RC_SIGN_IN = 123;

    private ActivityResultLauncher<Intent> signInLauncher;



    public static WelcomeFragment newInstance() {
        WelcomeFragment fragment = new WelcomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // TREATMENT AFTER A SUCCESSFUL CONNECTION
                        //userManager.createUser();

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            // Mettre à jour le texte du bouton et du TextView
                            binding.buttonLogin.setText(R.string.disconnect);
                            binding.buttonAccount.setEnabled(true);
                            binding.buttonAccount.setText(user.getEmail());

                        showSnackBar(getString(R.string.connection_succeed));
                        }
                    } else {
                        // Traitement des erreurs
                        IdpResponse response = IdpResponse.fromResultIntent(result.getData());
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

        binding.buttonAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AccountActivity.class);
                startActivity(intent);
            }
        });

        binding.imageParameterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ParametersActivity.class);
                startActivity(intent);
            }
        });

        binding.buttonGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager= getParentFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                GameFragment gameFragment= GameFragment.newInstance();
                fragmentTransaction.add(R.id.fragment_container, gameFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();


            }
        });

        binding.buttonScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager= getParentFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                ScoreFragment scoreFragment= ScoreFragment.newInstance();
                fragmentTransaction.add(R.id.fragment_container, scoreFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        binding.buttonLogin.setOnClickListener(v -> {
            String[] options = {"E-mail", "Google", "Facebook"};
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getString(R.string.connection_method))
                    .setItems(options, (dialog, which) -> {
                        switch (which) {
                            case 0: // E-MAIL
                                startMailSignInActivity();
                                break;
                            case 1: // GOOGLE

                                startGoogleSignInActivity();
                                break;
                            case 2: // FACEBOOK

                                startFacebookSignInActivity();
                                break;
                        }
                    });
            builder.show();

        });


    }

    public static int getThemeColor(Context context, int attributeColor) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attributeColor, typedValue, true);
        return typedValue.data;
    }

    private void startMailSignInActivity() {

        //CHOOSE AUTH PROVIDERS
        List<AuthUI.IdpConfig> providers = Collections.singletonList(new AuthUI.IdpConfig.EmailBuilder().build());

        // LAUNCH CONNECTION ACTIVITY
        Intent mailSignInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false,true)
                .setLogo(R.drawable.account_icon_button)
                .build();

        signInLauncher.launch(mailSignInIntent);
    }

    private void startGoogleSignInActivity() {
        List<AuthUI.IdpConfig> providers = Collections.singletonList(new AuthUI.IdpConfig.GoogleBuilder().build());

        Intent googleSignInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false,true)
                .setLogo(R.drawable.account_icon_button)
                .build();

        signInLauncher.launch(googleSignInIntent);
    }

    private void startFacebookSignInActivity(){
        List<AuthUI.IdpConfig> providers = Collections.singletonList(new AuthUI.IdpConfig.FacebookBuilder().build());

        Intent facebookSignInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false,true)
                .setLogo(R.drawable.account_icon_button)
                .build();

        signInLauncher.launch(facebookSignInIntent);
    }


    private void showSnackBar(String message) {
        Snackbar.make(binding.welcomeFragmentMainLayout, message, Snackbar.LENGTH_SHORT).show();
    }

}
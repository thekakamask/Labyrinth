package com.dcac.labyrinth.ui.activities.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dcac.labyrinth.R;
import com.dcac.labyrinth.databinding.FragmentSignInBinding;
import com.dcac.labyrinth.injection.UserViewModelFactory;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.dcac.labyrinth.viewModels.UserViewModel;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignInFragment extends Fragment {

    private FragmentSignInBinding binding;

    //private UserManager userManager = UserManager.getInstance();
    //JE PEUX PLUTOT PASSER L'INSTANCE DU USERMANAGER EN ARGUMENT DU FRAGMENT (LIGNE56)
    // ENLEVER LE STATIC DU FRAGMENT. FRAGMENT SET/FRAGMENT
    // IL FAUT JUSTE RECUPERER L'INSTANCE USERMANAGER EN LA PASSANT EN ARGUMENT
    // LORS DU LA CREATION DU FRAGMENT.
    private ActivityResultLauncher<Intent> signInOrUpLauncher;

    private FirebaseAuth mAuth;

    private UserViewModel userViewModel;

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();

        UserViewModelFactory factory = UserViewModelFactory.getInstance(requireContext().getApplicationContext());
        userViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);


        signInOrUpLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            userViewModel.createUser(user.getUid()).observe(getViewLifecycleOwner(), resource -> {
                                if (resource != null) {
                                    switch (resource.status) {
                                        case SUCCESS:
                                            if (resource.data != null && resource.data) {
                                                showSnackBar(getString(R.string.connection_succeed));
                                                showWelcomeFragment();
                                            }
                                            break;
                                        case ERROR:
                                            showSnackBar(getString(R.string.error_creating_user));
                                            break;
                                        case LOADING:
                                            // Optionnel: afficher un indicateur de chargement
                                            break;
                                    }
                                } else {
                                    // Gérer le cas où resource est null, si nécessaire
                                }
                            });
                        }
                    } else {
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
         binding = FragmentSignInBinding.inflate(inflater,container, false);
        //int logoResId = getThemeLogo(getContext());
        //int passwordLogoResId = getPasswordLogo(getContext());
        //binding.labyrinthImage.setImageResource(logoResId);
        //binding.mdpTextInputEdit.setImageResource(passwordLogoResId);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int backgroundColor = getThemeColor(getContext(), androidx.appcompat.R.attr.colorPrimary);

        binding.buttonSignIn.setEnabled(false);
        binding.buttonSignIn.setBackgroundColor(backgroundColor);

        binding.emailTextInputEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                enableSignInIfReady();
            }
        });
        binding.mdpTextInputEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                enableSignInIfReady();
            }
        });

        binding.buttonSignIn.setOnClickListener(v -> signInUser());

        binding.signUpLink.setOnClickListener(v -> startMailAndGoogleInActivity());

        TypedValue value = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.password_logo, value, true);
        int passwordEyeVisibleDrawableResId = value.resourceId;
        getContext().getTheme().resolveAttribute(R.attr.password_logo_close, value, true);
        int passwordEyeInvisibleDrawableResId = value.resourceId;

        binding.mdpTextInputEdit.setOnTouchListener((v, event) -> {
            final int RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (binding.mdpTextInputEdit.getRight() - binding.mdpTextInputEdit.getCompoundDrawables()[RIGHT].getBounds().width())) {
                    int selection = binding.mdpTextInputEdit.getSelectionEnd();
                    if (binding.mdpTextInputEdit.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        binding.mdpTextInputEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        binding.mdpTextInputEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, passwordEyeVisibleDrawableResId, 0);
                    } else {
                        binding.mdpTextInputEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        binding.mdpTextInputEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, passwordEyeInvisibleDrawableResId, 0);
                    }
                    binding.mdpTextInputEdit.setSelection(selection);
                    return true;
                }
            }
            return false;
        });
    }

    private void signInUser() {
        String email = binding.emailTextInputEdit.getText().toString();
        String password = binding.mdpTextInputEdit.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("SignIn", "signInWithEmail:success");
                        showWelcomeFragment();
                    } else {
                        Log.w("SignIn", "signInWithEmail:failure", task.getException());
                        Toast.makeText(getContext(), R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void showWelcomeFragment() {
        if (isAdded() && getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            WelcomeFragment welcomeFragment = WelcomeFragment.newInstance();
            fragmentTransaction.replace(R.id.fragment_container, welcomeFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    private void enableSignInIfReady() {
        String email = binding.emailTextInputEdit.getText().toString();
        String password = binding.mdpTextInputEdit.getText().toString();
        binding.buttonSignIn.setEnabled(!email.isEmpty() && !password.isEmpty());
    }

    private void showSnackBar(String message) {
        Snackbar.make(binding.welcomeFragmentMainLayout, message, Snackbar.LENGTH_SHORT).show();
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

    /*public static int getPasswordLogo(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.password_logo, typedValue, true);
        return typedValue.resourceId;
    }*/


}
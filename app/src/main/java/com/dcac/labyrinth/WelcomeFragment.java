package com.dcac.labyrinth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dcac.labyrinth.databinding.FragmentWelcomeBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeFragment extends Fragment {

    private FragmentWelcomeBinding binding;



    public static WelcomeFragment newInstance() {
        WelcomeFragment fragment = new WelcomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int backgroundColor = getThemeColor(getContext(), androidx.appcompat.R.attr.colorPrimary);

        binding.buttonWelcome.setEnabled(true);
        binding.buttonWelcome.setBackgroundColor(backgroundColor);
        binding.imageParameterButton.setEnabled(true);

        binding.buttonWelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CLICK SUR LE BOUTON POUR LANCER LE JEU.
            }
        });

        binding.imageParameterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ParametersActivity.class);
                startActivity(intent);
            }
        });

        binding.buttonWelcome.setOnClickListener(new View.OnClickListener() {
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
    }

    public static int getThemeColor(Context context, int attributeColor) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attributeColor, typedValue, true);
        return typedValue.data;
    }
}
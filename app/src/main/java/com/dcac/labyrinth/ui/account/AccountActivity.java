package com.dcac.labyrinth.ui.account;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.dcac.labyrinth.R;
import com.dcac.labyrinth.databinding.ActivityAccountBinding;
import com.dcac.labyrinth.databinding.ActivityParametersBinding;
import com.dcac.labyrinth.ui.BaseActivity;

public class AccountActivity extends BaseActivity<ActivityAccountBinding> {


    protected ActivityAccountBinding getViewBinding(){
        return ActivityAccountBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
package com.dcac.labyrinth.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.AuthUI;

public class AuthService {

    private final Context applicationContext;

    public AuthService(Context applicationContext) {
        this.applicationContext = applicationContext.getApplicationContext();
    }


    public LiveData<Void> signOut() {
        MutableLiveData<Void> liveData = new MutableLiveData<>();
        AuthUI.getInstance().signOut(applicationContext)
                .addOnCompleteListener(task -> liveData.setValue(null))
                .addOnFailureListener(e -> liveData.setValue(null));
        return liveData;
    }

    public LiveData<Void> deleteUser() {
        MutableLiveData<Void> liveData = new MutableLiveData<>();
        AuthUI.getInstance().delete(applicationContext)
                .addOnCompleteListener(task -> liveData.setValue(null))
                .addOnFailureListener(e -> liveData.setValue(null));
        return liveData;
    }
}

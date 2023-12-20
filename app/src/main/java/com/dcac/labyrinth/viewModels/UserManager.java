package com.dcac.labyrinth.viewModels;

import android.content.Context;

import com.dcac.labyrinth.data.repository.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class UserManager {

    private static volatile UserManager instance;
    private final UserRepository userRepository;

    private UserManager() {
        userRepository = UserRepository.getInstance();
    }

    public static UserManager getInstance() {
        UserManager result=instance;
        if(result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if(instance == null) {
                instance = new UserManager();
            }
            return instance;
        }
    }

    public FirebaseUser getCurrentUser(){
        return userRepository.getCurrentUser();
    }

    public Boolean isCurrentUserLogged(){
        return (this.getCurrentUser() != null);
    }

    public Task<Void> signOut(Context context) {
        return userRepository.signOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        return userRepository.deleteUser(context);
    }

    public void createUser(String uid) {
        userRepository.createUserInFirestore(uid);
    }

    public Task<DocumentSnapshot> getUserData(String uid) {
        userRepository.getUserData(uid);
        return userRepository.getUsersCollection().document(uid).get();
    }

    public CollectionReference getUsersCollection() {
        return userRepository.getUsersCollection();
    }

    public void updateScore(String uid, int score) {
        userRepository.updateScore(uid, score);
    }

    public void updateUserName(String uid, String userName) {
        userRepository.updateUserName(uid, userName);
    }






}

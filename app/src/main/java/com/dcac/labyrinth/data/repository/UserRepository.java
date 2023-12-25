package com.dcac.labyrinth.data.repository;

import android.content.Context;
import android.widget.Toast;

import com.dcac.labyrinth.R;
import com.dcac.labyrinth.data.models.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import javax.annotation.Nullable;

public final class UserRepository {

    private static final String COLLECTION_USERS = "users";

    private static Context context;
    private static volatile UserRepository instance;

    private UserRepository() {}

    public static UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
            return instance;
        }
    }

    public void setContext (Context context) {
        UserRepository.context = context;
    }

    public CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
    }

    //CREEZ UN USER DE FIREBASE AVEC SES PROPRIETES GENERIQUE MAIL ETC PUIS CREEZE EN UN NOUVEAU A PARTIR DE LUSER FIREBASE
    //POUR AJOUTER MES DONNEES. PUIS LOGIQUE DU JEU POUR AVOIR UN SCORE ET STOCKER LE SCORE

    public OnFailureListener onFailureListener() {
        return e-> Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show();
    }

    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /*@Nullable
    String getCurrentUserUID() {
        FirebaseUser user = getCurrentUser();
        return (user!= null) ? user.getUid(): null;
    }*/

    public Task<DocumentSnapshot> getUserData(String uid) {
        if (uid != null) {
            return getUsersCollection().document(uid).get();
        } else {
            return null;
        }
    }

    public void createUserInFirestore(String uid) {
        //FirebaseUser user = getCurrentUser();

        String urlPicture = (Objects.requireNonNull(getCurrentUser()).getPhotoUrl() != null) ? Objects.requireNonNull(getCurrentUser().getPhotoUrl()).toString() : null;
        String userName = getCurrentUser().getDisplayName();
        String email = getCurrentUser().getEmail();
        //String uid = getCurrentUser().getUid();

        getUserData(uid).addOnSuccessListener(documentSnapshot -> {
            User user =documentSnapshot.toObject(User.class);

            if (user != null) {
                createUser(uid, userName, urlPicture, email, user.getScore());
            } else {
                createUser(uid, userName, urlPicture, email, 0);
            }
        });

    }

    private Task<Void> createUser(String uid, String userName, String urlPicture, String email, int score) {
        //CREATE USER OBJECT
        User userToCreate = new User(uid, userName, urlPicture, email, score);
        //ADD A NEW USER DOCUMENT IN FIRESTORE
        return getUsersCollection()
                .document(uid) //SETTING UID FOR DOCUMENT
                .set(userToCreate);// SETTING OBJECT FOR DOCUMENT
    }

    public void updateScore(String uid, int score) {
        getUsersCollection().document(uid).update("score", score);
    }

    public void updateUserName(String uid, String userName) {
        getUsersCollection().document(uid).update("userName", userName);
    }



    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        return AuthUI.getInstance().delete(context);
    }
}

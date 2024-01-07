package com.dcac.labyrinth.viewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.dcac.labyrinth.data.repository.UserRepository;
import com.dcac.labyrinth.data.utils.Resource;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UserViewModel extends ViewModel {

    private final UserRepository userRepository;

    private final Map<LiveData<?>, Observer<Object>> observers = new HashMap<>();

    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Retirer les observateurs
        for (Map.Entry<LiveData<?>, Observer<Object>> entry : observers.entrySet()) {
            LiveData<?> liveData = entry.getKey();
            Observer<Object> observer = entry.getValue();
            liveData.removeObserver(observer);
        }
        observers.clear();
    }

    private <T> void observeForever(LiveData<T> liveData, Observer<T> observer) {
        Observer<Object> castedObserver = (Observer<Object>) observer;
        liveData.observeForever(castedObserver);
        observers.put(liveData, castedObserver);
    }


    public LiveData<Resource<Boolean>> isCurrentUserLogged() {
        MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        userRepository.getCurrentUserLiveData().observeForever(user -> {
            if (user != null) {
                liveData.setValue(Resource.success(true));
            } else {
                liveData.setValue(Resource.error("User not logged in", false));
            }
        });

        return liveData;
    }

    public LiveData<Resource<FirebaseUser>> getCurrentUser() {
        MutableLiveData<Resource<FirebaseUser>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        userRepository.getCurrentUserLiveData().observeForever(user -> {
            if (user != null) {
                liveData.setValue(Resource.success(user));
            } else {
                liveData.setValue(Resource.error("No current user", null));
            }
        });

        return liveData;
    }



    public LiveData<Resource<Void>> signOut() {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));  // Indique que la déconnexion est en cours

        LiveData<Resource<Void>> signOutResult = userRepository.signOut();
        signOutResult.observeForever(result -> {
            if (result != null && result.status == Resource.Status.SUCCESS) {
                liveData.setValue(Resource.success(null)); // Déconnexion réussie
            } else if (result != null && result.status == Resource.Status.ERROR) {
                liveData.setValue(Resource.error(result.message, null)); // Déconnexion échouée
            }
        });

        return liveData;
    }


    public LiveData<Resource<Void>> deleteUser() {
        // Obtient directement la LiveData<Resource<Void>> de UserRepository
        LiveData<Resource<Void>> deleteUserResult = userRepository.deleteUser();

        // Crée une nouvelle MutableLiveData pour envoyer les mises à jour
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        // S'abonne à la LiveData renvoyée par UserRepository
        deleteUserResult.observeForever(result -> {
            if (result != null && result.status == Resource.Status.SUCCESS) {
                liveData.setValue(Resource.success(null));
            } else if (result != null && result.status == Resource.Status.ERROR) {
                liveData.setValue(Resource.error(result.message, null));
            }
        });

        return liveData;
    }


    public LiveData<Resource<DocumentSnapshot>> getUserData(String uid) {
        MutableLiveData<Resource<DocumentSnapshot>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        Observer<DocumentSnapshot> observer = snapshot -> {
            if (snapshot != null) {
                liveData.setValue(Resource.success(snapshot));
            } else {
                liveData.setValue(Resource.error("Error fetching user data", null));
            }
        };
        observeForever(userRepository.getUserData(uid), observer);
        return liveData;
    }


    public LiveData<Resource<QuerySnapshot>> getAllUsers() {
        MutableLiveData<Resource<QuerySnapshot>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        Observer<QuerySnapshot> observer = querySnapshot -> {
            if (querySnapshot != null) {
                liveData.setValue(Resource.success(querySnapshot));
            } else {
                liveData.setValue(Resource.error("Error fetching all users", null));
            }
        };
        observeForever(userRepository.getAllUsers(), observer);
        return liveData;
    }

    public LiveData<Resource<Boolean>> createUser(String uid) {
        MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        Observer<Boolean> observer = isSuccess -> {
            if (isSuccess != null && isSuccess) {
                liveData.setValue(Resource.success(true));
            } else {
                liveData.setValue(Resource.error("Error creating user", false));
            }
        };
        observeForever(userRepository.createUser(uid), observer);
        return liveData;
    }

    public CollectionReference getUsersCollection() {
        return userRepository.getUsersCollection();
    }

    public LiveData<Resource<Boolean>> updateScore(String uid, int score) {
        MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        Observer<Boolean> observer = isSuccess -> {
            if (isSuccess != null && isSuccess) {
                liveData.setValue(Resource.success(true));
            } else {
                liveData.setValue(Resource.error("Error updating score", false));
            }
        };
        observeForever(userRepository.updateScore(uid, score), observer);
        return liveData;
    }


    public LiveData<Resource<Boolean>> updateUserName(String uid, String userName) {
        MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        Observer<Boolean> observer = isSuccess -> {
            if (isSuccess != null && isSuccess) {
                liveData.setValue(Resource.success(true));
            } else {
                liveData.setValue(Resource.error("Error updating user name", false));
            }
        };
        observeForever(userRepository.updateUserName(uid, userName), observer);
        return liveData;
    }

    public LiveData<Resource<Boolean>> updateUrlPicture(String uid, String urlPicture) {
        MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        Observer<Boolean> observer = isSuccess -> {
            if (isSuccess != null && isSuccess) {
                liveData.setValue(Resource.success(true));
            } else {
                liveData.setValue(Resource.error("Error updating URL picture", false));
            }
        };
        observeForever(userRepository.updateUrlPicture(uid, urlPicture), observer);
        return liveData;
    }






}

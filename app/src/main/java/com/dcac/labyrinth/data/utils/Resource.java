package com.dcac.labyrinth.data.utils;

import androidx.lifecycle.MutableLiveData;

public class Resource<T>{
    public enum Status {
        SUCCESS, ERROR, LOADING
    }

    public final Status status;

    public final T data;
    public final String message;

    private Resource(Status status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    private <T> MutableLiveData<Resource<T>> createResourceLiveData() {
        return new MutableLiveData<>();
    }

    private <T> void updateResourceLiveData(MutableLiveData<Resource<T>> liveData, Status status, T data, String message) {
        switch (status) {
            case SUCCESS:
                liveData.setValue(Resource.success(data));
                break;
            case ERROR:
                liveData.setValue(Resource.error(message, data));
                break;
            case LOADING:
                liveData.setValue(Resource.loading(data));
                break;
        }

    }

    public static <T> Resource<T> success(T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(String msg, T data) {
        return new Resource<>(Status.ERROR, data, msg);
    }

    public static <T> Resource<T> loading(T data) {
        return new Resource<>(Status.LOADING, data, null);
    }

}

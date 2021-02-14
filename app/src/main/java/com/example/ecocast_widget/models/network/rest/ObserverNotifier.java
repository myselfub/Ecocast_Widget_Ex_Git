package com.example.ecocast_widget.models.network.rest;


import java.util.Observable;

public class ObserverNotifier extends Observable {

    private ObserverNotifier() {
    }

    private static class Singleton {
        private static final ObserverNotifier instance = new ObserverNotifier();
    }

    public static ObserverNotifier getInstance() {

        return Singleton.instance;
    }

    @Override
    public void notifyObservers() {
        setChanged(); //
        super.notifyObservers();
    }

    @Override
    public void notifyObservers(Object data) {
        setChanged(); //
        super.notifyObservers(data);
    }
}
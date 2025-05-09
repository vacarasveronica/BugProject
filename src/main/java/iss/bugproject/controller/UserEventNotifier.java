package iss.bugproject.controller;


import java.util.ArrayList;
import java.util.List;

public class UserEventNotifier {
    private static final UserEventNotifier instance = new UserEventNotifier();

    private final List<UserObserver> observers = new ArrayList<>();

    private UserEventNotifier() {}

    public static UserEventNotifier getInstance() {
        return instance;
    }

    public void addObserver(UserObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(UserObserver observer) {
        observers.remove(observer);
    }

    public void notifyUserAdded() {
        for (UserObserver o : observers) {
            o.onUserAdded();
        }
    }
}


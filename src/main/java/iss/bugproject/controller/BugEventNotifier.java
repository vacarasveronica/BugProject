package iss.bugproject.controller;


import java.util.ArrayList;
import java.util.List;

public class BugEventNotifier {
    private static final BugEventNotifier instance = new BugEventNotifier();
    private final List<BugObserver> observers = new ArrayList<>();

    private BugEventNotifier() {}

    public static BugEventNotifier getInstance() {
        return instance;
    }

    public void addObserver(BugObserver obs) {
        if (!observers.contains(obs)) observers.add(obs);
    }

    public void notifyBugUpdated() {
        for (BugObserver obs : observers) {
            obs.onBugUpdated();
        }
    }
}


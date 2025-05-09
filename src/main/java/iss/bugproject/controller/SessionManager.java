package iss.bugproject.controller;


import iss.bugproject.domain.User;

import java.util.HashSet;
import java.util.Set;

public class SessionManager {
    private static final Set<User> loggedInUsers = new HashSet<>();

    public static void login(User user) {
        loggedInUsers.add(user);
    }

    public static void logout(User user) {
        loggedInUsers.remove(user);
    }

    public static boolean isLoggedIn(User user) {
        return loggedInUsers.contains(user);
    }

    public static Set<User> getLoggedInUsers() {
        return new HashSet<>(loggedInUsers); // returnăm o copie pentru siguranță
    }

    public static void logoutAll() {
        loggedInUsers.clear();
    }
}


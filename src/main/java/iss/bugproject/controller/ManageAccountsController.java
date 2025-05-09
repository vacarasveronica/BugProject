package iss.bugproject.controller;

import iss.bugproject.domain.Role;
import iss.bugproject.domain.User;
import iss.bugproject.service.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManageAccountsController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ChoiceBox<Role> roleChoiceBox;

    @FXML
    private Label errorLabel;

    private UserService userService;




    public void setUserService(UserService service) {
        this.userService = service;
        roleChoiceBox.setItems(FXCollections.observableArrayList(Role.ADMINISTRATOR, Role.TESTER, Role.PROGRAMMER));
        roleChoiceBox.setValue(Role.PROGRAMMER); // default
    }

    @FXML
    private void handleAddUser() throws SQLException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        Role selectedRole = roleChoiceBox.getValue();

        StringBuilder errors = new StringBuilder();

        if (username.isEmpty()) {
            errors.append("• Username cannot be empty!\n");
        }

        if (password.length() < 5) {
            errors.append("• Password must be at least 5 characters!\n");
        }

        try {
            for (User u : userService.getAllUsers()) {
                if (u.getUsername().equalsIgnoreCase(username)) {
                    errors.append("• Username already exists!\n");
                    break;
                }
            }
        } catch (SQLException e) {
            errors.append("• Database error while checking users.\n");
        }

        if (errors.length() > 0) {
            errorLabel.setText(errors.toString());
            return; // nu continuăm dacă există erori
        }

        // Confirmare
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm User Creation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to add this user?");
        ButtonType result = confirmation.showAndWait().orElse(ButtonType.CANCEL);

        if (result == ButtonType.OK) {
            User newUser = new User(username, password, selectedRole);
            userService.addUser(newUser); // parola e criptată în repo
            UserEventNotifier.getInstance().notifyUserAdded();
            showSuccess("User added successfully!");
        }

        // Resetare câmpuri indiferent dacă adăugarea a fost confirmată sau nu
        usernameField.setText("");
        passwordField.setText("");
        roleChoiceBox.setValue(Role.PROGRAMMER);
        errorLabel.setText("");
    }


    private void showSuccess(String message) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText(null);
        info.setContentText(message);
        info.showAndWait();
    }
}

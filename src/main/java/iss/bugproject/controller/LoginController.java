package iss.bugproject.controller;


import iss.bugproject.domain.Role;
import iss.bugproject.domain.User;
import iss.bugproject.service.BugService;
import iss.bugproject.service.FileService;
import iss.bugproject.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoginController {
    private UserService srvU;
    private BugService srvB;
    private FileService srvF;

    public void setData(UserService srvU, BugService srvB, FileService srvF) {
        this.srvU = srvU;
        this.srvB = srvB;
        this.srvF = srvF;
    }

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() throws SQLException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty()) {
            showAlert("Error", "Username cannot be empty!");
            return;
        }

        // TODO: Aici poți verifica dacă username-ul există în baza de date
        Iterable<User> iterableItems = srvU.getAllUsers();
        List<User> persoanaItemsList = new ArrayList<>();
        for (User item : iterableItems) {
            persoanaItemsList.add(item);
        }
        int existsuser = 0;
        for (User u : persoanaItemsList)
                if(u.getUsername().equals(username))
                    existsuser = 1;
        if(existsuser == 0){
            showAlert("Error", "This user is not in the database!");
            usernameField.setText("");
            passwordField.setText("");
        }

        for (User u : persoanaItemsList) {
            if (u.getUsername().equals(username)) {
                if (BCrypt.checkpw(password, u.getPassword())) {
                    if (SessionManager.isLoggedIn(u)) {
                        showAlert("Already logged in", "This user is already logged in!");
                        usernameField.setText("");
                        passwordField.setText("");
                        return;
                    }
                    SessionManager.login(u);
                    if(u.getRole().equals(Role.ADMINISTRATOR)) {
                        openAdminWindow(u);
                        usernameField.setText("");
                        passwordField.setText("");
                    }
                    else{
                        openEmployeeWindow(u);
                        usernameField.setText("");
                        passwordField.setText("");
                    }
                }
                else {
                    showAlert("Error", "Wrong password!");
                    passwordField.setText("");
                }
            }
        }


    }



    private void openAdminWindow(User admin) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load(), 400, 300));
            stage.setTitle(admin.getUsername());

            AdminController controller = loader.getController();
            controller.setData(admin,srvU);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openEmployeeWindow(User employee) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EmployeeView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load(), 400, 300));
            stage.setTitle(employee.getUsername());

            EmployeeController controller = loader.getController();
            controller.setData(employee,srvU,srvB,srvF);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

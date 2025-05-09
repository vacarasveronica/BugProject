package iss.bugproject.controller;

import iss.bugproject.domain.Role;
import iss.bugproject.domain.User;
import iss.bugproject.service.UserService;
import iss.bugproject.controller.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.util.ArrayList;

import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.List;

public class AdminController implements UserObserver {

    private User currentAdmin;
    private UserService userService;

    @FXML
    private ListView<String> employeeList;

    @FXML
    private Button manageAccountsButton;

    @FXML
    private Button logoutButton;

    public void setData(User admin, UserService userService) {
        this.currentAdmin = admin;
        this.userService = userService;
        UserEventNotifier.getInstance().addObserver(this);
        loadEmployees();
    }

    private void loadEmployees() {
        try {
            Iterable<User> allUsers = userService.getAllUsers();

            List<String> employeeNames = new ArrayList<>();
            for (User u : allUsers) {
                if (u.getRole() == Role.PROGRAMMER || u.getRole() == Role.TESTER) {
                    employeeNames.add(u.getUsername());
                }
            }

            employeeList.getItems().setAll(employeeNames);

        } catch (SQLException e) {
            showAlert("Error loading employees: " + e.getMessage());
        }
    }

    @Override
    public void onUserAdded() {
        loadEmployees(); // 🔁 actualizare automată
    }


    @FXML
    private void handleManageAccounts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ManageAccountsView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Manage Accounts");
            stage.setScene(new Scene(loader.load()));

            ManageAccountsController controller = loader.getController();
            controller.setUserService(userService);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleLogout() {
        SessionManager.logout(currentAdmin);
        ((Stage) logoutButton.getScene().getWindow()).close(); // Închide fereastra
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


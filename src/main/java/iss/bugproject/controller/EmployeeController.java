package iss.bugproject.controller;

import iss.bugproject.domain.Role;
import iss.bugproject.domain.User;
import iss.bugproject.service.BugService;
import iss.bugproject.service.FileService;
import iss.bugproject.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;


public class EmployeeController {

    private User currentEmployee;
    private UserService userService;
    private BugService bugService;
    private FileService fileService;

    @FXML
    private Button logoutButton;

    public void setData(User user, UserService userService, BugService bugService, FileService fileService) {
        this.currentEmployee = user;
        this.userService = userService;
        this.bugService = bugService;
        this.fileService = fileService;
    }

    @FXML
    private void handleSubmitBugs() {
        if (currentEmployee.getRole() != Role.TESTER) {
            showAlert("Only testers can submit bugs.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SubmitBugView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load(), 450, 400));

            SubmitBugController controller = loader.getController();
            controller.setData(currentEmployee, bugService, fileService);

            stage.setTitle("Submit Bug");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewBugs() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewBugListView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load(), 800, 500));

            ViewBugListController controller = loader.getController();
            controller.setData(currentEmployee, bugService, fileService);

            stage.setTitle("View Bugs");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.logout(currentEmployee);
        ((Stage) logoutButton.getScene().getWindow()).close(); // Închide fereastra
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

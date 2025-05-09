package iss.bugproject.controller;


import iss.bugproject.domain.Bug;
import iss.bugproject.domain.File;
import iss.bugproject.domain.Role;
import iss.bugproject.domain.User;
import iss.bugproject.service.BugService;
import iss.bugproject.service.FileService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class SubmitBugController {

    private User currentUser;
    private BugService bugService;
    private FileService fileService;

    private java.io.File selectedFile;

    @FXML
    private TextField bugNameField;

    @FXML
    private TextArea bugDescriptionField;

    @FXML
    private TextField fileDescriptionField;

    @FXML
    private Label filePathLabel;

    @FXML
    private Label errorLabel;

    public void setData(User user, BugService bugService, FileService fileService) {
        this.currentUser = user;
        this.bugService = bugService;
        this.fileService = fileService;

        if (currentUser.getRole() != Role.TESTER) {
            showAlert("Access Denied", "Only testers can submit bugs.");
            closeWindow();
        }
    }

    @FXML
    private void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            filePathLabel.setText(selectedFile.getAbsolutePath());
        } else {
            filePathLabel.setText("No file selected");
        }
    }

    @FXML
    private void handleSubmit() {
        String title = bugNameField.getText().trim();
        String desc = bugDescriptionField.getText().trim();
        String fileDesc = fileDescriptionField.getText().trim();

        StringBuilder errors = new StringBuilder();
        if (title.isEmpty()) errors.append("• Title is required\n");
        if (desc.isEmpty()) errors.append("• Description is required\n");

        if (selectedFile != null && fileDesc.isEmpty()) {
            errors.append("• Description for selected file is required\n");
        }

        if (errors.length() > 0) {
            errorLabel.setText(errors.toString());
            return;
        }

        // Confirmare
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Submit");
        confirm.setContentText("Are you sure you want to submit this bug?");
        ButtonType result = confirm.showAndWait().orElse(ButtonType.CANCEL);

        if (result == ButtonType.OK) {
            try {
                // 1. Adaugă bugul
                Bug bug = new Bug(title, desc, "UNASSIGNED", null);
                Optional<Bug> bugInserted = bugService.addBug(bug); // salvați bugul (va primi ID)

                // 2. Adaugă fișierul dacă e selectat
                if (selectedFile != null) {
                    File file = new File(selectedFile.getAbsolutePath(), fileDesc, bugInserted.get().getId());
                    fileService.addFile(file);
                }

                BugEventNotifier.getInstance().notifyBugUpdated();
                showAlert("Success", "Bug submitted successfully!");
                clearFields();

            } catch (Exception e) {
                showAlert("Error", "Failed to submit bug: " + e.getMessage());
            }
        } else {
            clearFields(); // dacă userul a renunțat
        }
    }

    private void clearFields() {
        bugNameField.setText("");
        bugDescriptionField.setText("");
        fileDescriptionField.setText("");
        filePathLabel.setText("No file selected");
        selectedFile = null;
        errorLabel.setText("");
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) bugNameField.getScene().getWindow();
        stage.close();
    }
}

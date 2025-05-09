package iss.bugproject.controller;


import iss.bugproject.domain.Bug;
import iss.bugproject.domain.File;
import iss.bugproject.domain.Role;
import iss.bugproject.domain.User;
import iss.bugproject.service.BugService;
import iss.bugproject.service.FileService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.*;

public class ViewBugListController implements BugObserver {

    @FXML
    private TableView<BugRow> bugTable;

    @FXML
    private TableColumn<BugRow, String> titleCol, descCol, statusCol, filePathCol, fileDescCol;
    @FXML
    private TableColumn<BugRow, Button> assignCol, solveCol;

    private BugService bugService;
    private FileService fileService;
    private User currentUser;

    private final ObservableList<BugRow> bugRows = FXCollections.observableArrayList();

    public void setData(User user, BugService bugService, FileService fileService) {
        this.currentUser = user;
        this.bugService = bugService;
        this.fileService = fileService;
        BugEventNotifier.getInstance().addObserver(this);
        loadTable();
    }

    public void loadTable() {
        bugRows.clear();
        try {
            List<Bug> bugs = new ArrayList<>();
            for (Bug bug : bugService.getAllBugs()) {
                bugs.add(bug);
            }
            for (Bug b : bugs) {
                Optional<File> file = fileService.findFileByBugId(b.getId());
                String path = file.map(File::getPath).orElse("");
                String fileDesc = file.map(File::getDescriere).orElse("");

                bugRows.add(new BugRow(b, path, fileDesc));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        bugTable.setItems(bugRows);
        initColumns();
    }

    private void initColumns() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        filePathCol.setCellValueFactory(new PropertyValueFactory<>("filePath"));
        fileDescCol.setCellValueFactory(new PropertyValueFactory<>("fileDescription"));

        assignCol.setCellValueFactory(new PropertyValueFactory<>("assignButton"));
        solveCol.setCellValueFactory(new PropertyValueFactory<>("solveButton"));
    }

    @Override
    public void onBugUpdated() {
        loadTable(); // se reîncarcă automat când alt user modifică
    }

    public class BugRow {
        private final Bug bug;
        private final String title, description, status, filePath, fileDescription;
        private final Button assignButton = new Button("Assign");
        private final Button solveButton = new Button("Solved");

        public BugRow(Bug bug, String path, String desc) {
            this.bug = bug;
            this.title = bug.getDenumire();
            this.description = bug.getDescriere();
            this.status = bug.getStatus();
            this.filePath = path;
            this.fileDescription = desc;

            assignButton.setOnAction(e -> handleAssign(currentUser));
            solveButton.setOnAction(e -> handleSolve(currentUser));

            if (currentUser.getRole() == Role.TESTER) {
                assignButton.setDisable(true);
                solveButton.setDisable(true);
            }
        }

        private void handleAssign(User user) {
            if (!bug.getStatus().equalsIgnoreCase("UNASSIGNED")) {
                showAlert("This bug is already assigned to someone else.");
                return;
            }

            try {
                Bug updated = new Bug(bug.getDenumire(), bug.getDescriere(), "IN PROCESS", user.getId());
                updated.setId(bug.getId());

                bugService.updateBug(updated);
                BugEventNotifier.getInstance().notifyBugUpdated();
            } catch (Exception e) {
                showAlert("Error assigning bug: " + e.getMessage());
            }
        }

        private void handleSolve(User user) {
            if (!bug.getStatus().equalsIgnoreCase("IN PROCESS")) {
                showAlert("Only bugs that are in process can be marked as solved.");
                return;
            }

            if (bug.getIdProgramator() == null || !bug.getIdProgramator().equals(user.getId())) {
                showAlert("You can only solve bugs assigned to you.");
                return;
            }

            try {
                Bug updated = new Bug(bug.getDenumire(), bug.getDescriere(), "SOLVED", user.getId());
                updated.setId(bug.getId());

                bugService.updateBug(updated);
                BugEventNotifier.getInstance().notifyBugUpdated();
            } catch (Exception e) {
                showAlert("Error solving bug: " + e.getMessage());
            }
        }

        private void showAlert(String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Action not allowed");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }



        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getStatus() { return status; }
        public String getFilePath() { return filePath; }
        public String getFileDescription() { return fileDescription; }
        public Button getAssignButton() { return assignButton; }
        public Button getSolveButton() { return solveButton; }
    }
}


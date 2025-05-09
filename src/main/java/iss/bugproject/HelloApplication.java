package iss.bugproject;

import iss.bugproject.controller.LoginController;
import iss.bugproject.domain.Role;
import iss.bugproject.domain.User;
import iss.bugproject.repository.*;
import iss.bugproject.service.BugService;
import iss.bugproject.service.FileService;
import iss.bugproject.service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class HelloApplication extends Application {
    String url;
    String username;
    String password;

    private void loadConfig() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find db.config.properties");
                return;
            }
            prop.load(input);
            url = prop.getProperty("db.url");
            username = prop.getProperty("db.username");
            password = prop.getProperty("db.password");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        loadConfig();

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to connect to the PostgreSQL server.");
            e.printStackTrace();
        }
        UserInterface repoUser = new UserDbRepo(url, username, password);
        UserService userService = new UserService(repoUser);


        BugInterfae repoBug = new BugDbRepo(url, username, password);
        BugService bugService = new BugService(repoBug);

        FileInterface repoFile = new FileDbRepo(url, username, password);
        FileService fileservice = new FileService(repoFile);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
        Parent root1 = loader.load();
        LoginController loginController = loader.getController();
        loginController.setData(userService,bugService,fileservice);
        stage.setScene(new Scene(root1, 300, 200));
        stage.setTitle("Login");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

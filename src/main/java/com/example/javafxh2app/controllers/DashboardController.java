package com.example.javafxh2app.controllers;

import com.example.javafxh2app.db.DB;
import com.example.javafxh2app.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private ListView<String> upcomingList;
    @FXML private Button openTasksBtn;
    @FXML private Button logoutBtn;

    private User user;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void setUser(User user) {
        this.user = user;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Добро пожаловать, " + user.getUsername() + "!");
        }
        loadUpcoming();
    }

    @FXML
    private void initialize() {
        openTasksBtn.setOnAction(e -> {
            try {
                openTasksWindow();
                ((Stage) openTasksBtn.getScene().getWindow()).close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        logoutBtn.setOnAction(e -> logoutBtn.getScene().getWindow().hide());
    }

    private void loadUpcoming() {
        if (user == null) return; // защита
        upcomingList.getItems().clear();
        try (Connection c = DB.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "SELECT id, text, deadline, priority FROM tasks WHERE user_id = ? AND status = FALSE AND deadline IS NOT NULL ORDER BY deadline ASC LIMIT 3"
            );
            ps.setLong(1, user.getId());
            ResultSet rs = ps.executeQuery();
            List<String> out = new ArrayList<>();
            while (rs.next()) {
                String text = rs.getString("text");
                java.sql.Timestamp ts = rs.getTimestamp("deadline");
                String priority = rs.getString("priority");
                String line = (ts == null ? "без дедлайна" : fmt.format(ts.toLocalDateTime()))
                        + " — " + text + " (" + priority + ")";
                out.add(line);
            }
            upcomingList.getItems().addAll(out);
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openTasksWindow() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tasks.fxml"));
        Parent root = loader.load();
        TasksController ctrl = loader.getController();
        ctrl.setUser(user);

        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm()
        );

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Tasks — " + user.getUsername());
        stage.show();
    }
}

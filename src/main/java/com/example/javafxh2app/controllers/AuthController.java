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
import java.util.Objects;

public class AuthController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginBtn;
    @FXML private Button registerBtn;
    @FXML private Label infoLabel;

    @FXML
    private void initialize() {
        loginBtn.setOnAction(e -> login());
        registerBtn.setOnAction(e -> register());
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            infoLabel.setText("Введите логин и пароль.");
            return;
        }

        try (Connection c = DB.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "SELECT id, password_hash FROM users WHERE username = ?"
            );
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                long id = rs.getLong("id");
                String storedHash = rs.getString("password_hash");

                if (storedHash.equals(DB.hash(password))) {
                    User user = new User(id, username);
                    openDashboard(user); // ← теперь работает корректно
                } else {
                    infoLabel.setText("Неверный пароль.");
                }
            } else {
                infoLabel.setText("Пользователь не найден.");
            }

            rs.close();
            ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            infoLabel.setText("Ошибка при входе.");
        }
    }

    private void register() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            infoLabel.setText("Введите логин и пароль.");
            return;
        }

        try (Connection c = DB.getConnection()) {
            PreparedStatement check = c.prepareStatement(
                    "SELECT COUNT(*) FROM users WHERE username = ?"
            );
            check.setString(1, username);
            ResultSet rs = check.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                infoLabel.setText("Логин занят.");
                rs.close();
                check.close();
                return;
            }
            rs.close();
            check.close();

            PreparedStatement ins = c.prepareStatement(
                    "INSERT INTO users (username, password_hash) VALUES (?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            ins.setString(1, username);
            ins.setString(2, DB.hash(password));
            ins.executeUpdate();

            ResultSet keys = ins.getGeneratedKeys();
            if (keys.next()) {
                long id = keys.getLong(1);
                User user = new User(id, username);
                openDashboard(user); // ← сразу открываем Dashboard
            }

            keys.close();
            ins.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            infoLabel.setText("Ошибка регистрации.");
        }
    }

    // ======================== Исправленный openDashboard ========================
    private void openDashboard(User user) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
        Parent root = loader.load(); // сначала загружаем FXML

        // Получаем контроллер после load
        DashboardController ctrl = loader.getController();
        ctrl.setUser(user); // передаем пользователя до создания сцены

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm()
        );

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Dashboard — " + user.getUsername());
        stage.show();

        // Закрываем окно логина
        ((Stage) loginBtn.getScene().getWindow()).close();
    }
}

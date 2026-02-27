package com.example.javafxh2app.controllers;

import com.example.javafxh2app.db.DB;
import com.example.javafxh2app.models.Task;
import com.example.javafxh2app.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class TasksController {

    @FXML private ListView<Task> listView;
    @FXML private TextField textField;
    @FXML private ComboBox<String> priorityBox;
    @FXML private DatePicker deadlineDate;
    @FXML private TextField deadlineTime; // HH:mm
    @FXML private Button saveBtn;
    @FXML private Button addBtn;
    @FXML private Button markDoneBtn;
    @FXML private Button deleteBtn;
    @FXML private Button backBtn; // ← новая кнопка
    @FXML private Label infoLabel;

    private User user;
    private ObservableList<Task> tasks = FXCollections.observableArrayList();
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

    public void setUser(User user) {
        this.user = user;
        loadTasks();
    }

    @FXML
    private void initialize() {
        priorityBox.getItems().addAll("Low", "Medium", "High");
        priorityBox.setValue("Medium");

        listView.setItems(tasks);
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Task item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText((item.isStatus() ? "[✓] " : "[ ] ") + item.getText());
            }
        });

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) populateDetails(newV);
            else clearDetails();
        });

        addBtn.setOnAction(e -> addNew());
        saveBtn.setOnAction(e -> saveChanges());
        markDoneBtn.setOnAction(e -> markDone());
        deleteBtn.setOnAction(e -> deleteTask());
        backBtn.setOnAction(e -> goBackToDashboard()); // ← обработчик кнопки
    }

    private void goBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());

            stage.setScene(scene);

            // Передаём текущего пользователя в DashboardController
            DashboardController ctrl = loader.getController();
            ctrl.setUser(user);

            stage.setTitle("Dashboard — " + user.getUsername());
            stage.show();

            // Закрываем текущее окно задач
            backBtn.getScene().getWindow().hide();

        } catch (Exception e) {
            e.printStackTrace();
            infoLabel.setText("Ошибка при возврате на Dashboard.");
        }
    }

    // ======================= Существующие методы =======================

    private void loadTasks() {
        tasks.clear();
        try (Connection c = DB.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "SELECT id, text, status, priority, deadline FROM tasks WHERE user_id = ? ORDER BY created_at DESC"
            );
            ps.setLong(1, user.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                long id = rs.getLong("id");
                String text = rs.getString("text");
                boolean status = rs.getBoolean("status");
                String priority = rs.getString("priority");
                Timestamp ts = rs.getTimestamp("deadline");
                LocalDateTime deadline = ts == null ? null : ts.toLocalDateTime();
                tasks.add(new Task(id, user.getId(), text, status, priority, deadline));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateDetails(Task t) {
        textField.setText(t.getText());
        priorityBox.setValue(t.getPriority() == null ? "Medium" : t.getPriority());
        if (t.getDeadline() != null) {
            deadlineDate.setValue(t.getDeadline().toLocalDate());
            deadlineTime.setText(t.getDeadline().toLocalTime().format(timeFmt));
        } else {
            deadlineDate.setValue(null);
            deadlineTime.setText("");
        }
        infoLabel.setText("Редактирование задачи id=" + t.getId());
    }

    private void clearDetails() {
        textField.clear();
        priorityBox.setValue("Medium");
        deadlineDate.setValue(null);
        deadlineTime.setText("");
        infoLabel.setText("");
    }

    private void addNew() {
        String text = textField.getText().trim();
        if (text.isEmpty()) { infoLabel.setText("Введите текст задачи."); return; }
        String priority = priorityBox.getValue();
        LocalDateTime deadline = parseDeadlineFromFields();
        if (deadlineDate.getValue() != null && deadline == null) return;
        try (Connection c = DB.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO tasks (user_id, text, status, priority, deadline) VALUES (?, ?, FALSE, ?, ?)"
            );
            ps.setLong(1, user.getId());
            ps.setString(2, text);
            ps.setString(3, priority);
            ps.setTimestamp(4, deadline != null ? Timestamp.valueOf(deadline) : null);
            ps.executeUpdate();
            ps.close();
            loadTasks();
            infoLabel.setText("Задача добавлена.");
        } catch (Exception e) {
            e.printStackTrace();
            infoLabel.setText("Ошибка при добавлении.");
        }
    }

    private void saveChanges() {
        Task sel = listView.getSelectionModel().getSelectedItem();
        if (sel == null) { infoLabel.setText("Выберите задачу."); return; }
        String text = textField.getText().trim();
        if (text.isEmpty()) { infoLabel.setText("Текст не может быть пустым."); return; }
        String priority = priorityBox.getValue();
        LocalDateTime deadline = parseDeadlineFromFields();
        if (deadlineDate.getValue() != null && deadline == null) return;
        try (Connection c = DB.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "UPDATE tasks SET text = ?, priority = ?, deadline = ? WHERE id = ? AND user_id = ?"
            );
            ps.setString(1, text);
            ps.setString(2, priority);
            ps.setTimestamp(3, deadline != null ? Timestamp.valueOf(deadline) : null);
            ps.setLong(4, sel.getId());
            ps.setLong(5, user.getId());
            ps.executeUpdate();
            ps.close();
            loadTasks();
            infoLabel.setText("Изменения сохранены.");
        } catch (Exception e) {
            e.printStackTrace();
            infoLabel.setText("Ошибка при сохранении.");
        }
    }

    private void markDone() {
        Task sel = listView.getSelectionModel().getSelectedItem();
        if (sel == null) { infoLabel.setText("Выберите задачу."); return; }
        try (Connection c = DB.getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "UPDATE tasks SET status = TRUE WHERE id = ? AND user_id = ?"
            );
            ps.setLong(1, sel.getId());
            ps.setLong(2, user.getId());
            ps.executeUpdate();
            ps.close();
            loadTasks();
            infoLabel.setText("Отмечено выполненной.");
        } catch (Exception e) {
            e.printStackTrace();
            infoLabel.setText("Ошибка при обновлении статуса.");
        }
    }

    private void deleteTask() {
        Task sel = listView.getSelectionModel().getSelectedItem();
        if (sel == null) { infoLabel.setText("Выберите задачу."); return; }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Удалить задачу?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try (Connection c = DB.getConnection()) {
                    PreparedStatement ps = c.prepareStatement(
                            "DELETE FROM tasks WHERE id = ? AND user_id = ?"
                    );
                    ps.setLong(1, sel.getId());
                    ps.setLong(2, user.getId());
                    ps.executeUpdate();
                    ps.close();
                    loadTasks();
                    infoLabel.setText("Удалено.");
                } catch (Exception e) {
                    e.printStackTrace();
                    infoLabel.setText("Ошибка при удалении.");
                }
            }
        });
    }

    private LocalDateTime parseDeadlineFromFields() {
        if (deadlineDate.getValue() == null) return null;
        String t = deadlineTime.getText().trim();
        if (t.isEmpty()) t = "23:59";
        try {
            return LocalDateTime.of(deadlineDate.getValue(), java.time.LocalTime.parse(t, timeFmt));
        } catch (Exception e) {
            infoLabel.setText("Неверный формат времени (ожидается HH:mm).");
            return null;
        }
    }
}

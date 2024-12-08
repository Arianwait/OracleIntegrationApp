package kz.aws.oracleapp.data;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppData {
    private static Stage primaryStage;
    private static Scene currentScene;

    // Конструктор приватный, чтобы предотвратить создание экземпляра класса
    private AppData() {}

    // Получить главную сцену
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    // Установить главную сцену
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    // Получить текущую сцену
    public static Scene getCurrentScene() {
        return currentScene;
    }

    // Установить текущую сцену
    public static void setCurrentScene(Scene scene) {
        currentScene = scene;
        primaryStage.setScene(scene);
    }
}


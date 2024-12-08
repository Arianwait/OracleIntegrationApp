package kz.aws.oracleapp.dispetcher;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import kz.aws.oracleapp.data.AppData;
import kz.aws.oracleapp.scene.LoginScene;

public class Dispatcher extends Application {

    @Override
    public void start(Stage primaryStage) {
        AppData.setPrimaryStage(primaryStage); // Устанавливаем основное окно в AppData
        primaryStage.setTitle("Авторизация");

        Scene loginScene = new Scene(new HBox(), 1280, 720); // Создаем сцену без начального корневого элемента
        AppData.setCurrentScene(loginScene); // Используем AppData для установки текущей сцены
        
        AppData.getCurrentScene().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        
        LoginScene.configureLoginScene(); // Настраиваем сцену логина

        primaryStage.show();
    }

    public static void updateScene(Scene newScene) {
        AppData.setCurrentScene(newScene); // Обновляем сцену через AppData
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
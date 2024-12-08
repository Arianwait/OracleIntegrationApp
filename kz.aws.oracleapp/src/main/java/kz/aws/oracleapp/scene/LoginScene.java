package kz.aws.oracleapp.scene;

import java.sql.Connection;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import kz.aws.oracleapp.data.AppData;
import kz.aws.oracleapp.data.UserData;
import kz.aws.oracleapp.database.DatabaseConnector;

public class LoginScene {

    public static void configureLoginScene() {
    	
        HBox root = new HBox(10); // Создаем HBox с внутренним отступом
        root.setAlignment(Pos.CENTER_RIGHT); // Выравниваем содержимое HBox по правому краю
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER); // Выравниваем GridPane по центру внутри HBox
        grid.setHgap(10);
        grid.setVgap(10);
        
        Label lblgen = new Label("Авторизация");
        lblgen.setStyle("-fx-font-size: 24px; -fx-text-alignment: center; -fx-text-fill: #ffffff;");
        grid.add(lblgen, 0, 0, 2, 1);
        GridPane.setHalignment(lblgen, HPos.CENTER); // Горизонтальное выравнивание по центру

        Label lblLogin = new Label("Логин:");
        grid.add(lblLogin, 0, 1);

        TextField txtLogin = new TextField();
        grid.add(txtLogin, 1, 1);

        Label lblPassword = new Label("Пароль:");
        grid.add(lblPassword, 0, 2);

        PasswordField pfPassword = new PasswordField();
        grid.add(pfPassword, 1, 2);

        Button btnLogin = new Button("Войти");
        btnLogin.setOnAction(event -> {
        	Connection connection  = DatabaseConnector.connect(txtLogin.getText(), pfPassword.getText());
            if (connection != null) {
            	UserData userData = new UserData(txtLogin.getText(), txtLogin.getText(), pfPassword.getText());
            	FollowingScene followingScene = new FollowingScene();
            	followingScene.configureFollowingScene(userData, root); // Конфигурирование существующей сцены
            } 
        });
        grid.add(btnLogin, 1, 3);
        
        root.getChildren().add(grid); // Добавляем GridPane в HBox

        root.setPadding(new Insets(0, 80, 0, 0)); // Устанавливаем отступ справа в 80 пикселей
        AppData.getCurrentScene().setRoot(root);
    }
}

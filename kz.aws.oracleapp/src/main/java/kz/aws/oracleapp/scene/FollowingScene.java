package kz.aws.oracleapp.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import kz.aws.oracleapp.data.AppData;
import kz.aws.oracleapp.data.UserData;
import kz.aws.oracleapp.scenehelper.Loader;

public class FollowingScene {
	private static BorderPane root;
	@SuppressWarnings("static-access")
	public void configureFollowingScene(UserData userData, HBox backToLoginScene) {
		root = new BorderPane(); // Использование BorderPane вместо VBox
		root.setPadding(new Insets(20));
		
	    Image image = new Image("file:data/person/elena.png");
	    ImageView imageView = new ImageView(image);
	    imageView.setFitWidth(400); // Ширина сцены
	    imageView.setFitHeight(700); // Высота сцены
	    imageView.setPreserveRatio(false);
	    StackPane.setAlignment(imageView, Pos.BOTTOM_CENTER);
	    imageView.setPreserveRatio(false);
	    
	    imageView.setId("custom-image-view"); // Устанавливаем ID для CSS
	    
	    root.getChildren().add(imageView);
	    
		AppData.getPrimaryStage().setTitle("Добро пожаловать");
		
		Text welcomeText = new Text("Добро пожаловать, " + userData.getUserName() + "!");
		welcomeText.setStyle("-fx-font-size: 16px;");

        MenuBar menuBar = new MenuBar();
        menuBar.getStyleClass().add("menu-bar");
        //views_config
        Menu viewsMenu = Loader.loadMenuItems("views_config.json", userData, root);
        
        Menu proceduresMenu = new Menu("Процедуры");        
        Menu sProceduresMenu = new Menu("Хранимые процедуры");
        
        // Создаем пункт меню "Выход"
        MenuItem exitItem = new MenuItem("Выход");

        // Создаем меню "Настройки" для размещения пункта "Выход"
        Menu optionsMenu = new Menu("Настройки");
        optionsMenu.getItems().add(exitItem);

        // Добавляем все созданные меню в меню бар
        menuBar.getMenus().addAll(viewsMenu, proceduresMenu, sProceduresMenu, optionsMenu);

        // Устанавливаем меню бар в верхней части корневого контейнера
        root.setTop(menuBar);
        root.setMargin(menuBar, new Insets(30)); // Устанавливает отступ в 10 пикселей со всех сторон
        
        exitItem.setOnAction(event -> AppData.getCurrentScene().setRoot(backToLoginScene));
		
        AppData.getCurrentScene().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        
		AppData.getCurrentScene().setRoot(root); // Установка BorderPane как корня сцены
	}
}
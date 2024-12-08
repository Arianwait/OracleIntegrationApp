package kz.aws.oracleapp.database;

import org.json.JSONArray;

import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

public interface DatabaseObject {
    String getName();
    MenuItem execute(String itemName, String name, BorderPane root, JSONArray fieldsArray);
    boolean isAvailable(String name); // Новый метод для проверки доступности объекта
}

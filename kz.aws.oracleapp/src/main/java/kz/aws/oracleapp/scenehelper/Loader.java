package kz.aws.oracleapp.scenehelper;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.control.Menu;
import javafx.scene.layout.BorderPane;
import kz.aws.oracleapp.data.UserData;
import kz.aws.oracleapp.database.DatabaseObject;
import kz.aws.oracleapp.database.DatabaseProcedure;
import kz.aws.oracleapp.database.DatabaseSProcedure;
import kz.aws.oracleapp.database.DatabaseView;

public class Loader {
	public static Menu loadMenuItems(String filePath, UserData userdata, BorderPane root) {
		Menu workMenu = new Menu("Default name");

		try {
			// Чтение файла
			String content = new String(Files.readAllBytes(Paths.get(filePath)));

			// Парсинг JSON
			JSONObject jsonObject = new JSONObject(content);
			String MenuName = jsonObject.getString("name");

			workMenu.setText(MenuName);

			JSONArray menuItemsArray = jsonObject.getJSONArray("MenuItem");

			for (int i = 0; i < menuItemsArray.length(); i++) {
				JSONObject menuItemObject = menuItemsArray.getJSONObject(i);

				// Имя и тип MenuItem
				String name = menuItemObject.getString("name");
				String sqlName = menuItemObject.getString("SQLName");
				String type = menuItemObject.getString("type");

				JSONArray fieldsArray = menuItemObject.getJSONArray("fields");

				DatabaseObject db;

				switch (type.toLowerCase()) { // Преобразуем тип в нижний регистр для универсальности
				case "view":
					db = new DatabaseView(userdata);
					if (db.isAvailable(sqlName)) {
						workMenu.getItems().add(db.execute(name, sqlName, root, fieldsArray));
					} else {
						System.out.println("Access denied for " + name);
					}
					break;

				case "hproc":
					db = new DatabaseProcedure(userdata);
					if (db.isAvailable(sqlName)) {
						workMenu.getItems().add(db.execute(name, sqlName, root, fieldsArray));
					} else {
						System.out.println("Access denied for " + name);
					}
					break;

				case "sproc":
					db = new DatabaseSProcedure(userdata);
					if (db.isAvailable(sqlName)) {
						workMenu.getItems().add(db.execute(name, sqlName, root, fieldsArray));
					} else {
						System.out.println("Access denied for " + name);
					}
					break;

				default:
					System.out.println("Unknown type: " + type);

					break;
				}
//	                for (int j = 0; j < fieldsArray.length(); j++) {
//	                    JSONObject fieldObject = fieldsArray.getJSONObject(j);
//	                    String fieldName = fieldObject.getString("name");
//	                    String fieldType = fieldObject.getString("type");
//	                    String fieldPlaceholder = fieldObject.getString("placeholder");
//	                }

				// Создание MenuItemData

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return workMenu;
	}

}

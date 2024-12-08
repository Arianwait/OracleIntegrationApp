package kz.aws.oracleapp.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;

import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import kz.aws.oracleapp.data.UserData;
import kz.aws.oracleapp.scene.DynamicFormProcedure;

public class DatabaseProcedure implements DatabaseObject {
	private UserData userdata;

	public DatabaseProcedure(UserData userdata) {
		this.userdata = userdata;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public MenuItem execute(String itemName, String name, BorderPane root, JSONArray fieldsArray) {
		MenuItem itemMenu = new MenuItem(itemName);

		itemMenu.setOnAction(event -> {
			DynamicFormProcedure formProcedure = new DynamicFormProcedure();
			formProcedure.start(root, name, userdata, fieldsArray);
		});

		return itemMenu;
	}

	@Override
	public boolean isAvailable(String name) {
	    String sql = "SELECT AIRMANADGER1.is_grantee_owner(?) AS Test FROM DUAL";

	    try (Connection connection = userdata.getConnection(); // Ваш метод для получения подключения
	         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

	        // Устанавливаем значение параметра
	        preparedStatement.setString(1, name);

	        try (ResultSet resultSet = preparedStatement.executeQuery()) {
	            if (resultSet.next()) {
	                // Получаем значение столбца "Test" и проверяем результат
	                int result = resultSet.getInt("Test");
	                return result == 1; // Возвращаем true, если 1, иначе false
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace(); // Обработка исключений
	    }

	    return false; // По умолчанию false, если ничего не найдено
	}

}

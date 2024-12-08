package kz.aws.oracleapp.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;

import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import kz.aws.oracleapp.data.UserData;
import kz.aws.oracleapp.scene.DynamicTableView;

public class DatabaseView implements DatabaseObject {
	private UserData userdata;

	public DatabaseView(UserData userdata) {
		this.userdata = userdata;
	}

	@Override
	public MenuItem execute(String itemName, String name, BorderPane root, JSONArray fieldsArray) {
		String query = "SELECT * FROM " + name; // Пример запроса

		MenuItem menuItem = new MenuItem(itemName);
		
		menuItem.setOnAction(event -> {
			PreparedStatement stmt;
			try {
				stmt = userdata.getConnection().prepareStatement(query);
				ResultSet rs = stmt.executeQuery();
				try {
					DynamicTableView infoScene = new DynamicTableView();
					infoScene.start(root, rs, userdata);
				} finally {
					if (rs != null)
						try {
							rs.close();
						} catch (SQLException ex) {
							//ex.printStackTrace();
							/* обработка закрытия */
						}
				}
			} catch (SQLException e) {
				//e.printStackTrace();
				/* обработка закрытия */
			}
		});

		return menuItem;
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

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}

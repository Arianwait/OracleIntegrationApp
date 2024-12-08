package kz.aws.oracleapp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import kz.aws.oracleapp.data.ConfigLoader;
import kz.aws.oracleapp.scenehelper.DialogHelper;

public class DatabaseConnector {
	private static Properties config = ConfigLoader.loadConfig();
	private static final String URL = config.getProperty("db.url");; // Обратите внимание на слеш вместо двоеточия перед xe
	
    public static Connection connect(String username, String password) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            // Получаем соединение без использования try-with-resources здесь
            Connection connection = getConnection( username, password);
            return connection;  // Возвращаем соединение, которое будет закрыто в вызывающем коде
        } catch (ClassNotFoundException e) {
            DialogHelper.showAlertDialog("Ошибка драйвера: " + e.getMessage());
        } catch (SQLException e) {
            DialogHelper.showAlertDialog("Ошибка подключения к базе данных: " + e.getMessage());
        }
        return null;
    }
    
    private static Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(URL, username, password);
    }
}

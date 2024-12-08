package kz.aws.oracleapp.data;

import java.sql.Connection;

import kz.aws.oracleapp.database.DatabaseConnector;

public class UserData {
    private String userName;
    private String login;
    private String password;

    // Конструктор для инициализации данных пользователя
    public UserData(String userName, String login, String password) {
        this.userName = userName;
        this.login = login;
        this.password = password;
    }

    // Геттер для имени пользователя
    public String getUserName() {
        return userName;
    }

    // Геттер для соединения
    public Connection getConnection() {
        return DatabaseConnector.connect(login, password);
    }

    // Сеттер для имени пользователя
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    // Сеттер для имени пользователя
    public void setlogin(String login) {
    	this.login = login;
    }
    
    // Сеттер для имени пользователя
    public void setPassword(String password) {
    	this.password = password;
    }
}
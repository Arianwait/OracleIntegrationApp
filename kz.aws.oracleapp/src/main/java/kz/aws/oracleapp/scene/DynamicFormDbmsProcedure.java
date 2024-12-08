package kz.aws.oracleapp.scene;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import org.json.JSONArray;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import kz.aws.oracleapp.data.AppData;
import kz.aws.oracleapp.data.UserData;

public class DynamicFormDbmsProcedure {

    private TextArea outputArea; // TextArea для вывода результатов DBMS_OUTPUT
    private UserData userdata;
    
    public DynamicFormDbmsProcedure(UserData userdata) {
        this.userdata = userdata;
    }
    
    public void start(BorderPane root2, JSONArray fieldsArray, String procName, String offname) {
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 30;");
        
        HBox hbox = new HBox(10);
        
        Button executeButton = new Button("Загрузить");
        executeButton.setOnAction(event -> executeProcedureAndFetchOutput(procName));

        Button backButton = new Button("Выход");
        backButton.setOnAction(event -> AppData.getCurrentScene().setRoot(root2));
        
        // Место для вывода результатов
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(200); // Настройка высоты TextArea
        root.getChildren().add(new Label("Получение данных с таблицы: " + offname));
        hbox.getChildren().addAll(backButton, executeButton);
        root.getChildren().addAll(hbox, outputArea);
        
        AppData.getCurrentScene().setRoot(root);
    }

    private void executeProcedureAndFetchOutput(String procName) {
        try {
        	Connection con = userdata.getConnection();
            try (CallableStatement enableStmt = con.prepareCall("{call dbms_output.enable()}")) {
                enableStmt.execute();
            }

            // Выполняем процедуру
            try (CallableStatement callStmt = con.prepareCall("{call " + procName + "}")) {
                callStmt.execute();
            }

            // Чтение DBMS_OUTPUT
            try (CallableStatement fetchStmt = con.prepareCall("{call dbms_output.get_line(?, ?)}")) {
                fetchStmt.registerOutParameter(1, Types.VARCHAR);
                fetchStmt.registerOutParameter(2, Types.INTEGER);
                StringBuilder outputBuilder = new StringBuilder();
                int status = 0;
                while (status == 0) {
                    fetchStmt.execute();
                    String line = fetchStmt.getString(1);
                    status = fetchStmt.getInt(2);
                    if (line != null && status == 0) {
                        outputBuilder.append(line).append("\n");
                    }
                }
                outputArea.setText(outputBuilder.toString());
            }
        } catch (SQLException e) {
            outputArea.setText("Error executing procedure: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
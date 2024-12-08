package kz.aws.oracleapp.scene;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import kz.aws.oracleapp.data.AppData;
import kz.aws.oracleapp.data.UserData;
import kz.aws.oracleapp.excel.ExportToExcel;

public class DynamicTableView{
	private TableView<ObservableList<String>> tableView = new TableView<>();
    public void start(BorderPane back, ResultSet rs, UserData userData)  {
    	VBox root = initializeUI(back, rs);
    	AppData.getCurrentScene().setRoot(root);
    }

    private void populateTableView(TableView<ObservableList<String>> tableView, ResultSet rs) {
        try {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Создание столбцов на основе метаданных ResultSet
            for (int i = 1; i <= columnCount; i++) {
                final int j = i;
                TableColumn<ObservableList<String>, String> column = new TableColumn<>(metaData.getColumnName(i));
                column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j - 1)));
                tableView.getColumns().add(column);
            }

            // Заполнение данных
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);
            }
            tableView.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private VBox initializeUI(BorderPane back, ResultSet rs) {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        
        Label headerLabel = new Label("Таблица");
        headerLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #2A2A2A;");
        
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER);
        
        Button backButton = new Button("Назад");
        backButton.setOnAction(event -> AppData.getCurrentScene().setRoot(back));
        ExportToExcel export = new ExportToExcel();
        Button exportButton = new Button("Export to Excel");
        
        exportButton.setOnAction(event -> {
            export.exportToExcel(tableView);
        });
        
        
        populateTableView(tableView, rs);
        //Button saveButton = createSaveButton(tableView);
        
        hbox.getChildren().addAll(exportButton, backButton);
        root.getChildren().addAll(headerLabel, tableView, hbox);
        return root;
    }
}

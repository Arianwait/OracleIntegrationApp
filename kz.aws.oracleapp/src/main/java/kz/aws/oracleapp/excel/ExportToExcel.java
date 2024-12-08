package kz.aws.oracleapp.excel;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import kz.aws.oracleapp.data.AppData;

public class ExportToExcel {
    public void exportToExcel(TableView<ObservableList<String>> table) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel File");
        fileChooser.setInitialFileName("data.xlsx");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );

        File file = fileChooser.showSaveDialog(AppData.getPrimaryStage());
        if (file != null) {
            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Data");

            // Создание заголовка
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < table.getColumns().size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(table.getColumns().get(i).getText());
            }

            // Заполнение данных
            for (int i = 0; i < table.getItems().size(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < table.getColumns().size(); j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(table.getItems().get(i).get(j));
                }
            }

            // Сохранение файла
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
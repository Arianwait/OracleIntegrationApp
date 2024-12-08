package kz.aws.oracleapp.scene;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import kz.aws.oracleapp.data.AppData;
import kz.aws.oracleapp.data.UserData;
import kz.aws.oracleapp.scenehelper.DatabaseQueryHandler;
import kz.aws.oracleapp.scenehelper.DialogHelper;

public class DynamicFormProcedure {
	private VBox vbox = new VBox(10);

	public void start(BorderPane back, String procedureName, UserData userdata, JSONArray fieldsArray) {

		vbox.setStyle("-fx-padding: 30;");

		for (int i = 0; i < fieldsArray.length(); i++) {
			JSONObject field = fieldsArray.getJSONObject(i);
			Label label = new Label(field.getString("name"));
			switch (field.getString("type")) {
			case "NUMBER":
				TextField textField = new TextField();
				textField.setPromptText(field.getString("placeholder"));
				vbox.getChildren().addAll(label, textField);
				break;
			case "DATE":
				DatePicker datePicker = new DatePicker();

				Spinner<Integer> hourSpinner = new Spinner<>(0, 23, LocalTime.now().getHour());
				hourSpinner.setEditable(true);

				Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, LocalTime.now().getMinute());
				minuteSpinner.setEditable(true);

				HBox timeBox = new HBox(5, new Label("Дата:"), datePicker, new Label("Часы:"), hourSpinner,
						new Label("Минуты:"), minuteSpinner);

				// Добавляем элементы в пользовательский интерфейс
				vbox.getChildren().addAll(label, timeBox);
				break;
			case "VARCHAR2":
				TextField textField1 = new TextField();
				textField1.setPromptText(field.getString("placeholder"));
				vbox.getChildren().addAll(label, textField1);
				break;
			case "NCHAR":
				TextField textField11 = new TextField();
				textField11.setPromptText(field.getString("placeholder"));
				vbox.getChildren().addAll(label, textField11);
				break;
			case "TableList":
				JSONObject tablelistjson = field.getJSONObject("ListItems");

				// Получение списка пар (id, name) из базы данных
				List<Pair<Object, String>> tableList = null;
				try {
					tableList = DatabaseQueryHandler.processJsonArray(tablelistjson, userdata);
				} catch (SQLException e) {
					e.printStackTrace();
				}

				ComboBox<String> comboBox = new ComboBox<>();
				Map<Object, Object> nameToIdMap = new HashMap<>();

				// Заполнение ComboBox данными и картой
				for (Pair<Object, String> pair : tableList) {
					comboBox.getItems().add(pair.getValue()); // Добавляем имя в ComboBox
					nameToIdMap.put(pair.getValue(), pair.getKey()); // Связываем имя с идентификатором
				}

				// Привязываем карту к ComboBox с помощью setUserData
				comboBox.setUserData(nameToIdMap);

				// Настройка подсказки для ComboBox
				comboBox.setPromptText("Выберете " + field.getString("name"));

				// Добавление ComboBox в пользовательский интерфейс
				vbox.getChildren().addAll(label, comboBox);
				break;
			}
		}
		VBox v2 = new VBox(10);

		HBox hbox = new HBox(10);
		Button saveButton = new Button("Загрузить");
		saveButton.setOnAction(event -> saveAction(vbox, procedureName, userdata));
		Button exitButton = new Button("Выйти");
		exitButton.setOnAction(event -> AppData.getCurrentScene().setRoot(back));
		hbox.getChildren().addAll(saveButton, exitButton);
		v2.getChildren().add(hbox);
		vbox.getChildren().add(v2);
		AppData.getCurrentScene().setRoot(vbox);
	}

	private void saveAction(VBox vbox, String procedureName, UserData userdata) {
		try {
			String procedureCall = createProcedureCall(vbox, procedureName);
			CallableStatement stmt = userdata.getConnection().prepareCall(procedureCall);

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			int parameterIndex = 1;

			// Создаем копию строки вызова для логирования
			String executingQuery = procedureCall;

			for (javafx.scene.Node node : vbox.getChildren()) {
				if (node instanceof TextField) {
					TextField textField = (TextField) node;
					String textValue = textField.getText();

					try {
						int intValue = Integer.parseInt(textValue);
						stmt.setInt(parameterIndex, intValue);
					} catch (NumberFormatException nfe) {
						try {
							java.util.Date parsedDate = dateFormat.parse(textValue);
							stmt.setDate(parameterIndex, new java.sql.Date(parsedDate.getTime()));
						} catch (ParseException pe) {
							stmt.setNString(parameterIndex, textValue);
						}
					}
					parameterIndex++;
				} else if (node instanceof ComboBox) {
					@SuppressWarnings("unchecked")
					ComboBox<String> comboBox = (ComboBox<String>) node;
					String selectedValue = comboBox.getValue();
					if (selectedValue != null) {
						@SuppressWarnings("unchecked")
						Map<String, Object> nameToIdMap = (Map<String, Object>) comboBox.getUserData();
						if (nameToIdMap != null) {
							if (nameToIdMap.get(selectedValue) instanceof Integer) {
								Integer id = (Integer) nameToIdMap.get(selectedValue);
								if (id != null) {
									stmt.setInt(parameterIndex, id);
								}
							} else if (nameToIdMap.get(selectedValue) instanceof String) {
								String id = (String) nameToIdMap.get(selectedValue);
								if (id != null) {
									stmt.setNString(parameterIndex, id);
								}
						    }
						} 
					}
					parameterIndex++;
				} else if (node instanceof HBox) {
					HBox timeBox = (HBox) node; // Предполагаем, что node — это HBox

					DatePicker datePicker = null;
					Spinner<Integer> hourSpinner = null;
					Spinner<Integer> minuteSpinner = null;

					System.out.println("Processing HBox content:");

					// Перебираем элементы внутри HBox
					for (Node child : timeBox.getChildren()) {
						if (child instanceof Label) {
							Label label = (Label) child;
							System.out.println("Found Label: " + label.getText());
						} else if (child instanceof DatePicker) {
							datePicker = (DatePicker) child;
							System.out.println("Found DatePicker: " + datePicker);
						} else if (child instanceof Spinner) {
							@SuppressWarnings("unchecked")
							Spinner<Integer> spinner = (Spinner<Integer>) child;
							int index = timeBox.getChildren().indexOf(child);

							// Проверяем предыдущий элемент для идентификации спиннера
							if (index > 0 && timeBox.getChildren().get(index - 1) instanceof Label) {
								Label label = (Label) timeBox.getChildren().get(index - 1);
								if ("Часы:".equals(label.getText())) {
									hourSpinner = spinner; // Spinner для часов
									System.out.println("Found Hour Spinner: " + hourSpinner.getValue());
								} else if ("Минуты:".equals(label.getText())) {
									minuteSpinner = spinner; // Spinner для минут
									System.out.println("Found Minute Spinner: " + minuteSpinner.getValue());
								}
							}
						} else {
							System.out.println("Unknown child node: " + child.getClass().getSimpleName());
						}
					}

					// Проверяем результат
					if (datePicker != null) {
						System.out.println("DatePicker value: " + datePicker.getValue());
					}
					if (hourSpinner != null) {
						System.out.println("Hour Spinner value: " + hourSpinner.getValue());
					}
					if (minuteSpinner != null) {
						System.out.println("Minute Spinner value: " + minuteSpinner.getValue());
					}

					// Проверяем, что все элементы найдены
					if (datePicker != null && hourSpinner != null && minuteSpinner != null) {
						LocalDate date = datePicker.getValue();
						Integer hour = hourSpinner.getValue();
						Integer minute = minuteSpinner.getValue();

						if (date != null && hour != null && minute != null) {
							// Объединяем дату и время
							LocalDateTime dateTime = date.atTime(hour, minute);

							// Преобразуем в Timestamp для SQL
							Timestamp sqlTimestamp = Timestamp.valueOf(dateTime);

							// Устанавливаем значение в PreparedStatement
							stmt.setTimestamp(parameterIndex, sqlTimestamp);

							// Логируем результат
							System.out.println("Parameter Index: " + parameterIndex);
							System.out.println("SQL Timestamp: " + sqlTimestamp);
						}
					}
					parameterIndex++;
				}
			}

			// Логирование финального запроса
			System.out.println("Executing Query: " + executingQuery);

			stmt.execute();
			DialogHelper.showAlertDialog("Data saved successfully.");
		} catch (SQLException e) {
			DialogHelper.showAlertDialog("Error executing stored procedure: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private String createProcedureCall(VBox vbox, String procedureName) {
		long paramsCount = vbox.getChildren().stream()
				.filter(node -> node instanceof TextField || node instanceof ComboBox || node instanceof HBox).count();

		StringJoiner joiner = new StringJoiner(", ", "{ call " + procedureName + "(", ") }");
		for (int i = 0; i < paramsCount; i++) {
			joiner.add("?");
		}
		return joiner.toString();
	}

	public JSONObject loadConfiguration(String fileName) {
		StringBuilder content = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line);
			}
			return new JSONObject(content.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
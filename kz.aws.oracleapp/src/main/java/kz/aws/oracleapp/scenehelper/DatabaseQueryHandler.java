package kz.aws.oracleapp.scenehelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import javafx.util.Pair;
import kz.aws.oracleapp.data.UserData;

public class DatabaseQueryHandler {
	public static List<Pair<Object, String>> processJsonArray(JSONObject jsonObject, UserData userdata) throws SQLException {
        List<Pair<Object, String>> resultList = new ArrayList<>();

            // Извлечение параметров из JSON
            String tableName = jsonObject.getString("TableName");
            String indexColumn = jsonObject.getString("indexColumn");
            String valueColumn = jsonObject.getString("valueColumn");
            String columnType = jsonObject.getString("indextype");

            // Запрос данных из базы
            String query = String.format("SELECT %s, %s FROM %s", indexColumn, valueColumn, tableName);

            try (Statement statement = userdata.getConnection().createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                // Извлечение данных из ResultSet и добавление их в список
                while (resultSet.next()) {
                	if(columnType.equals("int")) {
                		Integer indexValue = resultSet.getInt(indexColumn); // Преобразование к Integer
                        String value = resultSet.getString(valueColumn);    // Преобразование к String
                        resultList.add(new Pair<>(indexValue, value));
                	} else {
                		String indexValue = resultSet.getString(indexColumn); // Преобразование к Integer
                        String value = resultSet.getString(valueColumn);    // Преобразование к String
                        resultList.add(new Pair<>(indexValue, value));
                	}
                }
            }
        

        return resultList;
    }
}

package kz.aws.oracleapp.scenehelper;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DialogHelper {

    public static void showAlertDialog(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateBodyController {

    @FXML
    private ChoiceBox<Body.Type> typeChoice;

    @FXML
    private TextField massField, radiusField, vxField, vyField;
    private Body result;

    @FXML
    public void initialize() {
        typeChoice.getItems().addAll(Body.Type.values());
        typeChoice.setValue(Body.Type.Planet);
    }

    @FXML
    private void onCreate() {
        Body body = new Body(typeChoice.getValue());
        body.mass = Double.parseDouble(massField.getText());
        body.rad = Double.parseDouble(radiusField.getText());
        body.vx = Double.parseDouble(vxField.getText());
        body.vy = Double.parseDouble(vyField.getText());
        result = body;
        close();
    }

    @FXML
    private void onCancel() {
        result = null;
        close();
    }

    private void close() {
        Stage stage = (Stage) typeChoice.getScene().getWindow();
        stage.close();
    }

    public Body getResult() {
        return result;
    }
}

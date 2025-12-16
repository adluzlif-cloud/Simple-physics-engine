package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CreateBodyController {

    @FXML
    private ChoiceBox<Body.Type> typeChoice;

    @FXML
    private VBox massiveBox, photonBox;

    @FXML
    private Slider angleSlider;

    @FXML
    private Label angleLabel;

    @FXML
    private TextField massField, radiusField, vxField, vyField;

    private Body result;

    @FXML
    private CheckBox fixed;

    @FXML
    public void initialize() {
    typeChoice.getItems().setAll(Body.Type.values());
    typeChoice.setValue(Body.Type.Planet);

    angleSlider.valueProperty().addListener((o, a, b) ->
            angleLabel.setText(String.format("%.0f°", b.doubleValue()))
    );

    typeChoice.valueProperty().addListener((o, a, b) ->
            updateVisibility(b)
    );

    updateVisibility(typeChoice.getValue());
}

    private void updateVisibility(Body.Type type) {
    boolean isPhoton = type == Body.Type.Photon;
    boolean isBlackHole = type == Body.Type.Black_Hole;

    massiveBox.setManaged(!isPhoton);
    massiveBox.setVisible(!isPhoton);

    photonBox.setManaged(isPhoton);
    photonBox.setVisible(isPhoton);

    fixed.setManaged(isBlackHole);
    fixed.setVisible(isBlackHole);
    }

    @FXML
    private void onCreate() {
        Body.Type type = typeChoice.getValue();
        if (type == null) return;
        Body body = new Body(type);

        if (type == Body.Type.Photon) {
            body.mass = 0;
            body.rad = 2;

            double speed = 300;

            double angleDeg = angleSlider.getValue();
            double angleRad = Math.toRadians(angleDeg);

            body.vx = speed * Math.cos(angleRad);
            body.vy = speed * Math.sin(angleRad);
        }
        else {
        body.mass = Double.parseDouble(massField.getText());
        body.rad  = Double.parseDouble(radiusField.getText());
        body.vx   = Double.parseDouble(vxField.getText());
        body.vy   = Double.parseDouble(vyField.getText());
        body.fixed = (type == Body.Type.Black_Hole) && fixed.isSelected();
    }

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
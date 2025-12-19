package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.stage.Stage;


public class SettingsController {

    @FXML
    private CheckBox trailCheckBox;

    @FXML
    private CheckBox gunCheckBox;

    @FXML
    private Slider timeSlider;

    @FXML 
    private Slider directionSlider;
    
    @FXML 
    private Slider spreadSlider;

    @FXML 
    private CheckBox fieldCheckBox;

    @FXML
    public void initialize() {
        trailCheckBox.setSelected(Settings.showTrails);
        gunCheckBox.setSelected(Settings.gunMode);
        timeSlider.setValue(Settings.timeScale);
        directionSlider.setValue(Settings.gunDirectionDeg);
        spreadSlider.setValue(Settings.gunSpreadDeg);
        fieldCheckBox.setSelected(Settings.showField);

        trailCheckBox.selectedProperty().addListener((o, oldVal, newVal) -> Settings.showTrails = newVal);
        gunCheckBox.selectedProperty().addListener((o, oldVal, newVal) -> Settings.gunMode = newVal);
        timeSlider.valueProperty().addListener((o, oldVal, newVal) -> Settings.timeScale = newVal.doubleValue());
        directionSlider.valueProperty().addListener((o, oldV, newV) -> Settings.gunDirectionDeg = newV.doubleValue());
        spreadSlider.valueProperty().addListener((o, oldV, newV) -> Settings.gunSpreadDeg = newV.doubleValue());
        fieldCheckBox.selectedProperty().addListener((o,a,b) -> Settings.showField = b);
    }

    @FXML
    private void close() {
        Stage stage = (Stage) trailCheckBox.getScene().getWindow();
        stage.close();
    }
}
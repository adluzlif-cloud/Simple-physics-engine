package com.example;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SimulationController {

    @FXML 
    private Canvas canvas;

    private Simulation sim;

    public void initialize() {
        sim = new Simulation();

        GraphicsContext g = canvas.getGraphicsContext2D();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double dt = 0.016;
                g.setFill(Color.BLACK);
                g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                sim.update(dt);
                sim.render(g);
            }
        };
        timer.start();
        
        canvas.setOnMouseClicked(event -> {
            try {
                openCreateBodyDialog(event.getX(), event.getY());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void openCreateBodyDialog(double x, double y) throws Exception {

    FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/example/create_body.fxml")
    );

    Stage dialog = new Stage();
    dialog.setScene(new Scene(loader.load()));
    dialog.setTitle("Create Object");
    dialog.initModality(Modality.APPLICATION_MODAL);

    dialog.showAndWait();

    CreateBodyController controller = loader.getController();
    Body body = controller.getResult();

    if (body != null) {
        body.x = x;
        body.y = y;
        sim.addObject(body);
    }
}

}
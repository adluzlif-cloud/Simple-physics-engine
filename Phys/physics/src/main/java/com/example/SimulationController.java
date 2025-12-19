package com.example;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SimulationController {

    @FXML 
    private Canvas canvas;

    @FXML
    private Button pauseButton;

    @FXML 
    private Canvas fieldCanvas;

    private Simulation sim;

    private Body draggedBody = null;
    private Body hoveredBody = null;

    private boolean gunFiring = false;
    private int gunCooldown = 0;
    private int gunInterval = 5;
    private double gunX, gunY;


    public void initialize() {
        sim = new Simulation();

        GraphicsContext g = canvas.getGraphicsContext2D();

        AnimationTimer timer = new AnimationTimer() {
            
            @Override
            public void handle(long now) {

            double w = canvas.getWidth();
            double h = canvas.getHeight();

            if (Settings.showField) {
                renderField();
            } 
            else {
            fieldCanvas.getGraphicsContext2D() .clearRect(0, 0, w, h);
            }
            GraphicsContext g = canvas.getGraphicsContext2D();
            g.setFill(Color.BLACK);
            g.fillRect(0, 0, w, h);

            if (!Settings.paused) {
                sim.update(0.016 * Settings.timeScale, w, h);
            }

            if (Settings.gunMode && gunFiring && !Settings.paused) {
                gunCooldown -= 0.016;
                if (gunCooldown <= 0) {
                    spawnGunObject(gunX, gunY);
                    gunCooldown = gunInterval;
                }
            }

            sim.render(g);
        }

        };
        timer.start();
        
        canvas.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY && Settings.gunMode) {
                gunFiring = true;
                gunX = e.getX();
                gunY = e.getY();
                e.consume();
            }
        });

        canvas.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                gunFiring = false;
            }
        });

        canvas.setOnMouseDragged(event -> {
            if (gunFiring && Settings.gunMode) {
                gunX = event.getX();
                gunY = event.getY();
                event.consume();
                return;
            }
            if (draggedBody != null) {
                draggedBody.x = event.getX();
                draggedBody.y = event.getY();
                draggedBody.vx = 0;
                draggedBody.vy = 0;
            }
        });

        canvas.setOnMouseReleased(event -> {
            draggedBody = null;
        });

        canvas.setFocusTraversable(true);

        canvas.setOnMouseClicked(e -> canvas.requestFocus());

        canvas.setOnMouseMoved(event -> {
            hoveredBody = findBodyAt(event.getX(), event.getY());
        });

        canvas.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE && hoveredBody != null) {
                sim.removeObject(hoveredBody);
                hoveredBody = null;
            }
        });

        canvas.setOnMousePressed(event -> {

            if (event.getButton() == MouseButton.SECONDARY) {

                if (Settings.gunMode) {
                    gunX = event.getX();
                    gunY = event.getY();
                    gunFiring = true;
                } 
                else {
            
                    try {
                        openCreateBodyDialog(event.getX(), event.getY());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                event.consume();
                return;
            }
            if (event.getButton() == MouseButton.PRIMARY) {
                draggedBody = findBodyAt(event.getX(), event.getY());
            }
        });

        canvas.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                gunFiring = false;
            }
        });

        canvas.setOnMouseMoved(event -> {
            gunX = event.getX();
            gunY = event.getY();
            hoveredBody = findBodyAt(gunX, gunY);
        });

        fieldCanvas.widthProperty().bind(canvas.widthProperty());
        fieldCanvas.heightProperty().bind(canvas.heightProperty());
        fieldCanvas.setMouseTransparent(true);
    }

    /////////

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

    private Body findBodyAt(double x, double y) {
        for (int i = sim.objects.size() - 1; i >= 0; i--) {
            Body b = sim.objects.get(i);
            double dx = x - b.x;
            double dy = y - b.y;
            if (dx * dx + dy * dy <= b.rad * b.rad + 25) {
                return b;
            }
        }
        return null;
    }

    @FXML
    private void openSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/settings.fxml")
            );

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Settings");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void spawnGunObject(double x, double y) {

        Body b = new Body(Body.Type.Photon);
        b.x = x;
        b.y = y;
        b.mass = 0;
        b.rad = 2;

        double baseAngleRad =
                Math.toRadians(Settings.gunDirectionDeg);

        double spreadRad =
                Math.toRadians(Settings.gunSpreadDeg);

        double angle =
                baseAngleRad +
                (Math.random() - 0.5) * spreadRad;

        double speed = Settings.gunSpeed;

        b.vx = speed * Math.cos(angle);
        b.vy = speed * Math.sin(angle);

        sim.addObject(b);
    }

    private Color forceToColor(double f) {

    double v = Math.log(1 + f) * 0.15;
    v = Math.min(v, 1.0);

    return Color.color(v, 0, 1 - v, 0.4);
    }


    private void renderField() {
    GraphicsContext g = fieldCanvas.getGraphicsContext2D();
    double w = fieldCanvas.getWidth();
    double h = fieldCanvas.getHeight();
    g.clearRect(0, 0, w, h);
    int step = 5;
    double G = 1.0;

    for (int y = 0; y < h; y += step) {
        for (int x = 0; x < w; x += step) {

            double fx = 0;
            double fy = 0;

            for (Body b : sim.objects) {

                if (b.mass <= 0) continue;

                    double dx = b.x - x;
                    double dy = b.y - y;
                    double r2 = dx*dx + dy*dy + 25;

                    double f = G * b.mass / r2;

                    double r = Math.sqrt(r2);
                    fx += f * dx / r;
                    fy += f * dy / r;
                }

                double force = Math.sqrt(fx*fx + fy*fy);

                Color c = forceToColor(force);
                g.setFill(c);
                g.fillRect(x, y, step, step);
            }
        }
    }

    @FXML
    private void togglePause() {
        Settings.paused = !Settings.paused;
        pauseButton.setText(Settings.paused ? "Resume" : "Pause");
    }
}
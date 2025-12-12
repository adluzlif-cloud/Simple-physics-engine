package com.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Body{
    public enum Type {
        Planet,
        Black_Hole,
        Photon
    }
    public Type type;
    public double x, y;
    public double vx, vy;
    public double mass;
    public double rad;
    public double ax, ay;

    Body(){}
    Body(Type type){
        this.type = type;
    }
    Body(Type type, double x, double y, double vx, double vy, double mass, double rad){
        this.type = type;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
        this.rad = rad;
    }

        public void resetAcceleration() {
        ax = 0;
        ay = 0;
    }
    public void updatePhoton(double dt, Simulation sim){
        for (Body other : sim.objects) {

        double dx = other.x - x;
        double dy = other.y - y;
        double r2 = dx*dx + dy*dy;
        double r = Math.sqrt(r2);

        double bend = 3000 / (r2 * r);

        vx += bend * dx * dt;
        vy += bend * dy * dt;
        }
    }
    public void render(GraphicsContext g) {
        g.setFill(Color.RED);
        g.fillOval(
            x - rad,
            y - rad,
            rad * 2,
            rad * 2
        );
    }
}
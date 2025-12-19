package com.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayDeque;
import java.util.Deque;

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
    public boolean fixed = false;
    public double offscreenTime = 0;

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

    public void updatePhoton(double dt, Simulation sim) {

        double c = 300;

        for (Body other : sim.objects) {

            if (other == this) continue;
            if (other.type != Type.Black_Hole) continue;

            double dx = other.x - x;
            double dy = other.y - y;

            double r2 = dx * dx + dy * dy;
            if (r2 < 0.001) continue;

            double r = Math.sqrt(r2);

            double rs = other.rad;
            double factor = 1.0 + rs / r;

            double bend = 2* other.mass * factor / (r2 * r);

            vx += bend * dx * dt;
            vy += bend * dy * dt;
        }

        double len = Math.sqrt(vx * vx + vy * vy);

        if (len > 0) {
            vx = vx / len * c;
            vy = vy / len * c;
        }
        x += vx * dt;
        y += vy * dt;
    }

    public boolean isOutside(double width, double height) {
        return x + rad < 0 || x - rad > width || y + rad < 0 || y - rad > height;
    }

    public Deque<double[]> trail = new ArrayDeque<>();
    public int maxTrailLength = 600;

    public void addTrailPoint() {
        trail.addLast(new double[]{x, y});
        if (trail.size() > maxTrailLength) {
            trail.removeFirst();
        }
    }
    
    public void render(GraphicsContext g) {
        switch (type) {
        case Planet -> g.setFill(Color.GREEN);
        case Black_Hole -> g.setFill(Color.RED);
        case Photon -> g.setFill(Color.YELLOW);
        }
        g.fillOval(x - rad,y - rad,rad * 2,rad * 2);

        if (Settings.showTrails && trail.size() > 1){
            switch (type){
            case Planet -> g.setStroke(Color.GREEN);
            case Black_Hole -> g.setStroke(Color.RED);
            case Photon -> g.setStroke(Color.YELLOW);
            }
            g.setLineWidth(1);

            double[] prev = null;
            for (double[] p : trail) {
                if (prev != null) {
                    g.strokeLine(prev[0], prev[1], p[0], p[1]);
                }
                prev = p;
            }
        }
    }
}
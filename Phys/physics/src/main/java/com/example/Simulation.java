package com.example;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;

public class Simulation {

    public List<Body> objects = new ArrayList<>();

    public void addObject(Body body) {
        objects.add(body);
    }

    public void update(double dt) {

    for (Body b : objects) {
        b.resetAcceleration();
    }

    double G = 1.0;

    for (int i = 0; i < objects.size(); i++) {
        Body a = objects.get(i);

        if (a.type == Body.Type.Photon) continue;

        for (int j = i + 1; j < objects.size(); j++) {
            Body b = objects.get(j);

            if (b.type == Body.Type.Photon) continue;

            double dx = b.x - a.x;
            double dy = b.y - a.y;

            double r2 = dx * dx + dy * dy;
            if (r2 < 1e-4) continue;

            double r = Math.sqrt(r2);

            double ux = dx / r;
            double uy = dy / r;

            double force = G * a.mass * b.mass / r2;

            a.ax += force / a.mass * ux;
            a.ay += force / a.mass * uy;

            b.ax -= force / b.mass * ux;
            b.ay -= force / b.mass * uy;
        }
    }

    for (Body b : objects) {

        if (b.type == Body.Type.Photon) {
            b.updatePhoton(dt, this);
            continue;
        }

        b.vx += b.ax * dt;
        b.vy += b.ay * dt;

        b.x += b.vx * dt;
        b.y += b.vy * dt;
    }
}


    public void render(GraphicsContext g) {
        for (Body b : objects) {
            b.render(g);
        }
    }
}

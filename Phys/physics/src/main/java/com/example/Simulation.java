package com.example;

import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;

public class Simulation {

    public List<Body> objects = new ArrayList<>();

    public void addObject(Body body) {
        objects.add(body);
    }

    public void update(double dt, double width, double height) {

        double G = 1.0;
        double minR2 = 25;

        List<Body> toRemove = new ArrayList<>();

        for (Body b : objects) {
            if (b.type == Body.Type.Photon) continue;
            if (b.fixed) continue;
            b.vx += b.ax * dt * 0.5;
            b.vy += b.ay * dt * 0.5;
        }

        for (Body b : objects) {
            if (b.type == Body.Type.Photon) {
                b.updatePhoton(dt, this);
                continue;
            }
            if (b.fixed) continue;
            b.x += b.vx * dt;
            b.y += b.vy * dt; 
        }

        for (Body b : objects) {
            b.addTrailPoint();
    }

    for (Body b : objects) {
        b.ax = 0;
        b.ay = 0;
    }

    for (int i = 0; i < objects.size(); i++) {
        Body a = objects.get(i);
        if (a.type == Body.Type.Photon) continue;
        if (a.mass <= 0) continue;
        for (int j = i + 1; j < objects.size(); j++) {
            Body b = objects.get(j);
            if (b.type == Body.Type.Photon) continue;
            if (b.mass <= 0) continue;

            double dx = b.x - a.x;
            double dy = b.y - a.y;

            double r2 = dx * dx + dy * dy;
            if (r2 < minR2) r2 = minR2;
            double r = Math.sqrt(r2);
            double ux = dx / r;
            double uy = dy / r;
            double force = G * a.mass * b.mass / r2;

            if (!a.fixed) {
                a.ax += force / a.mass * ux;
                a.ay += force / a.mass * uy;
            }
            if (!b.fixed) {
                 b.ax -= force / b.mass * ux;
                b.ay -= force / b.mass * uy;
            }
        }
    }

        for (Body b : objects) {

            if (b.type == Body.Type.Photon) continue;
            if (b.fixed) continue;

            b.vx += b.ax * dt * 0.5;
            b.vy += b.ay * dt * 0.5;
        }

        checkAbsorptions();

        for (Body b : objects) {

        if (b.isOutside(width, height)) {
            b.offscreenTime += dt;
            if (b.offscreenTime > 5.0) {
                toRemove.add(b);
            }
        } 
        else {
            b.offscreenTime = 0;
        }
    }

    for (Body b : toRemove) {
        removeObject(b);
    }
}


    //////////////
    //////////////
    //////////////


    public void removeObject(Body body) {
        objects.remove(body);
    }

    public void checkAbsorptions() {

    List<Body> toRemove = new ArrayList<>();

    for (int i = 0; i < objects.size(); i++) {
        Body a = objects.get(i);

        if (a.type != Body.Type.Black_Hole) continue;

        for (int j = i + 1; j < objects.size(); j++) {
            Body b = objects.get(j);

            double dx = b.x - a.x;
            double dy = b.y - a.y;
            double dist2 = dx * dx + dy * dy;

            if (b.type == Body.Type.Black_Hole) {

                double minRad = Math.max(a.rad, b.rad);

                if (dist2 < minRad * minRad) {

                    Body big = (a.mass >= b.mass) ? a : b;
                    Body small = (a.mass >= b.mass) ? b : a;

                    if (!toRemove.contains(small)) {
                        big.mass += small.mass;
                        big.rad = 2 * big.mass / (300 * 300);
                        toRemove.add(small);
                    }
                }

            }
            else {

                if (dist2 < a.rad * a.rad) {
                    if (!toRemove.contains(b)) {
                        a.mass += b.mass;
                        a.rad = 2 * a.mass / (300 * 300);;
                        toRemove.add(b);
                    }
                }
            }
        }
    }

        for (Body b : toRemove) {
            removeObject(b);
        }
    }

    public void render(GraphicsContext g) {
        for (Body b : objects) {
            b.render(g);
        }
    }
}

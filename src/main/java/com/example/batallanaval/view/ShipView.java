package com.example.batallanaval.view;

import com.example.batallanaval.model.IShip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ShipView extends Pane {
    private final IShip ship;
    private final Rectangle[] parts;
    private String direction = "RIGHT"; // Dirección inicial
    private boolean placed = false; // Ya está en tablero

    public ShipView(IShip ship) {
        this.ship = ship;
        this.parts = new Rectangle[ship.getSize()];
        createShape();
        setFocusTraversable(true);
    }

    private void createShape() {
        getChildren().clear();
        for (int i = 0; i < ship.getSize(); i++) {
            Rectangle r = new Rectangle(35, 35, Color.GREEN);
            r.setStroke(Color.BLACK);
            parts[i] = r;
            getChildren().add(r);
        }
        rotateShape();
    }

    private void rotateShape() {
        for (int i = 0; i < parts.length; i++) {
            Rectangle r = parts[i];
            switch (direction) {
                case "RIGHT" -> {
                    r.setLayoutX(i * 35);
                    r.setLayoutY(0);
                }
                case "LEFT" -> {
                    r.setLayoutX(-i * 35);
                    r.setLayoutY(0);
                }
                case "DOWN" -> {
                    r.setLayoutX(0);
                    r.setLayoutY(i * 35);
                }
                case "UP" -> {
                    r.setLayoutX(0);
                    r.setLayoutY(-i * 35);
                }
            }
        }
    }

    public void previewAt(double layoutX, double layoutY) {
        if (placed) return;
        setLayoutX(layoutX);
        setLayoutY(layoutY);
        setOpacity(0.4);
    }

    public void placeAt(double layoutX, double layoutY) {
        setLayoutX(layoutX);
        setLayoutY(layoutY);
        setOpacity(1);
        placed = true;
        System.out.println("Colocado: " + ship.getName());
    }

    public void setDirection(String newDirection) {
        this.direction = newDirection;
        rotateShape();
    }

    public boolean isPlaced() {
        return placed;
    }

    public String getDirection() {
        return direction;
    }

    public IShip getShip() {
        return ship;
    }


}

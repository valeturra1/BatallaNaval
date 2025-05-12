package com.example.batallanaval.controller;

import com.example.batallanaval.view.GameView;
import com.example.batallanaval.view.ShipView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private GameView view;
    private static final int TILE_SIZE = 35;
    private static final int GRID_SIZE = 10;
    private final List<PlacedShip> placedShips = new ArrayList<>();

    public void init(Stage stage) {
        view = new GameView();
        view.setOnShipPlacementAttempt(this::tryPlaceShip);
        view.setupUI(stage);
    }

    private boolean tryPlaceShip(ShipView shipView, double sceneX, double sceneY) {
        // Calculamos las posiciones basadas en el TILE_SIZE
        int col = (int)(sceneX / TILE_SIZE);
        int row = (int)(sceneY / TILE_SIZE);
        String dir = shipView.getDirection();
        int size = shipView.getShip().getSize();

        // Verificamos si la colocación está dentro de los límites
        if (!isWithinBounds(row, col, size, dir)) return false;
        // Verificamos si hay solapamiento con otras piezas
        if (isOverlapping(row, col, size, dir)) return false;

        // Colocamos el barco
        placedShips.add(new PlacedShip(row, col, size, dir));
        return true;
    }

    private boolean isWithinBounds(int row, int col, int size, String dir) {
        switch (dir) {
            case "RIGHT":
                return col + size <= GRID_SIZE;
            case "LEFT":
                return col - size + 1 >= 0;
            case "DOWN":
                return row + size <= GRID_SIZE;
            case "UP":
                return row - size + 1 >= 0;
            default:
                return false;
        }
    }

    private boolean isOverlapping(int row, int col, int size, String dir) {
        for (PlacedShip s : placedShips) {
            for (int i = 0; i < size; i++) {
                int r = row, c = col;
                switch (dir) {
                    case "RIGHT" -> c += i;
                    case "LEFT" -> c -= i;
                    case "DOWN" -> r += i;
                    case "UP" -> r -= i;
                }
                if (s.occupies(r, c)) return true;
            }
        }
        return false;
    }

    private static class PlacedShip {
        final List<int[]> positions = new ArrayList<>();

        public PlacedShip(int row, int col, int size, String dir) {
            for (int i = 0; i < size; i++) {
                switch (dir) {
                    case "RIGHT" -> positions.add(new int[]{row, col + i});
                    case "LEFT" -> positions.add(new int[]{row, col - i});
                    case "DOWN" -> positions.add(new int[]{row + i, col});
                    case "UP" -> positions.add(new int[]{row - i, col});
                }
            }
        }

        public boolean occupies(int r, int c) {
            return positions.stream().anyMatch(pos -> pos[0] == r && pos[1] == c);
        }
    }
}
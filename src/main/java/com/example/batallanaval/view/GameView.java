package com.example.batallanaval.view;

import com.example.batallanaval.model.*;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.*;

public class GameView {

    // Interfaz para la validación de colocación de los barcos
    public interface ShipPlacementValidator {
        boolean canPlaceShip(ShipView ship, double layoutX, double layoutY);
    }

    private ShipPlacementValidator placementValidator;
    private static final int SIZE = 10;  // Tamaño del tablero
    private static final int TILE_SIZE = 35;  // Tamaño de cada celda del tablero
    private final Map<String, Cell> playerCells = new HashMap<>();
    private final Map<String, Cell> enemyCells = new HashMap<>();
    private GridPane playerGrid;
    private final List<ShipView> fleetList = new ArrayList<>();
    private final List<ShipView> placedShips = new ArrayList<>();
    private ShipView selectedShip = null;
    private VBox fleetMenu;
    private Pane overlay;

    public void setupUI(Stage stage) {
        BorderPane layout = new BorderPane();
        HBox boards = new HBox(40);
        fleetMenu = new VBox(10);
        fleetMenu.setStyle("-fx-padding: 10; -fx-border-color: black;");
        fleetMenu.setPrefWidth(220);

        overlay = new Pane();
        overlay.setPickOnBounds(false);

        createFleet();

        VBox positionBoard = createBoard("Tablero de posición", true);
        VBox mainBoard = createBoard("Tablero principal", false);

        boards.getChildren().addAll(positionBoard, mainBoard);
        StackPane centerStack = new StackPane();
        centerStack.getChildren().addAll(boards, overlay);

        layout.setLeft(fleetMenu);
        layout.setCenter(centerStack);

        Scene scene = new Scene(layout);
        scene.setOnKeyPressed(e -> {
            if (selectedShip != null) {
                KeyCode code = e.getCode();
                switch (code) {
                    case RIGHT -> selectedShip.setDirection("RIGHT");
                    case LEFT -> selectedShip.setDirection("LEFT");
                    case UP -> selectedShip.setDirection("UP");
                    case DOWN -> selectedShip.setDirection("DOWN");
                }
            }
        });

        stage.setTitle("Battleship Game");
        stage.setScene(scene);
        stage.setMinWidth(1200);
        stage.setMinHeight(750);
        stage.show();

        scene.setOnMouseMoved(this::handleMouseMoveOnGrid);
        scene.setOnMouseClicked(this::handleClickOnGrid);
    }

    public void setOnShipPlacementAttempt(ShipPlacementValidator validator) {
        this.placementValidator = validator;
    }

    private void createFleet() {
        fleetMenu.getChildren().add(new Label("Flota"));
        IShip[] ships = {
                new Carrier(), new Submarine(), new Submarine(),
                new Destroyer(), new Destroyer(), new Destroyer(),
                new Frigate(), new Frigate(), new Frigate(), new Frigate()
        };

        for  (int i = 0; i < ships.length; i++) {
            IShip model = ships[i];
            ShipView ship = new ShipView(model);
            fleetList.add(ship);
            fleetMenu.getChildren().add(ship);

            ship.setOnMouseClicked(e -> {
                e.consume();
                if (overlay.getChildren().contains(ship)) return;
                selectedShip = ship;
                if (!overlay.getChildren().contains(ship)) overlay.getChildren().add(ship);
                ship.requestFocus();
            });
        }
    }

    private void handleMouseMoveOnGrid(MouseEvent e) {
        if (selectedShip == null) return;

        Point2D local = overlay.sceneToLocal(e.getSceneX(), e.getSceneY());
        double x = Math.floor(local.getX() / TILE_SIZE) * TILE_SIZE;
        double y = Math.floor(local.getY() / TILE_SIZE) * TILE_SIZE;

        selectedShip.previewAt(x, y);
    }

    private void handleClickOnGrid(MouseEvent e) {
        if (selectedShip == null) return;

        Point2D local = overlay.sceneToLocal(e.getSceneX(), e.getSceneY());
        double x = Math.floor(local.getX() / TILE_SIZE) * TILE_SIZE;
        double y = Math.floor(local.getY() / TILE_SIZE) * TILE_SIZE;

        if (placementValidator != null &&
                !placementValidator.canPlaceShip(selectedShip, x, y)) {
            return;
        }

        selectedShip.placeAt(x, y);
        selectedShip.setOpacity(1);
        placedShips.add(selectedShip);
        selectedShip = null;

        fleetMenu.getChildren().removeIf(node -> node instanceof ShipView && ((ShipView) node).isPlaced());
    }

    private VBox createBoard(String title, boolean isPlayer) {
        VBox box = new VBox(10);
        Label label = new Label(title);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        playerGrid = new GridPane();

        for (int col = 0; col < SIZE; col++) {
            Label lbl = new Label(Character.toString((char) ('A' + col)));
            lbl.setPrefSize(TILE_SIZE, TILE_SIZE);
            lbl.setAlignment(Pos.CENTER);
            playerGrid.add(lbl, col + 1, 0);
        }

        for (int row = 0; row < SIZE; row++) {
            Label lbl = new Label(String.valueOf(row + 1));
            lbl.setPrefSize(TILE_SIZE, TILE_SIZE);
            lbl.setAlignment(Pos.CENTER);
            playerGrid.add(lbl, 0, row + 1);
        }

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Cell cell = new Cell();
                playerGrid.add(cell, col + 1, row + 1);
                String key = (char) ('A' + col) + String.valueOf(row + 1);
                if (isPlayer) {
                    playerCells.put(key, cell);
                } else {
                    enemyCells.put(key, cell);
                }
            }
        }

        box.getChildren().addAll(label, playerGrid);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    public static class Cell extends StackPane {
        private final Rectangle background = new Rectangle(TILE_SIZE, TILE_SIZE);

        public Cell() {
            background.setFill(Color.LIGHTBLUE);
            background.setStroke(Color.BLACK);
            getChildren().add(background);
        }
    }

    public List<ShipView> getPlacedShips() {
        return placedShips;
    }

    public GridPane getPlayerGrid() {
        return playerGrid;
    }
}

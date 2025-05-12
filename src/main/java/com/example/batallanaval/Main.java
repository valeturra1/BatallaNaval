package com.example.batallanaval;

import com.example.batallanaval.controller.GameController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        GameController controller = new GameController();
        controller.init(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

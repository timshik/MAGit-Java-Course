package UI;

import backend.GitManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainFx extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        GitManager manager = new GitManager();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FxmlFiles/EntryController.fxml"));
        Pane root = loader.load();
        EntryController EntryController = loader.getController();
        EntryController.Setmanager(manager); // might be redundent
        EntryController.setPrimaryStage(primaryStage);
        primaryStage.setTitle("Amazinggg Git");
        primaryStage.setScene(new MyScene(root));
        primaryStage.show();
    }

    }


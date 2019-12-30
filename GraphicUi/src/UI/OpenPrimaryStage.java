package UI;

import backend.GitManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class OpenPrimaryStage {

    private Stage primaryStage;
    private GitManager manager;
    public void setManager(GitManager manager) {
        this.manager = manager;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    public void show() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsToFxml.magitApp));
        Pane root = loader.load();
        MagitController MainController = loader.getController();
        MainController.setManager(manager);
        MainController.setPrimaryStage(primaryStage);
        primaryStage.setTitle("Amazinggg Git");
        Scene scene = new MyScene(root);
        MainController.setScene(scene); // במקום לרשום new Scene רושמים new MyScene
        primaryStage.setScene(scene);
        MainController.StartAllData();
        primaryStage.show();
    }
}




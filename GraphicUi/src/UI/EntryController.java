package UI;

import backend.GitManager;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class EntryController {
    private GitManager manager;
    private OpenPrimaryStage openPrimaryStage = new OpenPrimaryStage();
    private Stage primaryStage;

    @FXML
    public void ImportRepositoryFromXml() {
        File chosenFile = StaticUi.FileChooser();
        if (chosenFile != null) {
            try {
                if (!manager.CheckIfXmlValid(chosenFile.getPath())) {
                    BooleanProperty booleanProperty = new SimpleBooleanProperty();
                    booleanProperty.addListener(((observable, oldValue, newValue) -> {
                        try {
                            manager.ReadRepositoryFile(chosenFile.getPath(), true);
                            showPrimaryStage();
                        } catch (Exception e) {
                            StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                        }
                    }));
                    StaticUi.yesOrNoAlert(" the path entered is a path to  exist repository would you like to override? ", booleanProperty);
                } else {
                    manager.ReadRepositoryFile(chosenFile.getPath(), false);
                    showPrimaryStage();
                }

            } catch (Exception e) {
                StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());////
            }
        }

    }

    @FXML
    public void LoadRepository()  {
        File chosenFile = StaticUi.DirectoryChooser();
        if (chosenFile != null) {
            try {
                manager.SwitchRepository(chosenFile.getPath()); // if user will close the window without choosing there will be a problem
                showPrimaryStage();
            } catch (Exception e) {
                StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
            }
        }

    }

    @FXML
    public void BuildRepository() {
        File chosenFile = StaticUi.DirectoryChooser();
        if (chosenFile != null) {
            StringProperty sp = new SimpleStringProperty();
            sp.addListener(((observable, oldValue, newValue) -> {
                new Thread(() -> {
                    try {
                        manager.Build(chosenFile.getPath(), newValue);
                        Platform.runLater(() -> {
                            try {
                                showPrimaryStage();
                            } catch (Exception e) {
                                Platform.runLater(() -> StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage()));
                            }
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage()));
                    }
                }).start();
            }));
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsToFxml.namePopUp));
                StaticUi.OpenOneButtonPopUp(sp, loader, OutputStrings.repositoryname);

            } catch (Exception e) {
                StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
            }


        }


    }

    public void Setmanager(GitManager manager) {
        this.manager = manager;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void showPrimaryStage() throws Exception {
        primaryStage.close();
        openPrimaryStage.setManager(manager);
        openPrimaryStage.setPrimaryStage(primaryStage);
        openPrimaryStage.show();
    }


}

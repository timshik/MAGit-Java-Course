package UI;

import backend.BasicFile;
import backend.Blob;
import backend.GitManager;
import backend.StaticMethods;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ConflictHandlerController {
    @FXML
    private ListView<Blob> listOfCOnflicts;
    @FXML
    private Button doneButton;
    @FXML
    private Map<Blob,Blob[]> conflictsMap; // <newone, ancestor theirs ours>
    @FXML
    private javafx.scene.control.TextArea TextArea;

    public void initialize() {
    }
    private StringProperty sp = new SimpleStringProperty(); //go through the list and delete the files that shouldnt be there
                                                           // delete the current Wc
                                                          // deploy the new map
                                                         // make the new commit with the two maps (ours and new)

    private List<Blob> filesToDelete;
    public void setFilesToDelete(List<Blob> filesToDelete)
    {
        this.filesToDelete = filesToDelete;
    }
    private GitManager manager;
    public void setManager (GitManager manager)
    {
        this.manager = manager;
    }
    public void setStringPropery(StringProperty sp)
    {
        this.sp=sp;
    }

    public void setConflictsMap(Map<Blob, Blob[]> conflictsMap) {
        this.conflictsMap = conflictsMap;
    }
    public void InitializeData()
    {
     for(Blob blob : conflictsMap.keySet())
     {
         listOfCOnflicts.getItems().add(blob);
     }
     if(conflictsMap.size() == 0)
     {
         doneButton.setDisable(false);
     }
    }


    @FXML
    void DoneCnflicts(ActionEvent event) {
        if(TextArea.getText().equals("")) {
            StaticUi.ShowError(Alert.AlertType.WARNING , OutputStrings.noMassageEntered);
        }
        else
        {
            sp.setValue(TextArea.getText());
            ((Node)event.getSource()).getScene().getWindow().hide();
        }

    }
    @FXML
    void Conflictchosen(MouseEvent event) throws IOException {
     if(event.getClickCount() == 2)
     {
         Blob newFile = listOfCOnflicts.getSelectionModel().getSelectedItem();
         ObjectProperty op = new SimpleObjectProperty();  //needs to be blob property
         op.addListener(((observable, oldValue, newValue) ->
         {
             if (((Blob)newValue).getName().equals("notExist")) // if someone will call his file with that name the program will delete it
             {
                 filesToDelete.add(newFile);
             }
             else{
                 Blob selectedBlob = ((Blob)newValue);
                 StaticMethods.CopyBlob(newFile ,selectedBlob);
             }

             listOfCOnflicts.getItems().remove(newFile);// and then remove the line that was chosen
             if(listOfCOnflicts.getItems().size() == 0)  // check if list view is empty if its empty activate the button done
             {
                 doneButton.setDisable(false);
             }

         }));
         FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsToFxml.conflictPopup));
         Pane root = loader.load();
         ConflictPopUpController conflictPopUpController = loader.getController();
         Stage stage = new Stage();
         conflictPopUpController.setConflict(conflictsMap.get(newFile));
         conflictPopUpController.setManager(manager);
         conflictPopUpController.setObjectPropery(op);
         stage.setTitle("Conflict");
         stage.setScene(new MyScene(root));
         conflictPopUpController.initializeData();
         stage.show();
     }
    }

}


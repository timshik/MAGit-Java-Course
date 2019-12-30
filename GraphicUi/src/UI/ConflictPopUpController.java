package UI;

import backend.Blob;
import backend.GitManager;
import backend.StaticMethods;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;



public class ConflictPopUpController {
    @FXML
    private TextArea ancestorTextArea;

    @FXML
    private TextArea theirsTextArea;

    @FXML
    private TextArea oursTextArea;

    @FXML
    private TextArea yourFileTextArea;

    @FXML
    private Button submitButton;

    @FXML
    private RadioButton ancestorRadioButton;

    @FXML
    private ToggleGroup whichFile;

    @FXML
    private RadioButton theirsRadioButton;

    @FXML
    private RadioButton oursRadioButton;

    @FXML
    private RadioButton yourfileRadioButton;

    private ObjectProperty op = new SimpleObjectProperty(); // blob property
    private  Blob[] conflict;
    private GitManager manager;
    public void setManager (GitManager manager)
    {
        this.manager = manager;
    }
    public void setConflict( Blob[] conflict) {
        this.conflict = conflict;
    }

    public void initialize()
    {

    }
    public void setObjectPropery(ObjectProperty op)
    {
        this.op=op;
    }
    @FXML
     public void SetConflict(ActionEvent event) {
       Toggle tempToggle = whichFile.getSelectedToggle();
       Blob fileChosen ;
       if(tempToggle.equals(ancestorRadioButton))
       {
           fileChosen = conflict[0];
       }
        else if(tempToggle.equals(theirsRadioButton))
        {
            fileChosen = conflict[1];
        }
        else if(tempToggle.equals(oursRadioButton))
        {
            fileChosen = conflict[2];
        }
        else
        {
           fileChosen = new Blob(conflict[0].getPath(),yourFileTextArea.getText(),conflict[0].getName(),manager.getCurrentUser().getUsername(), StaticMethods.GetCurrentDate());
        }
        if(fileChosen == null)
        {
            fileChosen = new Blob("","","notExist","",StaticMethods.GetCurrentDate());
        }
        op.setValue(fileChosen);
        ((Node)event.getSource()).getScene().getWindow().hide();

    }

    public void initializeData() {
        if(conflict[0] != null)
        {
            ancestorTextArea.setText(conflict[0].getContent());
        }
        else
        {
            ancestorTextArea.setText("File not Exist");
        }
        if(conflict[1] != null)
        {
            theirsTextArea.setText((conflict[1].getContent()));
        }
        else
        {
            theirsTextArea.setText("File not Exist");
        }
        if(conflict[2] != null)
        {
            oursTextArea.setText(conflict[2].getContent());
        }
        else
        {
            oursTextArea.setText("File not Exist");
        }
        whichFile.selectToggle(yourfileRadioButton);

    }
}



package UI;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

import java.util.List;

public class ChooseBranchController {

    @FXML
    private Label branchLabel;
    public void SetProperty(StringProperty sp) {
        this.sp = sp;
    }
    @FXML
    private ChoiceBox<String> branchChoiceBox;

    @FXML
    private Button branchSubmit;
    private StringProperty sp;
    @FXML
    void submit(ActionEvent event) {
        sp.setValue(branchChoiceBox.getValue());
        ((Node)event.getSource()).getScene().getWindow().hide();
    }
   public void initialize()
   {}
   public void initializedata(List <String> branchesToShow, String  labelText)
    {
        branchChoiceBox.getItems().addAll( branchesToShow);
        branchLabel.setText(labelText);
    }
}



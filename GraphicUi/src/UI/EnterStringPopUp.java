
package UI;


import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.event.ActionEvent;

public class EnterStringPopUp {
    private StringProperty objectsToChangeName;// set
    @FXML
    private TextField PopUpNameTextFieldfx;

    @FXML
    private Button popupNameButtonFx;
    @FXML
    private Label PopUpNameLabelfx;

    @FXML
    public void submit(ActionEvent event) {

        objectsToChangeName.setValue(PopUpNameTextFieldfx.getText());
        ((Node)event.getSource()).getScene().getWindow().hide();
    }

    public void SetProperty(StringProperty name) {
        objectsToChangeName = name;
    }
    public void SetLabel(String label)
    {
        PopUpNameLabelfx.setText(label);
    }

    public void Initilize() {
    }
}




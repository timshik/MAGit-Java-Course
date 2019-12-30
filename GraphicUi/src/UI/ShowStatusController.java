package UI;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.text.TextFlow;

import java.awt.event.ActionEvent;
import java.util.List;

public class ShowStatusController {


    @FXML
    private ListView<String> listViewChanged;

    @FXML
    private ListView<String> listViewAdded;

    @FXML
    private ListView<String> listViewDeleted;

    @FXML
    private TextArea textAreaShowStatus;

    @FXML
    private Button CommitButton;

    private StringProperty sp = new SimpleStringProperty();
    public void setStringPropery(StringProperty sp)
    {
        this.sp=sp;
    }

 public void Initialize(){

 }
 public void setTextFlows (List<String>[] changedItems)//////////////
 {

     for(int i=0; i< changedItems.length; i++)
     {

         {

             if (i==0)
             {
                 if(changedItems[i].size()==0)
                 {
                     listViewChanged.getItems().add ("no changes in this category");
                 }
                 else
                 {
                     listViewChanged.getItems().addAll(changedItems[i]);
                 }
             }
             if (i==1)
             {
                 if(changedItems[i].size()==0)
                 {
                    listViewAdded.getItems().add("no changes in this category");
                 }
                 else
                 {
                     listViewAdded.getItems().addAll(changedItems[i]);
                 }
             }
             if (i==2)
             {
                 if(changedItems[i].size()==0)
                 {
                    listViewDeleted.getItems().add("no changes in this category");
                 }
                 else
                 {
                     listViewDeleted.getItems().addAll(changedItems[i]);
                 }
             }
         }

     }
 }
//  @FXML
//   public void commit(ActionEvent event) {
//
//
//
//    }
   @FXML
    public void commit(javafx.event.ActionEvent actionEvent) {
        if(textAreaShowStatus.getText().equals("")) {
            StaticUi.ShowError(Alert.AlertType.WARNING , OutputStrings.noMassageEntered);
        }
        else{
            sp.setValue(textAreaShowStatus.getText());
            ((Node)actionEvent.getSource()).getScene().getWindow().hide();
        }
    }
}



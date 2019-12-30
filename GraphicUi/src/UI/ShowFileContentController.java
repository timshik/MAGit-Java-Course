package UI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ShowFileContentController {


    @FXML
    private Label lastUpdatedLabel;

    @FXML
    private Label UpdaterLabel;

    @FXML
    private TextArea textAreaShowContent;

    @FXML
    private Label nameLabel;

   public void initialize()
   {}


    public void setText(String content)
    {
        textAreaShowContent.setText(content);
    }
    public void setLabels (String name , String lastUpdated, String updater)
    {
        nameLabel.setText(name);
        lastUpdatedLabel.setText(lastUpdated);
        UpdaterLabel.setText(updater);
    }
}

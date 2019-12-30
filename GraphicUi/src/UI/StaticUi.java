package UI;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

//import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class StaticUi {
    public static File FileChooser() {
        FileChooser fc = new FileChooser();
        return fc.showOpenDialog(null);
    }
    public static File DirectoryChooser()
    {
        DirectoryChooser dc = new DirectoryChooser();
        return dc.showDialog(null);
    }
    public static void ShowError(Alert.AlertType icon,String massage)
    {
        Alert alert = new Alert(icon);
        alert.setTitle("Alert");
        alert.setContentText(massage);

        alert.showAndWait();

    }
    public static void yesOrNoAlert(String text, BooleanProperty booleanProperty) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Choise popup");
        alert.setContentText(text);
        List<ButtonType> buttons = new LinkedList<>();
        buttons.add(new ButtonType("NO", ButtonBar.ButtonData.NO));
        buttons.add(new ButtonType("YES", ButtonBar.ButtonData.YES));
        alert.getButtonTypes().setAll(buttons);
        alert.showAndWait().ifPresent(type -> {
            if (type.getButtonData() == ButtonBar.ButtonData.YES) {
                booleanProperty.setValue(true);
            }
        });
    }
    public static void YesOrNoAlertWithNofunction (String text, BooleanProperty yesBooleanProperty,BooleanProperty noBooleanProperty)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Choise popup");
        alert.setContentText(text);
        List<ButtonType> buttons = new LinkedList<>();
        buttons.add(new ButtonType("NO", ButtonBar.ButtonData.NO));
        buttons.add(new ButtonType("YES", ButtonBar.ButtonData.YES));
        alert.getButtonTypes().setAll(buttons);
        alert.showAndWait().ifPresent(type -> {
            if (type.getButtonData() == ButtonBar.ButtonData.YES) {
                yesBooleanProperty.setValue(true);
            }
            else
            {
                noBooleanProperty.setValue(true);
            }
        });
    }
    static void OpenOneButtonPopUp (StringProperty sp, FXMLLoader loader,String label) throws IOException {
            Pane root = loader.load();
            EnterStringPopUp nameController = loader.getController();
            nameController.SetProperty(sp);
            nameController.SetLabel(label);
            Stage NamePopUp = new Stage();
            NamePopUp.setTitle("Magit");
            NamePopUp.setScene(new MyScene(root));
            NamePopUp.show();
        }
        static void OpenBranchChooser(StringProperty sp, FXMLLoader loader,List<String> branchesToShow,String textLabel) throws IOException {
            Pane root = loader.load();
            ChooseBranchController branchController = loader.getController();
            branchController.SetProperty(sp);
            branchController.initializedata(branchesToShow, textLabel);
            Stage NamePopUp = new Stage();
            NamePopUp.setTitle("Magit");
            NamePopUp.setScene(new MyScene(root));
            NamePopUp.show();
        }

    static List<ClassForTableView> StringsToTableClass(List<String> details)
    {
       List <ClassForTableView> tableInfo = new ArrayList<>();
        for(String info : details)
        {
            String[] strings = info.split(",");
            tableInfo.add(new ClassForTableView(strings));
        }
        return tableInfo;
    }

    public static void editCustomCSSFile(Color color) throws IOException {
        InputStream in = new FileInputStream(new File(new File("").getAbsolutePath() + MyScene.cssStyleSheet));
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;


        try {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while ((line = reader.readLine()) != null) {
                if (i == 2) {
                    String standard = getColorCode(color);
                    line = "    -standard                    : " + standard + ";";
                } else if (i == 3) {
                    String light = getColorCode(color.brighter());
                    line = "    -bright                    : " + light + ";";
                }
                else if (i==73 )
                {
                   String font = null;
                   if(color.equals( new Color (1.0,0.0,0.0,1.0)))
                   {
                      font = "AR BLANCA";
                   }
                   else if(color.equals( new Color (0.0,0.0,1.0,1.0)))
                   {
                       font = "AR JULIAN";
                   }
                   else if(color.equals( new Color (0.0,1.0,0.0,1.0)))
                   {
                        font = "AGENCY FB";
                   }
                   else
                   {
                        font = " Roboto Light";
                   }
                    line = "-fx-font-family      :\""+  font +  "\";" ;
                }

                else if (i==74)
                {
                    double fontStyle;
                    if(color.equals( new Color (1.0,0.0,0.0,1.0)))
                    {
                        fontStyle = 3;
                    }
                    else if(color.equals( new Color (0.0,0.0,1.0,1.0)))
                    {
                        fontStyle = 1.2;
                    }
                    else if(color.equals( new Color (0.0,1.0,0.0,1.0)))
                    {
                        fontStyle = 0.7;
                    }
                    else
                    {
                        fontStyle = 0;
                    }
                    line = "-fx-background-radius :" + fontStyle +  "em;" ;
                }
                sb.append(line).append("\n");
                i++;
            }
            PrintWriter writer =
                    new PrintWriter(
                            new File(new File("").getAbsolutePath() + MyScene.cssStyleSheet));
                            //new File(StaticUi.class.getResource(MyScene.cssStyleSheet).getPath()));
            writer.write(sb.toString());
            writer.close();
        } catch (IOException ignored) {

        }
    }

    private static String getColorCode(Color color) {
        return "#" + color.toString().substring(2, 8);
    }




//     public static void ShowEror(String eror) throws IOException {
//            FXMLLoader loader =  new FXMLLoader(getClass().getResource("UI.ErorPopUp.fxml"));
//            Pane root = loader.load();
//            UI.ErorPopUp ErorController = loader.getController();
//            ErorController.setText(eror);
//            Stage stage = new Stage();
//            stage.setScene(new Scene(root));
//            stage.show();
//        }

    }


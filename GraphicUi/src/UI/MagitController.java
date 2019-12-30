package UI;

import backend.*;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MagitController {
    @FXML
    private VBox commitTreeVBox;
    @FXML
    private HBox branchMap;

    @FXML
    private AnchorPane commitTreeScrollPane;
    @FXML
    private ScrollPane branchNameScrollPane;
    @FXML
    private MenuBar MenuBarFx;

    @FXML
    private Menu MenuRepositoryfx;

    @FXML
    private MenuItem MenuItemNewRepositoryFx;

    @FXML
    private MenuItem MenuItemSwitchRepositoryFx;

    @FXML
    private MenuItem MenuItemImportFx;

    @FXML
    private MenuItem MenuItemExportFx;

    @FXML
    private MenuItem MenuItemExitFx;

    @FXML
    private Menu MenuEditFx;

    @FXML
    private MenuItem MenuItemChangeUserNameFx;

    @FXML
    private Menu MenuViewFx;

    @FXML
    private MenuItem menuItemClone;

    @FXML
    private MenuItem menuItemFetch;

    @FXML
    private MenuItem menuItemPull;

    @FXML
    private MenuItem menuItemPush;

    @FXML
    private Menu MenuBranchesFx;

    @FXML
    private MenuItem MenuItemAddBranchFx;

    @FXML
    private MenuItem MenuItemDeleteBranchFx;

    @FXML
    private MenuItem MenuItemCheckoutBranch;

    @FXML
    private MenuItem MenuItemResetActiveBranchFx;


    @FXML
    private ColorPicker ComboBoxMasterFx;

    @FXML
    private MenuButton MenuButtonCheckoutFx;

    @FXML
    private TextField TextFieldFx;


    @FXML
    private ProgressIndicator ProgressIndicatorFx;

    @FXML
    private ProgressBar ProgressBarFx;
    @FXML
    private ChoiceBox<String> ChoiceBoxFx;
    @FXML
    private TableView<ClassForTableView> TableViewFx;

    @FXML
    private TableColumn<ClassForTableView, String> branchColumnFx;

    @FXML
    private TableColumn<ClassForTableView, String> noteColumnFx;

    @FXML
    private TableColumn<ClassForTableView, String> dateColumnFx;

    @FXML
    private TableColumn<ClassForTableView, String> shawanColumnFx;
    @FXML
    private TreeView<BasicFile> filesTreeOfTheCmmitTreeView;

    @FXML
    private ListView<String> historyOfBranchTableView;
    @FXML
    private TextFlow contentTextFlow;
    @FXML
    private Button mergeButton;
    @FXML
    private GitManager manager;
    @FXML
    private Label repoNameLabel;

    @FXML
    private Label repoPathLabel;

    private StringProperty sp;
    private Stage primaryStage;
    private TreeViewOfRepository treeManager;
    private CommitTree commitTree;
    private Scene currentScene;
    @FXML
    private ListView<String> firstDeltaListView;

    @FXML
    private ListView<String> secondDeltaListView;

    @FXML
    private Button commitTreeButton;
    private boolean fullCommitTree =false;
    @FXML
    private MenuItem animationMenuItem;
    private double duration = 2000;

    @FXML
    public void initialize() {
        branchColumnFx.setCellValueFactory(new PropertyValueFactory<ClassForTableView, String>("branchName"));
        noteColumnFx.setCellValueFactory(new PropertyValueFactory<ClassForTableView, String>("commitNote"));
        dateColumnFx.setCellValueFactory(new PropertyValueFactory<ClassForTableView, String>("commitLastUpdated"));
        shawanColumnFx.setCellValueFactory(new PropertyValueFactory<ClassForTableView, String>("commitShawan"));
        branchNameScrollPane.setVvalue(0.7);
    }

    public void setScene(Scene scene) {
        this.currentScene = scene;
    }

    public void StartAllData () {
        try {
            setChoiceBox();
            ChoiceBoxFx.setOnAction((event) ->
            {
                try {
                    if (!new File(manager.PathToActiveBranch()).getName().equals(ChoiceBoxFx.getValue()))
                        if (ChoiceBoxFx.getValue() != null) {
                            CheckoutForChoiceBox(ChoiceBoxFx.getValue());
                        }

                } catch (Exception e) {
                    StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                }
            });
            treeManager = new TreeViewOfRepository();
            setTableView();
            commitTree = new CommitTree(manager, this, commitTreeVBox, branchMap, commitTreeScrollPane, fullCommitTree, duration);
            SetInfoLabels();
        } catch (Exception e)
        {
            StaticUi.ShowError(Alert.AlertType.ERROR,e.getMessage());
        }
    }

    @FXML
    void ChangeCommitTreeStatus(ActionEvent event) {
        if(fullCommitTree)
        {
            commitTreeButton.setText("commit tree (not full)");
            fullCommitTree = false;
        }
        else
        {
            commitTreeButton.setText("commit tree (full)");
            fullCommitTree = true;
        }

        try {
            commitTree = new CommitTree(manager, this, commitTreeVBox, branchMap, commitTreeScrollPane,fullCommitTree,duration);
        } catch (Exception e) {
            StaticUi.ShowError(Alert.AlertType.ERROR,e.getMessage());
        }
    }
    @FXML
    void Animation(ActionEvent event) {
        if(duration == 0)
        {
            animationMenuItem.setText("animations (activated)");
            duration = 2000;
        }
        else
        {
            animationMenuItem.setText("animations (canceled)");
            duration = 0;
        }

        try {
            commitTree = new CommitTree(manager, this, commitTreeVBox, branchMap, commitTreeScrollPane,fullCommitTree,duration);
        } catch (Exception e) {
            StaticUi.ShowError(Alert.AlertType.ERROR,e.getMessage());
        }
    }
    public void SetInfoLabels()
    {
        repoNameLabel.setText(manager.getMyRepository().getName());
        repoPathLabel.setText(manager.getMyRepository().getPath());
    }

    public void setAllData() throws Exception {
        setChoiceBox();

        setTableView();
        commitTree = new CommitTree(manager, this, commitTreeVBox, branchMap, commitTreeScrollPane, fullCommitTree,duration);
    }

    public ObservableList<ClassForTableView> getDetailsForTable(List<ClassForTableView> tableInfo) {
        ObservableList<ClassForTableView> details = FXCollections.observableArrayList();
        for (ClassForTableView table : tableInfo) {
            details.add(table);
        }
        return details;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void setOnCssTimeChanges(ActionEvent event) throws IOException {
        StaticUi.editCustomCSSFile(ComboBoxMasterFx.getValue());
        currentScene.getStylesheets().clear();
        File externalCSS = new File(new File("").getAbsolutePath() + MyScene.cssStyleSheet);
        currentScene.getStylesheets().add(externalCSS.toURI().toString());
    }

    public void setManager(GitManager manager) {
        this.manager = manager;
    }

    public void AddToChoiceBox(String name) {
        ChoiceBoxFx.getItems().add(name);
    }

    public void DeleteFromChoiceBox(String name) {
        ChoiceBoxFx.getItems().remove(name);
    }

    public void setfilesTreeOfTheCmmitTreeView(TreeView<BasicFile> treeView) {
        filesTreeOfTheCmmitTreeView.setRoot(treeView.getRoot());
    }

    public void setChoiceBox()  {
        try {
            File[] allBranches = manager.GetAllBranches();
            List<String> nameOfAllBranches = new ArrayList<String>();
            for (File file : allBranches) {
                if (!file.getName().equals("Head")) {
                    if (manager.IsRemote()) {
                        if (!file.getName().equals(manager.GetRemoteRepoDetails().get(1))) {
                            nameOfAllBranches.add(file.getName());
                        }
                    } else {
                        nameOfAllBranches.add(file.getName());
                    }
                }


            }
            ChoiceBoxFx.getSelectionModel().clearSelection();
            ChoiceBoxFx.getItems().clear();
            ChoiceBoxFx.getItems().addAll(nameOfAllBranches);
            ChoiceBoxFx.setValue(new File(manager.PathToActiveBranch()).getName());
        }
        catch(Exception e)
        {
            StaticUi.ShowError(Alert.AlertType.ERROR,e.getMessage());
        }
    }

    public void setTableView() throws IOException {
        TableViewFx.setItems(getDetailsForTable(StaticUi.StringsToTableClass(manager.MakeDetailsForTable())));
    }

    /////////////////// button functions
    @FXML
    public void ChangeUserName() {
        StringProperty sp = new SimpleStringProperty();
        sp.addListener(((observable, oldValue, newValue) -> manager.ChangeUserName(newValue)));
        FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsToFxml.namePopUp));
        try {
            StaticUi.OpenOneButtonPopUp(sp, loader, OutputStrings.enterNamePopup);
        } catch (IOException e) {
            StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    @FXML
    public void ImportRepositoryFromXml() { // the code already exist in EntryController (beside show primary ) needs to change
        File chosenFile = StaticUi.FileChooser();
        if (chosenFile != null) {
            try {
                if (!manager.CheckIfXmlValid(chosenFile.getPath())) {
                    BooleanProperty booleanProperty = new SimpleBooleanProperty();
                    booleanProperty.addListener(((observable, oldValue, newValue) -> {
                        try {
                            manager.ReadRepositoryFile(chosenFile.getPath(), true);
                            setAllData();
                            SetInfoLabels();
                        } catch (Exception e) {
                            StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                        }
                    }));
                    StaticUi.yesOrNoAlert(" the path entered is a path to  exist repository would you like to override? ", booleanProperty);
                } else {
                    manager.ReadRepositoryFile(chosenFile.getPath(), false);
                    setAllData();
                    SetInfoLabels();
                }

            } catch (Exception e) {
                StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());////
            }
        }


    }

    @FXML
    public void SwitchRepository() {
        File chosenFile = StaticUi.DirectoryChooser();
        if (chosenFile != null) {
            try {
                manager.SwitchRepository(chosenFile.getPath());
                setAllData();
                SetInfoLabels();
            } catch (Exception e) {
                StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
            }
        }
    }

//    public List<String> ShowCurrentCommitFileSystemInfo() throws Exception { // build tree
//
//    }

    @FXML
    public void ShowStatus()  {
        try {
            StringProperty sp = new SimpleStringProperty();
            sp.addListener(((observable, oldValue, newValue) -> {
                try {
                    manager.Commit(newValue);
                    setAllData();
                } catch (Exception e) {
                    StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                }
            }));
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsToFxml.showStatusPopup));
            Pane root = loader.load();
            ShowStatusController showStatusController = loader.getController();
            showStatusController.setStringPropery(sp);
            showStatusController.setTextFlows(manager.ShowStatus());
            Stage stage = new Stage();
            stage.setTitle("show status");
            stage.setScene(new MyScene(root));
            stage.show();
        }
        catch (Exception e)
        {
            StaticUi.ShowError(Alert.AlertType.ERROR,e.getMessage());
        }
    }

    @FXML
    public void AddBranch()  {
        try {
            if (!manager.IsRemote()) {
                AddNotRTBBranch();
            } else {
                BooleanProperty yesBp = new SimpleBooleanProperty();  // if yes
                yesBp.addListener((observable -> {
                    List<String> nameOfAllBranches = null;/////
                    try {
                        nameOfAllBranches = manager.GetListOfAllRB();
                    } catch (Exception e) {
                        StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                    }
                    StringProperty sp = new SimpleStringProperty();

                    sp.addListener(((observable1, oldValue, newValue) ->
                    {
                        String name = StaticMethods.GetSuffix(newValue);
                        try {
                            String shawan = Files.readAllLines(Paths.get(manager.getMyRepository().getPathToBranches() + File.separator + newValue)).get(0);
                            manager.CreateNewBranchwithShawan(name, shawan, true);
                            setAllData();
                        } catch (Exception e) {
                            StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                        }
                    }));
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsToFxml.branchChooser));
                    try {
                        StaticUi.OpenBranchChooser(sp, loader, nameOfAllBranches, OutputStrings.chooseBranch);
                    } catch (IOException e) {
                        StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                    }


                }));

                BooleanProperty noBp = new SimpleBooleanProperty();   // if no
                noBp.addListener((observable ->
                {
                    try {
                        AddNotRTBBranch();
                    } catch (Exception e) {
                        StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                    }
                }));


                StaticUi.YesOrNoAlertWithNofunction(OutputStrings.RTBbranch, yesBp, noBp);

            }
        } catch (Exception e)
        {
            StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
        }

    }


    public void AddNotRTBBranch()  {
        try {
            StringProperty sp = new SimpleStringProperty();
            sp.addListener((observable, oldValue, newValue) -> {
                List<String> allCommits = null;
                try {
                    allCommits = manager.GetListOfAllCommits();
                } catch (IOException ex) {
                    StaticUi.ShowError(Alert.AlertType.ERROR, ex.getMessage());
                }
                StringProperty sp1 = new SimpleStringProperty();
                sp1.addListener(((observable1, oldValue1, newValue1) ->
                {
                    try {
                        manager.CreateNewBranchwithShawan(newValue, newValue1, false);
                        setAllData();
                        BooleanProperty bpForDeploy = new SimpleBooleanProperty();
                        bpForDeploy.addListener((observable2 -> {
                            try {
                                if (manager.CheckIfWcIscLean()) {
                                    manager.SwitchActiveBranch(newValue);////////
                                    manager.DeleteWc(manager.getMyRepository().getPath());
                                    manager.DeployBranch(new File(manager.getMyRepository().getPathToBranches() + File.separator + newValue));
                                    setAllData();
                                } else {
                                    StaticUi.ShowError(Alert.AlertType.WARNING, OutputStrings.cantDeploy);
                                }
                            } catch (Exception e) {
                                StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                            }
                        }));
                        StaticUi.yesOrNoAlert(OutputStrings.deployBranch, bpForDeploy);

                    } catch (Exception e) {
                        StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                    }
                }));
                FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsToFxml.branchChooser));
                try {
                    StaticUi.OpenBranchChooser(sp1, loader, allCommits, OutputStrings.chooseCommit);
                } catch (IOException e) {
                    StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                }

            });
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsToFxml.namePopUp));
            try {
                StaticUi.OpenOneButtonPopUp(sp, loader, OutputStrings.enterNamePopup);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e)
        {
            StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
        }

    }

    @FXML
    public void DeleteBrunch()  {
        try {
            List<String> nameOfAllBranches = manager.getListOfBranches();
            StringProperty sp = new SimpleStringProperty();
            sp.addListener((observable, oldValue, newValue) -> {
                try {
                    manager.DeleteBrunch(newValue);
                    setAllData();
                } catch (Exception e) {
                    StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                }
            });
            //sp.addListener((observable, oldValue, newValue) -> DeleteFromChoiceBox(newValue));
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsToFxml.branchChooser));
            try {
                StaticUi.OpenBranchChooser(sp, loader, nameOfAllBranches, OutputStrings.chooseBranch);
            } catch (IOException e) {
                StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
            }
        } catch (Exception e)
        {
            StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
        }

    }


    @FXML
    public void CheckoutBranch()  { ///
        try {
            List<String> nameOfAllBranches = manager.getListOfBranches();
            StringProperty sp = new SimpleStringProperty();
            sp.addListener((observable, oldValue, newValue) -> {
                try {
                    CheckoutForChoiceBox(newValue);
                } catch (Exception e) {
                    StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                }
            });

            {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsToFxml.branchChooser));
                try {
                    StaticUi.OpenBranchChooser(sp, loader, nameOfAllBranches, OutputStrings.chooseBranch);
                } catch (IOException e) {
                    StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                }
            }
        }
        catch (Exception e)
        {
            StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    public void CheckoutForChoiceBox(String name) {
        try {
            if (!manager.CheckIfWcIscLean()) {
                BooleanProperty sbyes = new SimpleBooleanProperty();
                sbyes.addListener(observable -> {
                    try {
                        manager.CheckoutBranch(name);
                        setChoiceBox(); // no need to set the table or the commit tree ( i think )
                    } catch (Exception e) {
                        StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                    }
                });
                BooleanProperty sbno = new SimpleBooleanProperty();
                sbno.addListener((observable -> {
                    try {
                        setChoiceBox();
                    } catch (Exception e) {
                        StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                    }
                }));
                StaticUi.YesOrNoAlertWithNofunction(OutputStrings.changesOnWc, sbyes,sbno);
            } else {
                manager.CheckoutBranch(name);
                setChoiceBox();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void CreateNewRepository() {  // the code already exist in EntryController (beside show primary ) needs to change
        File chosenFile = StaticUi.DirectoryChooser();
        if (chosenFile != null) {
            StringProperty sp = new SimpleStringProperty();
            sp.addListener(((observable, oldValue, newValue) -> {
                try {
                    manager.Build(chosenFile.getPath(), newValue);
                    setAllData();
                    SetInfoLabels();

                } catch (Exception e) {
                    StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                }
            }));
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsToFxml.namePopUp));
                StaticUi.OpenOneButtonPopUp(sp, loader, OutputStrings.repositoryname);

            } catch (Exception e) {
                StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
            }
        }
    }

    @FXML
    public void SwitchActiveBranch()  {
        try {
            List<String> allcommits = manager.GetListOfAllCommits();
            File file = new File(manager.PathToActiveBranch());
            String shawan = manager.GetShawanOfBranch(new File(manager.PathToActiveBranch()).getPath());
            if (!shawan.equals("Null") & allcommits.contains(shawan)) {
                allcommits.remove(shawan);
            }
            StringProperty sp = new SimpleStringProperty();
            sp.addListener((observable, oldValue, newValue) -> {
                try {
                    manager.CheckoutBranch(manager.GetTheCommit(newValue));
                    setAllData();
                } catch (Exception e) {
                    StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                }
            });
            try {
                if (!manager.CheckIfWcIscLean()) {
                    BooleanProperty sb = new SimpleBooleanProperty();
                    sb.addListener((observable ->
                    {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsToFxml.branchChooser));
                        try {
                            StaticUi.OpenBranchChooser(sp, loader, allcommits, OutputStrings.chooseCommit);
                        } catch (IOException e) {
                            StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                        }
                    }));
                    StaticUi.yesOrNoAlert(OutputStrings.changesOnWc, sb);
                } else {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsToFxml.branchChooser));
                    try {
                        StaticUi.OpenBranchChooser(sp, loader, allcommits, OutputStrings.chooseCommit);
                    } catch (IOException e) {
                        StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                    }
                }

            } catch (Exception e) {
                StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
            }
        } catch (Exception e)
        {
            StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
        }

    }


    @FXML
    public void ExportDataToXml() {
        File chosenFile = StaticUi.DirectoryChooser();
        if (chosenFile != null) {
            try {
                manager.ExportDataToXml(chosenFile.getPath());
                setAllData();
                SetInfoLabels();
            } catch (Exception e) {
                StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
            }
        }
    }

    @FXML
    void SetDetailsOfCommitAndBranch(MouseEvent event)  {
        try {
            if (TableViewFx.getSelectionModel().getSelectedItem() == null) {
                return;
            }
            String shawanOfCommit = TableViewFx.getSelectionModel().getSelectedItem().getCommitShawan();
            updateCommitDetails(shawanOfCommit);
            historyOfBranchTableView.getItems().clear();
            if (!TableViewFx.getSelectionModel().getSelectedItem().getBranchName().equals(OutputStrings.emptyCell)) {
                if (shawanOfCommit.equals(OutputStrings.emptyCell)) {
                    historyOfBranchTableView.getItems().addAll(OutputStrings.branchWarning);
                } else {
                    historyOfBranchTableView.getItems().addAll(manager.getBranchHistory(shawanOfCommit));
                }

            }
        } catch (Exception e)
        {
            StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    public void updateCommitDetails(String shawanOfCommit)  {
        try {
            contentTextFlow.getChildren().clear();
            filesTreeOfTheCmmitTreeView.setRoot(null);/////
            if (shawanOfCommit.equals(OutputStrings.emptyCell)) {
                historyOfBranchTableView.getItems().addAll(OutputStrings.branchWarning);
            } else {
                Text text = new Text(manager.ShowDetailsOfCommit(shawanOfCommit));
                contentTextFlow.getChildren().addAll(text);
                // one line
                Folder treeFromCommit = (Folder) manager.GetPreviousWc
                        (manager.getWcPointedFromCommit(shawanOfCommit), manager.getMyRepository().getPathToObject()
                                , StaticMethods.GetSuffix(manager.getMyRepository().getPath()), manager.getMyRepository().getPath());

                setfilesTreeOfTheCmmitTreeView(treeManager.getTreeFromCommit(treeFromCommit));
                // end of line

                // start of Diff
                String[] commitDetails = Files.readAllLines(Paths.get(manager.getMyRepository().getPathToObject() + File.separator + shawanOfCommit)).get(0).split(",");
                List<String>[] diff1 = manager.GetDiff(commitDetails[0], commitDetails[1]);
                List<String>[] diff2 = manager.GetDiff(commitDetails[0], commitDetails[5]);
                firstDeltaListView.getItems().clear();
                secondDeltaListView.getItems().clear();
                firstDeltaListView.getItems().addAll(SetDiffListView(diff1));
                secondDeltaListView.getItems().addAll(SetDiffListView(diff2));
            }
        }catch(Exception e)
        {
            StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    private List<String> SetDiffListView( List<String>[] diff1) {

        List<String>  delta = new ArrayList<>();
        if (diff1.length ==1)
        {
            delta.add("this prev commit doesnt exist");
            return delta;
        }
        else if(diff1[0].size()==0 & diff1[1].size()==0 & diff1[2].size()==0)
        {
            delta.add("no changes");
        }
        for(int i=0; i< diff1.length; i++)
        {
            if( diff1[i].size()!=0)
            {
                if (i==0){delta.add(" the changed items are:");}
                if (i==1){delta.add(" the added items are:");}
                if (i==2){delta.add(" the deleted items are:");}
            }

            for(int j=0; j<diff1[i].size(); j++)
            {
                delta.add(diff1[i].get(j));
            }
        }
        return delta;
    }

    @FXML
    public void ShowFile(MouseEvent event)  {
        try {
            if (event.getClickCount() == 2) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsToFxml.showContentOfFile));
                Pane root = loader.load();
                ShowFileContentController showFileContent = loader.getController();
                setShowContent(showFileContent);
                Stage stage = new Stage();
                stage.setTitle("Magit");
                stage.setScene(new MyScene(root)); ///
                stage.show();


            }
        }catch(Exception e)
        {
            StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    public void setShowContent(ShowFileContentController controller) {
        BasicFile basicFile = filesTreeOfTheCmmitTreeView.getSelectionModel().getSelectedItem().getValue();
        if (basicFile.GetType() == TypeOfDoc.BLOB) {
            controller.setText(((Blob) basicFile).getContent());
        }
        controller.setLabels(basicFile.getName(), StaticMethods.DateToString(basicFile.getLastmodified()), basicFile.getLastchangedby());
    }

    @FXML
    void merge(ActionEvent event) {
        Task task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateProgress(0, 5);
                List<String> nameOfAllBranches = manager.getListOfBranches();
                StringProperty sp = new SimpleStringProperty();
                sp.addListener(((observable, oldValue, newValue) ->
                {
                    mergeClassobject conflictsAndWc;
                    try {

                        conflictsAndWc = manager.Merge(newValue);
                        if (conflictsAndWc.isFF()) {
                            if(conflictsAndWc.isChangeFF()) {
                                manager.DeleteWc(manager.getMyRepository().getPath());
                                manager.DeployBranch(new File(manager.PathToActiveBranch()));
                                setAllData();
                            }
                            StaticUi.ShowError(Alert.AlertType.INFORMATION, OutputStrings.FF);
                        }
                        else
                        {
                            //updateProgress(1,5);
                            StringProperty stringProperty = new SimpleStringProperty();
                            LinkedList<BasicFile>[] changeList = new LinkedList[3];
                            for (int i = 0; i < changeList.length; i++) {
                                changeList[i] = new LinkedList<>();
                            }
                            stringProperty.addListener(((observable1, oldValue1, newValue1) ->
                            {
                                conflictsAndWc.setNote(newValue1);
                                manager.DeleteFiles(conflictsAndWc.getNewWc(), conflictsAndWc.getFilesToDelete()); //צריך לעדכן גם את השוואנים של התיקיות שמהם נמחקו קבצים
                                manager.DeleteWc(manager.getMyRepository().getPath());
                                for (BasicFile file : conflictsAndWc.getNewWc().getContent().values())  // send all the content of the Wc
                                {
                                    try {
                                        manager.RecDeployBranch(file, manager.getMyRepository().getPath());

                                    } catch (Exception e) {
                                        Platform.runLater(() -> StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage()));
                                    }
                                }
                                try {
                                    //updateProgress(2,5);
                                    manager.BuildCommitFileAndUpdateActiveBranch(conflictsAndWc.getNewWc().getShawan(), conflictsAndWc.getPrevCommit1(), conflictsAndWc.getNote(), StaticMethods.DateToString(conflictsAndWc.getNewWc().getLastmodified()),
                                            conflictsAndWc.getNewWc().getLastchangedby(), manager.getMyRepository().getPathToObject(), conflictsAndWc.getPrevCommit2());
                                    manager.GetChanges(changeList, conflictsAndWc.getNewWc(), conflictsAndWc.getOursWc(), true);
                                    manager.CommitFiles(changeList, manager.getMyRepository().getPathToObject());
                                    Platform.runLater(() -> {
                                        try {
                                            setAllData();
                                        } catch (Exception e) {
                                            Platform.runLater(() -> StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage()));
                                        }
                                    });
                                    //updateProgress(5,5);
                                } catch (Exception e) {
                                    Platform.runLater(() -> StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage()));
                                }

                            }));

                            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsToFxml.conflictHandler));
                            Pane root = loader.load();
                            ConflictHandlerController conflictHandlerController = loader.getController();
                            Stage stage = new Stage();
                            conflictHandlerController.setConflictsMap(conflictsAndWc.getConflictMap());
                            conflictHandlerController.setManager(manager);
                            conflictHandlerController.setStringPropery(stringProperty);
                            conflictHandlerController.setFilesToDelete(conflictsAndWc.getFilesToDelete());
                            stage.setTitle("Conflict Handler");
                            stage.setScene(new MyScene(root));
                            conflictHandlerController.InitializeData();
                            Platform.runLater(stage::show);
                        }

                    } catch (Exception e) {
                        StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                    }
                }));
                FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsToFxml.branchChooser));
                Platform.runLater(() -> {
                    try {
                        StaticUi.OpenBranchChooser(sp, loader, nameOfAllBranches,OutputStrings.chooseBranch);
                    } catch (IOException e) {
                        Platform.runLater(() -> StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage()));
                    }
                });


                return null;
            }
        };

        //bindToComponents(task);
        new Thread(task).start();
    }

   public void MergeCommitTree(String branch)
   {
       mergeClassobject conflictsAndWc;
       try {

           conflictsAndWc = manager.Merge(branch);
           if (conflictsAndWc.isFF()) {
               if(conflictsAndWc.isChangeFF()) {
                   manager.DeleteWc(manager.getMyRepository().getPath());
                   manager.DeployBranch(new File(manager.PathToActiveBranch()));
                   setAllData();
               }
               StaticUi.ShowError(Alert.AlertType.INFORMATION, OutputStrings.FF);
           }
           else
           {
               //updateProgress(1,5);
               StringProperty stringProperty = new SimpleStringProperty();
               LinkedList<BasicFile>[] changeList = new LinkedList[3];
               for (int i = 0; i < changeList.length; i++) {
                   changeList[i] = new LinkedList<>();
               }
               stringProperty.addListener(((observable1, oldValue1, newValue1) ->
               {
                   conflictsAndWc.setNote(newValue1);
                   manager.DeleteFiles(conflictsAndWc.getNewWc(), conflictsAndWc.getFilesToDelete()); //צריך לעדכן גם את השוואנים של התיקיות שמהם נמחקו קבצים
                   manager.DeleteWc(manager.getMyRepository().getPath());
                   for (BasicFile file : conflictsAndWc.getNewWc().getContent().values())  // send all the content of the Wc
                   {
                       try {
                           manager.RecDeployBranch(file, manager.getMyRepository().getPath());

                       } catch (Exception e) {
                           Platform.runLater(() -> StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage()));
                       }
                   }
                   try {
                       //updateProgress(2,5);
                       manager.BuildCommitFileAndUpdateActiveBranch(conflictsAndWc.getNewWc().getShawan(), conflictsAndWc.getPrevCommit1(), conflictsAndWc.getNote(), StaticMethods.DateToString(conflictsAndWc.getNewWc().getLastmodified()),
                               conflictsAndWc.getNewWc().getLastchangedby(), manager.getMyRepository().getPathToObject(), conflictsAndWc.getPrevCommit2());
                       manager.GetChanges(changeList, conflictsAndWc.getNewWc(), conflictsAndWc.getOursWc(), true);
                       manager.CommitFiles(changeList, manager.getMyRepository().getPathToObject());
                       Platform.runLater(() -> {
                           try {
                               setAllData();
                           } catch (Exception e) {
                               Platform.runLater(() -> StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage()));
                           }
                       });
                       //updateProgress(5,5);
                   } catch (Exception e) {
                       Platform.runLater(() -> StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage()));
                   }

               }));

               FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsToFxml.conflictHandler));
               Pane root = loader.load();
               ConflictHandlerController conflictHandlerController = loader.getController();
               Stage stage = new Stage();
               conflictHandlerController.setConflictsMap(conflictsAndWc.getConflictMap());
               conflictHandlerController.setManager(manager);
               conflictHandlerController.setStringPropery(stringProperty);
               conflictHandlerController.setFilesToDelete(conflictsAndWc.getFilesToDelete());
               stage.setTitle("Conflict Handler");
               stage.setScene(new MyScene(root));
               conflictHandlerController.InitializeData();
               Platform.runLater(stage::show);
           }

       } catch (Exception e) {
           StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
       }
   }


//    private void bindToComponents(Task task) {
//        ProgressBarFx.progressProperty().bind(task.progressProperty());
//        ProgressIndicatorFx.progressProperty().bind(task.progressProperty());
//    }

    @FXML
    void clone(ActionEvent event) {
        File chosenFile = StaticUi.DirectoryChooser();
        if (chosenFile != null) {
            StringProperty sp = new SimpleStringProperty();
            sp.addListener(((observable, oldValue, newValue) -> {
                try {
                    manager.clone(chosenFile.getPath(), newValue);
                } catch (Exception e) {
                    StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                }
            }));

            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsToFxml.namePopUp));
            try {
                StaticUi.OpenOneButtonPopUp(sp, loader, OutputStrings.repositoryname);
            } catch (IOException e) {
                StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
            }

        }

    }

    @FXML
    void Fetch(ActionEvent event) {
        if (manager.IsRemote()) {
            try {
                manager.fetch();
                setAllData();
            } catch (Exception e) {
                StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
            }
        } else {
            StaticUi.ShowError(Alert.AlertType.WARNING, OutputStrings.notCollaborate);
        }
    }

    @FXML
    void pull(ActionEvent event)  {
        try {
            if (manager.IsRemote()) {
                File headBranch = new File(manager.PathToActiveBranch());
                List<String> branchdetails = Files.readAllLines(Paths.get(headBranch.getPath()));
                if (branchdetails.size() > 1) {
                    if (manager.CheckIfWcIscLean()) {
                        if (manager.CheckIfRtbHaveNewData(headBranch)) {
                            manager.pull(headBranch);
                            setAllData();
                        } else {
                            StaticUi.ShowError(Alert.AlertType.WARNING, OutputStrings.RTBHaveNewData);
                        }
                    } else {
                        StaticUi.ShowError(Alert.AlertType.WARNING, OutputStrings.cantDeploy);
                    }
                } else {
                    StaticUi.ShowError(Alert.AlertType.WARNING, OutputStrings.notRTBBranch);
                }
            } else {
                StaticUi.ShowError(Alert.AlertType.WARNING, OutputStrings.notCollaborate);
            }
        } catch (Exception e )
          {
          StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
          }

    }

    @FXML
    void Push(ActionEvent event)  {
        try {
            if (manager.IsRemote()) {
                File headBranch = new File(manager.PathToActiveBranch());

                List<String> branchdetails = Files.readAllLines(Paths.get(headBranch.getPath()));
                if (branchdetails.size() > 1) {
                    if (manager.CheckIfRemoteBranchHaveNewData(headBranch)) {
                        manager.push(headBranch);
                    } else {
                        StaticUi.ShowError(Alert.AlertType.WARNING, OutputStrings.branchInRemoteRepositoryHaveNewData);
                    }
                } else {
                    BooleanProperty sp = new SimpleBooleanProperty();
                    sp.addListener((observable -> {
                        try {
                            manager.pushNotRTB(headBranch);
                        } catch (Exception e) {
                            StaticUi.ShowError(Alert.AlertType.WARNING, OutputStrings.branchInRemoteRepositoryHaveNewData);
                        }
                    }));
                    StaticUi.yesOrNoAlert(OutputStrings.getNotRTBBranchProcced, sp);
                }

            } else {
                StaticUi.ShowError(Alert.AlertType.WARNING, OutputStrings.notCollaborate);
            }
        } catch (Exception e )
        {
            StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
        }
    }


    @FXML
    public void Exit() {
        primaryStage.close();
    }

//////////////////////////////////
}






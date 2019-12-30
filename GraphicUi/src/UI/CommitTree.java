package UI;

import backend.GitManager;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import mypackage.MagitSingleBranch;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CommitTree {
    private GitManager gitManager;
    private MagitController controller;
    private VBox vBox;
    private HBox hBox;
    private AnchorPane scrollPane;
    private Map<String, String> commitBranchMaps, pointedCommits = new HashMap<>();
    private Map<String, Integer> pointBranchMap;
    private Map<String, Point> commitPlaces = new HashMap<>();
    private Map<String, Color> branchColors = new HashMap<>();
    private Map<String, Circle> commitCircles = new HashMap<>();
    private Random random = new Random();
    private double duration;

    public boolean isAllCommits() {
        return allCommits;
    }

    public void setAllCommits(boolean allCommits) {
        this.allCommits = allCommits;
    }

    private boolean allCommits ;//
    public CommitTree(GitManager gitManager, MagitController controller, VBox vBox, HBox hBox, AnchorPane scrollPane,boolean allCommits,double duration) throws Exception {
        this.gitManager = gitManager;
        this.vBox = vBox;
        this.hBox = hBox;
        this.scrollPane = scrollPane;
        this.controller = controller;
        this.allCommits = allCommits;
        this.duration = duration;
        initializeTree();
    }


    private void initializeTree() throws Exception {
        List<String> commitList = gitManager.GetListOfAllCommits();
        List<String> abandoned = new ArrayList<>();
        if(allCommits) //
        {
            abandoned = gitManager.AddAllAbandonedCommits(commitList);
            commitList.addAll(abandoned);
        } //
        File[] branches = gitManager.GetAllBranches();
        List<String> branchList = Arrays.stream(branches).filter(file -> !file.getName().equals("Head")).map(File::getName).collect(Collectors.toList());

        if (gitManager.IsRemote()) {
            branchList.addAll(gitManager.GetListOfAllRB());
            branchList.remove(gitManager.GetRemoteRepoDetails().get(1));
        }

        for (String branch : branchList) {
            String firstCommit = Files.readAllLines(Paths.get(gitManager.getMyRepository().getPathToBranches() + File.separator + branch)).get(0);
            if(!firstCommit.equals("Null") )
            {
                pointedCommits.put(firstCommit, branch);
            }

        }

        commitBranchMaps = gitManager.wrapFindCommits(branchList);
        if(allCommits) //
        {
            AddllAbandonedToTheList(commitBranchMaps,abandoned);
        }//
        pointBranchMap = new HashMap<>();

        int startX = 10;
         if(allCommits)
         {
             branchList.add("abandoned");
         }
        for (String branch : branchList) {
            pointBranchMap.put(branch, startX);
            startX += 30;
            branchColors.put(branch, nextColor());
        }

        Group group = new Group();
        scrollPane.getChildren().clear();
        scrollPane.getChildren().add(group);

        List<Circle> circles = new LinkedList<>();

        int startY = 10;

        for (Map.Entry<String, String> entry : commitBranchMaps.entrySet()) {
            Point coords = new Point(pointBranchMap.get(entry.getValue()), startY);
            Circle circle = createCircle(entry.getKey(), entry.getValue(), coords);
            circles.add(circle);
            commitCircles.put(entry.getKey(), circle);
            commitPlaces.put(entry.getKey(), coords);
            startY += 30;
        }

        group.getChildren().addAll(connectionTheDots());
        group.getChildren().addAll(circles);
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(duration), group);///
        group.setRotate(180);
        rotateTransition.setByAngle(360);
        rotateTransition.play();
        addBranchNames(branchList);
    }

    private void AddllAbandonedToTheList(Map<String, String> commitBranchMaps, List<String> abandoned) { ///
        for(String commit : abandoned)
        {
         commitBranchMaps.put(commit,"abandoned");
        }
    }


    private List<Node> connectionTheDots() throws IOException {
        List<Node> lineList = new LinkedList<>();
        for (Map.Entry<String, String> entry : commitBranchMaps.entrySet()) {
            List<String> prevs = gitManager.getPrevCommitShawan(entry.getKey());
            for (String prevCommit : prevs) {
                Point start = commitPlaces.get(entry.getKey());
                Point end = commitPlaces.get(prevCommit);
                CubicCurve line = new CubicCurve(
                        start.x, start.y, //start
                        start.x, end.y, //curve 1
                        start.x, end.y, //curve 2
                        end.x, end.y // end
                );
                line.setStroke(branchColors.get(commitBranchMaps.get(entry.getKey())));
                line.setFill(Color.TRANSPARENT);
                line.setStrokeWidth(7);
                lineList.add(line);
            }
        }

        return lineList;
    }

    private void addBranchNames(List<String> branchList) {
        hBox.getChildren().clear();
        for (String branch : branchList) {
            Circle circle = new Circle(0, 0, 10, branchColors.get(branch));
            Label label = new Label(branch);
            label.setMinWidth(branch.length() == 1 ? 15 : branch.length() * 10);
            label.setPadding(new Insets(0, 0, 0, 3));
            Separator separator = new Separator(Orientation.VERTICAL);
            separator.setPadding(new Insets(0, 3, 0, 3));
            label.setOnMouseClicked(event -> changeSize(branch));
            circle.setOnMouseClicked(event -> changeSize(branch));
            hBox.getChildren().addAll(circle, label, separator);
        }
    }

    private void changeSize(String branch) {
        try {
            String firstCommit = Files.readAllLines(Paths.get(gitManager.getMyRepository().getPathToBranches() + File.separator + branch)).get(0);
            List<String> commits = new LinkedList<>();
            gitManager.ListallPrevCommits(commits, firstCommit);

            for (String commit : commits) {
                Circle circle = commitCircles.get(commit.split(",")[0]);
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(duration), circle);///
                scaleTransition.setByX(1.1);
                scaleTransition.setByY(1.1);
                scaleTransition.setCycleCount(4);
                scaleTransition.setAutoReverse(true);
                scaleTransition.play();
            }

        } catch (IOException ignored) {
        }
    }

    private Color nextColor() {
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    private Circle createCircle(String commit, String branch, Point coords) {
        Color color = branchColors.get(branch);
        Circle circle = new Circle(coords.x, coords.y, 10, color);

        if (pointedCommits.containsKey(commit)) {
            Tooltip tooltip = new Tooltip(pointedCommits.get(commit));
            Tooltip.install(circle, tooltip);
            if (color.equals(color.darker())) {
                circle.setStroke(Color.RED);
            } else {
                circle.setStroke(color.darker());
            }
            circle.setStrokeWidth(3);
        }

        circle.setOnMouseClicked((event -> {
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                addMenu(commit, branch).show(circle, event.getScreenX(), event.getScreenY());
            } else {
                try {
                    controller.updateCommitDetails(commit);
                    changeSize(branch);
                } catch (Exception e) {
                    StaticUi.ShowError(Alert.AlertType.ERROR, e.getMessage());
                }
            }
        }));

        return circle;
    }

    private ContextMenu addMenu(String commit, String branch) {
        ContextMenu menu = new ContextMenu();

        MenuItem addNewBranch = new MenuItem("Add new branch"), //will not be RTB // will not ask whether to deploy the branch or not
                resetHeadBranch = new MenuItem("Reset head branch to this"),
                merge = new MenuItem("Merge to this"),
                delete = new MenuItem("Delete pointed branch");


        addNewBranch.setOnAction((event -> addNewBranchCommitTree(commit)));
        resetHeadBranch.setOnAction((event -> resetHeadBranchCommitTree(commit)));
        merge.setOnAction((event -> mergeCommitTree(commit)));
        delete.setOnAction((event -> DeleteBranchCommitTree(commit)));
        if (!pointedCommits.containsKey(commit)) {
            merge.setDisable(true);
            delete.setDisable(true);
        }
        else if((pointedCommits.get(commit).contains(File.separator)))
        {
            merge.setDisable(true);
            delete.setDisable(true);
        }



        menu.getItems().addAll(addNewBranch, resetHeadBranch, merge, delete);
        return menu;
    }

    public void addNewBranchCommitTree(String commit)
    {
        StringProperty sp = new SimpleStringProperty();
        sp.addListener(((observable, oldValue, newValue) ->
        {
            try {
                gitManager.CreateNewBranchwithShawan(newValue,commit,false);
                controller.setAllData();
            } catch (Exception e) {
                StaticUi.ShowError(Alert.AlertType.ERROR,e.getMessage());
            }
        }));
        FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsToFxml.namePopUp));
        try {
            StaticUi.OpenOneButtonPopUp(sp, loader, OutputStrings.enterNamePopup);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void resetHeadBranchCommitTree(String commit)
    {
        try {
            if(gitManager.CheckIfWcIscLean())
            {
                gitManager.CheckoutBranch(gitManager.GetTheCommit(commit));
                controller.setAllData();
            }
            else
            {
                BooleanProperty sb = new SimpleBooleanProperty();
                sb.addListener((observable ->
                {
                    try {
                        gitManager.CheckoutBranch(gitManager.GetTheCommit(commit));
                        controller.setAllData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
                StaticUi.yesOrNoAlert(OutputStrings.changesOnWc, sb);
            }
        } catch (Exception e) {
            StaticUi.ShowError(Alert.AlertType.ERROR,e.getMessage());
        }
    }
    public void mergeCommitTree(String commit)
    {
        String branch = pointedCommits.get(commit);
        controller.MergeCommitTree(branch);
    }
    public void DeleteBranchCommitTree(String commit)
    {

        String branch = pointedCommits.get(commit);
        try {
            gitManager.DeleteBrunch(branch);
            controller.setAllData();

        } catch (Exception e) {
            StaticUi.ShowError(Alert.AlertType.ERROR,e.getMessage());
        }

    }
//    public File FindIfCommitIsPointed(String commit) throws IOException {
//        File[] allBranches = gitManager.GetAllBranches();
//        for(File file : allBranches )
//        {
//            if(file.isFile())
//            {
//                String content = Files.readAllLines(Paths.get(file.getPath())).get(0);
//                if(commit.equals(content))
//                    return file;
//            }
//
//        }
//        return null;
//    }
}

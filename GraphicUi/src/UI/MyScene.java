package UI;

import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.File;

public class MyScene extends Scene {
    public static final String cssStyleSheet = "/lib/css/custom.css";

    public MyScene(Parent root) {
        super(root);
        File externalCSS = new File(new File("").getAbsolutePath() + cssStyleSheet);
        this.getStylesheets().add(externalCSS.toURI().toString());
    }
}

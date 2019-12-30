package UI;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.function.Function;

import backend.BasicFile;
import backend.Folder;
import backend.TypeOfDoc;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TreeViewOfRepository  {


    Image foldericon;
    Image fileicon;
    public TreeViewOfRepository()
    {
         foldericon = new Image (getClass().getResourceAsStream("/Img/folderResized.jpg"));
         fileicon = new Image (getClass().getResourceAsStream("/Img/fileresized.png"));
    }
    public TreeView<String> GetTree(String Root) throws IOException {

        // create root
        TreeItem<String> treeItem = new TreeItem<>(new File(Root).getName(),new ImageView(foldericon));
        treeItem.setExpanded(true);

        // create tree structure
        createTree(treeItem, Paths.get(Root));

        // sort tree structure by name
        treeItem.getChildren().sort( Comparator.comparing( new Function<TreeItem<String>, String>() {
            @Override
            public String apply(TreeItem<String> t) {
                return t.getValue().toString().toLowerCase();
            }
        }));

        // create components
       return new TreeView<String>(treeItem);

    }

    public void createTree(TreeItem<String> rootItem, Path rootPath) throws IOException {

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(rootPath)) {

            for (Path path : directoryStream) {
                File file = new File(path.toString());
                String nameOfFile = file.getName();
                TreeItem<String> newItem = new TreeItem<>();
                if(file.isDirectory())
                {
                    TreeItem<String> temp = new TreeItem<>(nameOfFile,new ImageView(foldericon));
                    newItem=temp;
                }
                else
                {
                    TreeItem<String> temp = new TreeItem<>(nameOfFile,new ImageView(fileicon));
                    newItem=temp;
                }
                if(!(nameOfFile.equals(".Magit"))) {

                    newItem.setExpanded(false);

                    rootItem.getChildren().add(newItem);

                    if (Files.isDirectory(path)) {
                        createTree(newItem, path);
                    }
                }
            }
        }
    }
   public  TreeView<BasicFile> getTreeFromCommit(Folder folder )
   {
       TreeView<BasicFile> commitTree = new TreeView<>();
       TreeItem<BasicFile> root = new TreeItem<>(folder,new ImageView(foldericon));
       createTreeFromObject(root,folder);
       root.setExpanded(true);
       commitTree.setRoot(root);
       return commitTree;
   }


    private void createTreeFromObject(TreeItem<BasicFile> root,BasicFile file)
    {

        if(file.GetType() == TypeOfDoc.FOLDER)
        {
            for(BasicFile childrenFiles : ((Folder)file).getContent().values())
            {
                if(childrenFiles.GetType() == TypeOfDoc.FOLDER)
                {
                    TreeItem<BasicFile> child = new TreeItem<>(childrenFiles, new ImageView(foldericon));
                    root.getChildren().add(child);
                    createTreeFromObject(child,childrenFiles);
                }
                else
                {
                    TreeItem<BasicFile> child = new TreeItem<>(childrenFiles, new ImageView(fileicon));
                    root.getChildren().add(child);
                }
            }
        }
    }




}


package backend;


import java.io.File;

public class Repository {
   // private String name;
    private String name; //needs to change
    private String Path ;
    private String PathToMagit;
    private String PathToObject;
    private String PathToBranches;
    private String PathTOHead;

    public String getPathToMagit() {
        return PathToMagit;
    }

    public String getPathToObject() {
        return PathToObject;
    }

    public String getPathToBranches() {
        return PathToBranches;
    }



    private Magit magit = new Magit();
    // will hold Pathes to all relevant directories

    public void setPath(String path) {
        this.Path = path;
        this.PathToMagit = Path + File.separator + ".Magit";
        this.PathToObject = PathToMagit + File.separator + "Object";
        this.PathToBranches = PathToMagit + File.separator + "Branches";
        this.PathTOHead = getPathToBranches() + File.separator + "Head";
    }

    public Repository() {// create folders

    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws Exception {
        this.name = name;
        StaticMethods.BuildFile(Path + File.separator + ".Magit" + File.separator + "name" , name);
    }

    public String getPathToHead() {
        return PathTOHead;
    }

    public void BuildRepository (String Path,String name) throws Exception {
     setPath(Path);
     StaticMethods.BuildDir(Path);
     magit.BuildMagit(Path + File.separator + ".Magit");
     setName(name);

 }



    public Magit getMagit() {
        return magit;
    }

    public void setMagit(Magit magit) {
        this.magit = magit;
    }

    public String getPath() {
        return Path;
    }


}

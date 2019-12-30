package backend;

public class Magit {
    private Object obj = new Object();
    private Branches branches = new Branches();

    public  void BuildMagit(String Path) throws Exception {
        StaticMethods.BuildDir(Path);
        obj.BuildObj(Path + "/Object");
        branches.BuildBranches (Path + "/Branches");
    }

    public Magit() {
    }

    public Magit(Object obj, Branches branches) {
        this.obj = obj;
        this.branches = branches;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public Branches getBranches() {
        return branches;
    }

    public void setBranches(Branches branches) {
        this.branches = branches;
    }
}

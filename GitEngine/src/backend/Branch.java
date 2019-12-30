package backend;

public class Branch {
private String commit;
private  String name;

    public Branch(String commit , String name) {
        this.commit = commit;
        this.name = name;
    }

    public String getCommit() {
        return commit;
    }

    public String getName() {
        return name;
    }
}

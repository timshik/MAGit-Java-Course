package UI;



public class ClassForTableView {

    private  String branchName ;
    private String commitNote;
    private String commitLastUpdated;
    private String commitShawan;
    public  ClassForTableView(String[] strings) {
        if (strings.length == 1) {
            setClassForTableView(strings[0], OutputStrings.emptyCell,OutputStrings.emptyCell,OutputStrings.emptyCell);
        }
        if (strings.length == 3) {
            setClassForTableView(OutputStrings.emptyCell,strings[0], strings[1], strings[2]);
        }
        if (strings.length == 4) {
            setClassForTableView(strings[0], strings[1], strings[2], strings[3]);
        }
    }


    public  void setClassForTableView(String branchName, String commitNote, String commitLastUpdated, String commitShawan)
    {
        setBranchName(branchName);
        setCommitNote(commitNote);
        setCommitLastUpdated(commitLastUpdated);
        setCommitShawan(commitShawan);
    }
    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getCommitNote() {
        return commitNote;
    }

    public void setCommitNote(String commitNote) {
        this.commitNote = commitNote;
    }

    public String getCommitLastUpdated() {
        return commitLastUpdated;
    }

    public void setCommitLastUpdated(String commitLastUpdated) {
        this.commitLastUpdated = commitLastUpdated;
    }

    public String getCommitShawan() {
        return commitShawan;
    }

    public void setCommitShawan(String commitShawan) {
        this.commitShawan = commitShawan;
    }



}

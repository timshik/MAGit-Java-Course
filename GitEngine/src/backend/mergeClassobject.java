package backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class mergeClassobject {
    private Folder newWc;
    private Folder oursWc ;
    private Folder theirsWc;
    private Folder ancestorWc;
    private String prevCommit1;
    private String prevCommit2;
    private String note;

    public boolean isChangeFF() {
        return ChangeFF;
    }

    public void setChangeFF(boolean changeFF) {
        ChangeFF = changeFF;
    }

    private boolean FF = false;
    private boolean ChangeFF = false;
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isFF() {
        return FF;
    }

    public void setFF(boolean FF) {
        this.FF = FF;
    }
    private Map <Blob, Blob[]> conflictMap = new HashMap<>();
    private List<Blob> filesToDelete = new ArrayList<>();
    public String getPrevCommit1() {
        return prevCommit1;
    }

    public void setPrevCommit1(String prevCommit1) {
        this.prevCommit1 = prevCommit1;
    }

    public String getPrevCommit2() {
        return prevCommit2;
    }

    public void setPrevCommit2(String prevCommit2) {
        this.prevCommit2 = prevCommit2;
    }



    public List<Blob> getFilesToDelete() {
        return filesToDelete;
    }

    public void setFilesToDelete(List<Blob> filesToDelete) {
        this.filesToDelete = filesToDelete;
    }



    public Folder getNewWc() {
        return newWc;
    }

    public void setNewWc(Folder newWc) {
        this.newWc = newWc;
    }

    public Folder getOursWc() {
        return oursWc;
    }

    public void setOursWc(Folder oursWc) {
        this.oursWc = oursWc;
    }

    public Folder getTheirsWc() {
        return theirsWc;
    }

    public void setTheirsWc(Folder theirsWc) {
        this.theirsWc = theirsWc;
    }

    public Folder getAncestorWc() {
        return ancestorWc;
    }

    public void setAncestorWc(Folder ancestorWc) {
        this.ancestorWc = ancestorWc;
    }

    public Map<Blob, Blob[]> getConflictMap() {
        return conflictMap;
    }

    public void setConflictMap(Map<Blob, Blob[]> conflictMap) {
        this.conflictMap = conflictMap;
    }
}

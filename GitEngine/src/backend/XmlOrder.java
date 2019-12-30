package backend;

import java.util.Map;
import java.util.TreeMap;

public class XmlOrder {

       private int commitCounter = 1;
       private int folderCounter = 1;
       private int blobCounter = 1;
       private Map<String,String> commitMap = new TreeMap<>(); //name
       private Map<String,String> folderMap = new TreeMap<>(); // path
       private Map<String,String> blobMap = new TreeMap<>();  //path

    public void commitInc ()
    {
        commitCounter++;
    }
    public void folderInc ()
    {
        folderCounter++;
    }
    public void BlobInc ()
    {
        blobCounter++;
    }

    public int getCommitCounter() {
        return commitCounter;
    }

    public void setCommitCounter(int commitCounter) {
        this.commitCounter = commitCounter;
    }

    public int getFolderCounter() {
        return folderCounter;
    }

    public void setFolderCounter(int folderCounter) {
        this.folderCounter = folderCounter;
    }

    public int getBlobCounter() {
        return blobCounter;
    }

    public void setBlobCounter(int blobCounter) {
        this.blobCounter = blobCounter;
    }

    public Map<String,String> getCommitMap() {
        return commitMap;
    }

    public void setCommitMap(Map<String,String> commitMap) {
        this.commitMap = commitMap;
    }

    public Map<String,String> getFolderMap() {
        return folderMap;
    }

    public void setFolderMap(Map<String,String> folderMap) {
        this.folderMap = folderMap;
    }

    public Map<String,String> getBlobMap() {
        return blobMap;
    }

    public void setBlobMap(Map<String,String> blobMap) {
        this.blobMap = blobMap;
    }
}

package backend;

import mypackage.*;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class XmlValdiation {
   private XmlExportImport xmlClass = new XmlExportImport();
    public boolean StartChecking (String pathToXml) throws Exception {
        InputStream inputStream = new FileInputStream(pathToXml); // need to change
        xmlClass.deserializeFrom(inputStream);
        checkFileExitsAndXml (pathToXml);

        if(xmlClass.getXmlRepo().getMagitRemoteReference()!= null)
        {
            if(xmlClass.getXmlRepo().getMagitRemoteReference().getName()!= null & xmlClass.getXmlRepo().getMagitRemoteReference().getLocation()!= null) {
                CheckIfRemoteRepoExist();
                CheckTrakingBranches(xmlClass.getXmlRepo().getMagitBranches());
            }
        }
        CheckDoubledId (xmlClass.getXmlRepo().getMagitBranches(),xmlClass.getXmlRepo().getMagitCommits(),xmlClass.getXmlRepo().getMagitFolders(),xmlClass.getXmlRepo().getMagitBlobs());
        checkPointedId (xmlClass.getXmlRepo().getMagitFolders());
        CheckifCommitPointsToFolder(xmlClass.getXmlRepo().getMagitCommits());
        //CheckIfBranchPointsToCommit(xmlClass.getXmlRepo().getMagitBranches());
        CheckifHeadExists (xmlClass.getXmlRepo().getMagitBranches());
        if(!checkLocationIsValid(xmlClass.getXmlRepo().getLocation()))
        {
            return false;
        }
        return true;
    }

    private void CheckIfRemoteRepoExist() throws Exception {
           String pathToRemoteRepo = xmlClass.getXmlRepo().getMagitRemoteReference().getLocation();
           if(!new File (pathToRemoteRepo + File.separator + ".Magit").exists())
           {
               throw new Exception("remote repo doesnt exist");
           }

    }
    private void CheckTrakingBranches(MagitBranches branchList) throws Exception {
        for(MagitSingleBranch curr : branchList.getMagitSingleBranch())
        {
            if(curr.isTracking())
            {
               String trackingbranch =curr.getTrackingAfter();
               String nameOfRB = curr.getTrackingAfter();
               MagitSingleBranch RB = FindBranch(nameOfRB,branchList);
               if (RB == null )
               {
                   throw new Exception(curr.getName() + " tracking after branch that doesnt exist");
               }
                if(!RB.isIsRemote())
                {
                    throw new Exception(curr.getName() + " tracking after branch that isnt a remote branch");
                }
            }
        }
    }

    private MagitSingleBranch FindBranch(String nameOfRB,MagitBranches branchList) {
        for(MagitSingleBranch curr : branchList.getMagitSingleBranch())
        {
            if (curr.getName().equals(nameOfRB))
            {
                return curr;
            }
        }
        return null;
    }


    private  void checkFileExitsAndXml(String path) throws Exception { // valdation number 3.1
        File file = new File(path);
        if(!file.exists())
        {
            throw new Exception("the xml file doesnt exist");
        }
        if(! FilenameUtils.getExtension(file.getName()).toString().equals("xml"))
        {
            throw new Exception("the file that was given wasn't xml file");
        }
    }
    private boolean checkLocationIsValid(String location) throws Exception {
       File file = new File(location);
        if (file.exists())
        {
            if (new File (file.getPath()+ File.separator + ".Magit").exists())
            {
                return false;
            }
            if(file.listFiles().length!=0)
            {
                throw new Exception("the path given wasn't to an empty folder");////
            }
        }
        return true;
    }


    private void CheckDoubledId(MagitBranches branches, MagitCommits commits,MagitFolders folders,MagitBlobs blobs ) throws Exception //3.2
    {
        Map<String,MagitSingleCommit> mapCommit = new HashMap<>();
        for (MagitSingleCommit curr : commits.getMagitSingleCommit())
        {
            if (mapCommit.containsKey(curr.getId()) )
            {
                throw new Exception( "Two Commits With same ID");
            }
            mapCommit.put(curr.getId(),curr);
        }
        Map<String,MagitSingleFolder> mapFolder = new HashMap<>();
        for (MagitSingleFolder curr : folders.getMagitSingleFolder())
        {
            if (mapFolder.containsKey(curr.getId()) )
            {
                throw new Exception( "Two folders With same ID");
            }
            mapFolder.put(curr.getId(),curr);
        }
        Map<String,MagitBlob> mapBlob = new HashMap<>();
        for (MagitBlob curr : blobs.getMagitBlob())
        {
            if (mapBlob.containsKey(curr.getId()))
            {
                throw new Exception( "Two blobs With same ID");
            }
            mapBlob.put(curr.getId(),curr);
        }
        for(MagitSingleBranch curr : branches.getMagitSingleBranch())
        {
            if(!curr.getPointedCommit().getId().equals("") ) {
                if (xmlClass.getSingleCommitFromId(curr.getPointedCommit().getId()) == null) {
                    throw new Exception("Branch points to Commit that doesnt Exist");
                }
            }
        }

    }

    private void checkPointedId(MagitFolders folderList) throws Exception //3.3 //3.4 //3.5
        {
               for(MagitSingleFolder curr : folderList.getMagitSingleFolder())
               {
                 for(Item item: curr.getItems().getItem())
                 {
                     if(StaticMethods.WhichType(item.getType())==TypeOfDoc.FOLDER)
                     {
                        MagitSingleFolder folder = xmlClass.getSingleFolderFromId(item.getId());
                         if(folder == null)
                         {
                             throw new Exception( "Pointed folder id doesnt exist");
                         }
                         if(folder.getId().equals(curr.getId()))
                         {
                             throw new Exception( "folder contain itself (id)");
                         }

                     }
                     else
                     {
                         if(xmlClass.getSingleBlobFromId(item.getId())==null)
                         {
                             throw new Exception( "Pointed blob id doesnt exist");
                         }
                     }
                 }
               }

        }
    private void CheckifCommitPointsToFolder (MagitCommits commitList) throws Exception { //3.6 //3.7
            for (MagitSingleCommit curr : commitList.getMagitSingleCommit()) {
                MagitSingleFolder folder = xmlClass.getSingleFolderFromId(curr.getRootFolder().getId());
                if (folder == null) {
                    throw new Exception("Commit points to Folder that doesnt exist");
                }
                if (!folder.isIsRoot())
                {
                    throw new Exception("Commit points to Folder that isnt RootFolder");
                }
            }
        }
    private void CheckIfBranchPointsToCommit (MagitBranches branchList) throws Exception { //3.8
            for(MagitSingleBranch curr : branchList.getMagitSingleBranch())
            {
                if(xmlClass.getSingleCommitFromId(curr.getPointedCommit().getId())==null)
                {
                    throw new Exception("Branch points to Commit that doesnt Exist");
                }
            }
        }
    private void CheckifHeadExists (MagitBranches magitBranches) throws Exception { //3.9
            for(MagitSingleBranch curr : magitBranches.getMagitSingleBranch())
            {
                if(curr.getName().equals(magitBranches.getHead()))
                {
                    return;
                }
            }
            throw new Exception("Branch Head doesnt exist");
        }


}



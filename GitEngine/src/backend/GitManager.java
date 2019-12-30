package backend;


import javafx.scene.shape.Path;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import puk.team.course.magit.ancestor.finder.AncestorFinder;
import puk.team.course.magit.ancestor.finder.CommitRepresentative;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

public class GitManager {
    private Repository MyRepository;
    private User CurrentUser;

    public User getCurrentUser() {
        return CurrentUser;
    }

    public GitManager(Repository myRepository, User user) throws Exception {
        this.MyRepository = myRepository;
        CurrentUser = user;
    }

    public GitManager() throws Exception {
        this.MyRepository = new Repository();
        CurrentUser = new User();
    }

    public Repository getMyRepository() {
        return MyRepository;
    }

    public void setMyRepository(Repository myRepository) {
        MyRepository = myRepository;
    }

    //////////////////////////////////  functions implementation"?(OPL.


    public void ChangeUserName(String Name) {
        CurrentUser.setUsername(Name);
    }

    public boolean CheckIfXmlValid(String path) throws Exception {
        XmlValdiation validation = new XmlValdiation();
        return (validation.StartChecking(path));
    }

    public void DeleteRepository(String path) {
        Deleterepository(path);
    }

    public void ReadRepositoryFile(String path, boolean deleteOrNot) throws Exception {

        XmlExportImport xmlClass = new XmlExportImport(this);
        xmlClass.XmlToGit(path, deleteOrNot);
    }

    public void SwitchRepository(String Path) throws Exception     //change name as well
    {
        File file = new File(Path + File.separator + ".Magit");
        if (file.exists()) {
            MyRepository.setPath(Path);
            String name = Files.readAllLines(Paths.get(file.getPath() + File.separator + "name")).get(0);
            MyRepository.setName(name);
        } else {
            throw new FileNotFoundException("The Path entered isn't a path to repository");
        }
    }

    public List<String> ShowCurrentCommitFileSystemInfo() throws Exception {
        Checkifworkingrepository();
        List<String> filesFromLastCommit = new LinkedList<>();
        String Path = MyRepository.getPathToObject();
        String Shawan = GetShawanFromLastCommit();   /// i can get it now from the table
        if (!Shawan.equals("Null")) {
            Folder previousWc = (Folder) GetPreviousWc(Shawan, Path, StaticMethods.GetSuffix(MyRepository.getPath()), MyRepository.getPath());
            for (BasicFile file : previousWc.getContent().values()) {
                StaticMethods.WcToStrings(file, filesFromLastCommit);
            }
        }
        return filesFromLastCommit;
    }


    public LinkedList<String>[] ShowStatus() throws Exception {
        Checkifworkingrepository();
        return (StaticMethods.GetChangedFIlesByPath(GetChangedFilesFromWc(false, null)));
    }

    public void Commit(String note) throws Exception {
        Checkifworkingrepository();
        LinkedList<BasicFile>[] ChangedList = GetChangedFilesFromWc(true, note); //will get note and send it
        CommitFiles(ChangedList, MyRepository.getPathToObject());

    }

    public List<String> ShowAllTheBranchesInTheSystem() throws Exception //////
    {
        Checkifworkingrepository();
        List<String> namesOfBranches = new LinkedList<>();

        File[] allBranches = GetAllBranches();


        File activeBranch = new File((PathToActiveBranch()));
        for (File curr : allBranches) {
            if (!curr.getName().equals("Head")) {
                String string = new String();
                if (curr.getName().equals(activeBranch.getName())) {
                    string = "this is the head branch: ";

                }
                string += curr.getName() + ",";
                String commit = Files.readAllLines(Paths.get(curr.getPath())).get(0);

                if (!commit.equals("Null")) {
                    string += commit + "," + GetNoteFromCommit(commit);
                }
                namesOfBranches.add(string);
            }
        }

        return namesOfBranches;
    }

    public void CreateNewBranch(String name) throws Exception {  // Working
        Checkifworkingrepository();
        if (name.equals("Head")) {
            throw new Exception(" you cant create branch with the name: Head");
        }
        String Path = MyRepository.getPathToBranches();
        File brunchToAdd = new File(Path + File.separator + name);
        if (brunchToAdd.exists()) {
            throw new Exception("the branch already exists");
        }
        String commit = null;
        try {
            commit = (Files.readAllLines(Paths.get(PathToActiveBranch()))).get(0);
        } catch (Exception e) {
            throw new Exception("someone deleted Mater branch");
        }
        StaticMethods.BuildFile(Path + File.separator + name, commit);

    }

    public void CreateNewBranchwithShawan(String name, String shawan, boolean isRTB) throws Exception {  //send another parameter (true/false) if the branch created is RTB
        Checkifworkingrepository();
        if (name.equals("Head")) {
            throw new Exception(" you cant create branch with the name: Head");
        }
        String Path = MyRepository.getPathToBranches();
        File brunchToAdd = new File(Path + File.separator + name);
        if (brunchToAdd.exists()) {
            throw new Exception("the branch already exists");
        }
        if(isRTB)
        {
            StaticMethods.BuildFile(Path + File.separator + name, shawan + "\n" + "true");
        }
        else
        {
            StaticMethods.BuildFile(Path + File.separator + name, shawan);
        }


    }


    public void DeleteBrunch(String name) throws Exception { // working
        Checkifworkingrepository();
        String pathToHead = PathToActiveBranch();
        File head = new File(Objects.requireNonNull(pathToHead));
        String Path = MyRepository.getPathToBranches();
        File brunchTODelete = new File(Path + File.separator + name);
        if (name.equals("Head")) {
            throw new Exception(" you cant delete file: Head");
        }
        if (head.getName().equals(name)) {
            throw new Exception(" you cant delete the Head Branch");
        }
        if (brunchTODelete.exists()) {
            brunchTODelete.delete();
        } else {
            throw new Exception("the branch doesnt exist");
        }
    }

    public boolean CheckIfWcIscLean() throws Exception {
        Checkifworkingrepository();
        LinkedList<BasicFile>[] ChangeList = GetChangedFilesFromWc(false, null);
        for (int i = 0; i < ChangeList.length; i++) {
            if (ChangeList[i].size() != 0) {
                return false;
            }
        }
        return true;
    }

    public String GetTheCommit(String shawan) throws Exception {
        File commit = new File(MyRepository.getPathToObject() + File.separator + shawan);
        if (!commit.exists()) {
            throw new Exception("the commit you entered doesnt exist");
        }
        String shawanToWc = Files.readAllLines(Paths.get(commit.getPath())).get(0).split(",")[0];
        File wc = new File(MyRepository.getPathToObject() + File.separator + shawanToWc);
        if (!wc.exists()) {
            throw new Exception("the string you entered isn't sha-1 of a commit");
        }
        StaticMethods.BuildFile(PathToActiveBranch(), shawan);
        return new File(PathToActiveBranch()).getName();
    }

    public void CheckoutBranch(String name) throws Exception {
        Checkifworkingrepository();
        //String pathToHead = PathToActiveBranch();
        String pathToBranches = MyRepository.getPathToBranches();
        File brunchToDeploy = new File(pathToBranches + File.separator + name);

        if (brunchToDeploy.exists()) {
            DeleteWc(MyRepository.getPath());
            DeployBranch(brunchToDeploy);
            StaticMethods.BuildFile(pathToBranches + File.separator + "Head", name);
        } else {
            throw new Exception(" the branch doesnt exist");
        }
    }

    public List<String> ShowHistoryOfActiveBranch() throws Exception {
        Checkifworkingrepository();
        String commit;
        try {
            commit = (Files.readAllLines(Paths.get(PathToActiveBranch()))).get(0);
        } catch (Exception e) {
            throw new Exception("Master branch was deleted");
        }
        List<String> allCommits = new LinkedList<>(); /////////////////////////
        ListallPrevCommits(allCommits, commit);
        return allCommits;
    }

    public void Build(String Path, String name) throws Exception {
        File file = new File(Path + File.separator + ".Magit");
        if (file.exists()) {
            throw new Exception("repository allready exists in this path ");
        }
        MyRepository.BuildRepository(Path, name);
    }

    public void SwitchActiveBranch(String name) throws Exception {
        StaticMethods.BuildFile(MyRepository.getPathToHead(), name);
    }

    public void ExportDataToXml(String pathToExport) throws Exception {
        XmlExportImport xmlClass = new XmlExportImport(this);
        xmlClass.ExportXml(pathToExport);
    }

    ///////////////////////////////////  Auxiliary functions
    private LinkedList<BasicFile>[] GetChangedFilesFromWc(boolean MarkAllChanges, String note) throws Exception // will get note null in the case of show status
    {
        String Path = MyRepository.getPathToObject();
        String shawanOfWc = GetShawanFromLastCommit();
        String shawanOfShawan = null;
        try {
            shawanOfShawan = (Files.readAllLines(Paths.get(PathToActiveBranch()))).get(0);
        } catch (IOException e) {
            throw new Exception("someone deleted the root Folder from object");
        }
        Folder currentWc = GetCurrentWc();
        LinkedList<BasicFile>[] changeList = new LinkedList[3];
        for (int i = 0; i < changeList.length; i++) {
            changeList[i] = new LinkedList<>();
        }
        if (!shawanOfWc.equals("Null")) {// shawan of shawan
            Folder previousWc = (Folder) GetPreviousWc(shawanOfWc, Path, StaticMethods.GetSuffix(MyRepository.getPath()), MyRepository.getPath());

            if (MarkAllChanges) {  // if we are at commit func add the file to Obj
                BuildCommitFileAndUpdateActiveBranch(currentWc.getShawan(), shawanOfShawan, note, StaticMethods.DateToString(currentWc.getLastmodified()), currentWc.getLastchangedby(), Path, "Null");
            }
            GetChanges(changeList, currentWc, previousWc, MarkAllChanges);
            return (changeList);
        } else {

            if (MarkAllChanges) {
                BuildCommitFileAndUpdateActiveBranch(currentWc.getShawan(), shawanOfShawan, note, StaticMethods.DateToString(currentWc.getLastmodified()), currentWc.getLastchangedby(), Path, "Null");
                StaticMethods.addToChangeLIst(currentWc, changeList, 1);
            }
            for (BasicFile curr : currentWc.getContent().values())
                StaticMethods.addToChangeLIst(curr, changeList, 1);

            return changeList;
        }
    }
    public List<String>[] GetDiff(String shawanOfCommitsWc, String prevShawanOfCommit) throws Exception {

        if( prevShawanOfCommit.equals("Null"))
        {
            LinkedList<String>[] diff = new LinkedList[1];
           diff[0] = new LinkedList<>();
            diff[0].add("no prev Shawan");
            return diff;
        }
        LinkedList<BasicFile>[] diff = new LinkedList[3];
        for (int i = 0; i < diff.length; i++) {
            diff[i] = new LinkedList<>();
        }
        String Path = MyRepository.getPathToObject();
        Folder currentCommit = (Folder) GetPreviousWc(shawanOfCommitsWc,Path, StaticMethods.GetSuffix(MyRepository.getPath()), MyRepository.getPath());
        String prevCommitShawanOfWc = Files.readAllLines(Paths.get(MyRepository.getPathToObject() + File.separator + prevShawanOfCommit)).get(0).split(",")[0];
        Folder prevCommit = (Folder)  GetPreviousWc(prevCommitShawanOfWc,Path, StaticMethods.GetSuffix(MyRepository.getPath()), MyRepository.getPath()) ;
        GetChanges(diff,currentCommit,prevCommit,false);
        return StaticMethods.GetChangedFIlesByPath(diff);
    }

    public void BuildCommitFileAndUpdateActiveBranch(String shawanOfWc, String shawanOfShawan, String note, String lastModified, String lastChangedBy, String path, String prev2) throws Exception {
        String content = StaticMethods.convertToString(shawanOfWc, shawanOfShawan, note, lastModified, lastChangedBy, prev2);
        String StringToshawan = StaticMethods.convertToString(shawanOfWc, shawanOfShawan, note, prev2);
        StaticMethods.BuildFile(path + File.separator + DigestUtils.sha1Hex(StringToshawan), content); //build commit file
        File headBranch = new File(PathToActiveBranch());
        String contentOfbranch = DigestUtils.sha1Hex(StringToshawan);
        if(IsRemote())
        {

            if(CheckIfBranchIsRTB(new File(PathToActiveBranch())))
            {
                contentOfbranch = contentOfbranch + "\n" + "true";
            }
        }
        StaticMethods.BuildFile(headBranch.getPath(), contentOfbranch);
    }

    private Folder GetCurrentWc() throws Exception // Path to file
    {
        java.io.File file = new java.io.File(getMyRepository().getPath());
        return (Folder) RecGetCurrentWc(file); // we know that the recursion will return Folder
    }

    private BasicFile RecGetCurrentWc(File file) throws Exception {
        if (file.isFile()) {
            String content = null;
            try {
                content = StaticMethods.ListToString(Files.readAllLines(Paths.get(file.getPath())));
            } catch (IOException e) {
                throw new Exception("someone deleted file from Object");
            }
            return new Blob(file.getPath(), content, file.getName(), User.username, StaticMethods.GetCurrentDate()); // if the file exist in the object with the same sha-1 there is no need to put new date and username thought about it to late
        } else {
            Map<String, BasicFile> content = new TreeMap<>();
            File[] files = new File(file.getPath()).listFiles();
            if (files != null) {
                for (File curr : files) {
                    if (!(curr.getName().equals(".Magit"))) {
                        BasicFile tempFile = RecGetCurrentWc(curr);
                        content.put(tempFile.getName(), tempFile);
                    }
                }
            }
            return new Folder(file.getPath(), content, file.getName(), User.username, StaticMethods.GetCurrentDate());

        }
    }

    public BasicFile GetPreviousWc(String Shawan, String Path, String name, String filePath) throws Exception {

        File Zipfile = new File(Path + File.separator + Shawan);
        File file = (Objects.requireNonNull(Objects.requireNonNull(Zip.Unzip(Zipfile, Path + File.separator + "temp"))));
        Map<String, BasicFile> content = new TreeMap<>();
        if (!Zipfile.exists()) {
            throw new Exception("Someone deleted file in object");
        }
        List<String> childFiles;
        try {
            childFiles = Files.readAllLines(Paths.get(file.getPath()));
        } catch (IOException e) {
            throw new Exception("couldnt read content from file ");
        }
        file.delete();
        new File(Path + File.separator + "temp").delete();/////
        for (String string : childFiles) {
            BasicFile tempFile = recGetPreviousWc((string.split(",")[1]).trim(), Path, StaticMethods.WhichType(string.split(",")[2]), (string.split(",")[0]).trim(), filePath, (string.split(",")[4]).trim(), (string.split(",")[3]).trim()); //remove trim
            content.put(tempFile.getName(), tempFile);
        }
        //new File(path + File.separator + "temp").delete
        return new Folder(filePath, content, name, User.username, StaticMethods.GetCurrentDate());


    }

    private BasicFile recGetPreviousWc(String Shawan, String Path, TypeOfDoc Type, String name, String filePath, String createdate, String userCreated) throws Exception {
        File Zipfile = new File(Path + File.separator + Shawan);
        if (!Zipfile.exists()) {
            throw new Exception("Someone deleted file in object");
        }
        File file = (Objects.requireNonNull(Objects.requireNonNull(Zip.Unzip(Zipfile, Path + File.separator + "temp"))));
        if (Type == TypeOfDoc.FOLDER) {
            Map<String, BasicFile> content = new TreeMap<>();

            List<String> childFiles;
            try {
                childFiles = Files.readAllLines(Paths.get(file.getPath()));
            } catch (IOException e) {
                throw new Exception("couldnt read content from file ");

            }
            file.delete();
            new File(Path + File.separator + "temp").delete();/////
            for (String string : childFiles) {
                BasicFile tempFile = recGetPreviousWc((string.split(",")[1]).trim(), Path, StaticMethods.WhichType(string.split(",")[2]), (string.split(",")[0]).trim(), filePath + File.separator + name, (string.split(",")[4]).trim(), (string.split(",")[3]).trim()); //remove trim
                content.put(tempFile.getName(), tempFile);
            }
            return new Folder(filePath + File.separator + name, content, name, userCreated, StaticMethods.GetDateFromString(createdate));
        } else {
            String content = null;
            try {
                content = StaticMethods.ListToString(Files.readAllLines(Paths.get(file.getPath())));
            } catch (Exception e) {
                throw new Exception("couldnt read file");
            }

            file.delete();
            return new Blob(filePath + File.separator + name, content, name, userCreated, StaticMethods.GetDateFromString(createdate)); //same here StaticMethods.GetCurrentDate()
        }

    }

    public void GetChanges(LinkedList<BasicFile>[] changeList, Folder dirty, Folder clean, boolean MarkAllChanges) //if there are files that havn't change or files that was delited
    //  the files that we should enter to the list are the files from the last Wc
    {
        if (dirty.getShawan().equals(clean.getShawan())) {
            return;
        }

        for (String name : dirty.getContent().keySet()) {
            BasicFile fileDirty = dirty.getContent().get(name);
            BasicFile fileClean = clean.getContent().get(name);
            if (fileClean == null) {
                StaticMethods.addToChangeLIst(fileDirty, changeList, 1);
            } else if (fileDirty.GetType() == TypeOfDoc.BLOB) {
                if (!(fileDirty.getShawan().equals(fileClean.getShawan()))) {
                    changeList[0].add(fileDirty);
                }
            } else if (!(fileDirty.getShawan().equals(fileClean.getShawan()))) {
//                if (MarkAllChanges) {
//                    changeList[0].add(fileDirty);  // probably we dont need that
//                }
                GetChanges(changeList, (Folder) fileDirty, (Folder) fileClean, MarkAllChanges);
            }
        }
        if (MarkAllChanges) {
            changeList[0].add(dirty);
        }  // add the Wc itself


        for (String name : clean.getContent().keySet()) {
            BasicFile fileDirty = dirty.getContent().get(name);
            BasicFile fileClean = clean.getContent().get(name);
            if (fileDirty == null) {
                StaticMethods.addToChangeLIst(fileClean, changeList, 2);
            }
        }
    }

    private String GetShawanFromLastCommit() throws Exception {
        return (getShawanFromBranch(PathToActiveBranch()));
    }

    private String getShawanFromBranch(String pathToBranch) throws Exception {
        String commit;

        try {
            commit = (Files.readAllLines(Paths.get(pathToBranch))).get(0);
            if (!commit.equals("Null")) {
                return (Files.readAllLines(Paths.get(MyRepository.getPathToObject() + File.separator + commit))).get(0).split(",")[0];
            }
        } catch (IOException e) {
            throw new Exception("Master branch not found");
        }

        return "Null";
    }

    public String getCommitByBranchName(String name) throws IOException {
        String Path = MyRepository.getPathToBranches();
        File file = new File(Path);
        for (File branches : file.listFiles()) {
            if (branches.getName().equals(name)) {
                return GetShawanOfBranch(branches.getPath());
            }

        }
        return "Null";
    }

    public String GetShawanOfBranch(String pathToBranch) throws IOException {
        String commit;
        commit = (Files.readAllLines(Paths.get(pathToBranch))).get(0);
        return commit;
    }

    public void CommitFiles(LinkedList<BasicFile>[] FilesToCommit, String Path) {
        for (int i = 0; i < FilesToCommit.length - 1; i++) //no need to commit the delited files
        {
            for (int j = 0; j < FilesToCommit[i].size(); j++) {
                BasicFile TempFile = FilesToCommit[i].get(j);
                if (TempFile.GetType() == TypeOfDoc.FOLDER) {
                    Zip.CreateDirectoryZip(((Folder) TempFile).getContent(), TempFile.getName(), TempFile.getPath(), Path);
                } else {
                    Zip.CreatefileZip(Path, TempFile.getShawan(), TempFile.getName(), TempFile.getPath());
                }
            }
        }
    }

    public String PathToActiveBranch() throws Exception {
        String nameOfActiveBranch;

        nameOfActiveBranch = (Files.readAllLines(Paths.get(MyRepository.getPathToHead()))).get(0);//
        return MyRepository.getPathToBranches() + File.separator + nameOfActiveBranch;
    }

    public File[] GetAllBranches() {
        String Path = MyRepository.getPathToBranches();
        File file = new File(Path);
        return file.listFiles();
    }

    public void ListallPrevCommits(List<String> allCommits, String commit) throws IOException {
        String allDetails = Files.readAllLines(Paths.get(MyRepository.getPathToObject() + File.separator + commit)).get(0);
        allCommits.add(StaticMethods.convertToString(commit, allDetails.split(",")[2], allDetails.split(",")[3], allDetails.split(",")[4]));
        if (allDetails.split(",")[1].equals("Null")) {
            return;
        } else if (allDetails.split(",")[5].equals("Null")) {
            ListallPrevCommits(allCommits, allDetails.split(",")[1]);
        } else {
            ListallPrevCommits(allCommits, allDetails.split(",")[1]);
            ListallPrevCommits(allCommits, allDetails.split(",")[5]);
        }

    }

    public void DeleteWc(String path) {
        File Wc = new File(path);
        File[] subDirectories = Wc.listFiles();
        for (File file : subDirectories) {
            if (!file.getName().equals(".Magit")) {
                RecDeleteDirectory(file);
            }
        }

    }

    private void Deleterepository(String path) // change
    {
        File Wc = new File(path);
        File[] subDirectories = Wc.listFiles();
        for (File file : subDirectories) {
            {
                RecDeleteDirectory(file);
            }
        }
    }

    private void RecDeleteDirectory(File fileToDelete) {
        if (fileToDelete.isDirectory()) {
            File[] subDirectories = fileToDelete.listFiles();
            if (subDirectories != null) {
                for (File file : subDirectories) {
                    {
                        RecDeleteDirectory(file);
                    }
                }
            }
        }

        fileToDelete.delete();

    }

    public void DeployBranch(File BranchToDeploy) throws Exception // used from outside
    {
        String pathToObject = MyRepository.getPathToObject();
        String commitToDeploy = getShawanFromBranch(BranchToDeploy.getPath());
        if(commitToDeploy.equals("Null")) // no commits from this branch
        {
            return;
        }
        Folder WcToDeploy = (Folder) GetPreviousWc(commitToDeploy, pathToObject, StaticMethods.GetSuffix(MyRepository.getPath()), MyRepository.getPath());
        for (BasicFile file : WcToDeploy.getContent().values())  // send all the content of the Wc
        {
            RecDeployBranch(file, MyRepository.getPath());
        }
    }

    public void RecDeployBranch(BasicFile file, String pathToBuildDirectory) throws Exception // used from outside
    {
        if (file.GetType() == TypeOfDoc.FOLDER) {
            StaticMethods.BuildDir(pathToBuildDirectory + File.separator + file.getName());
            for (BasicFile curr : ((Folder) file).getContent().values()) {
                RecDeployBranch(curr, pathToBuildDirectory + File.separator + file.getName());
            }

        } else {
            StaticMethods.BuildFile(pathToBuildDirectory + File.separator + file.getName(), ((Blob) file).getContent());
        }
    }


    private void Checkifworkingrepository() throws Exception {
        if (MyRepository.getPath() == null) {
            throw new Exception(" please Build or choose repository to work on  ");
        }
    }

    /////////////////////////////////////////////////////// functions for UI ):
    public List<String> MakeDetailsForTable() throws IOException {
        List<String> details = new ArrayList<>();
        List<String> commits = new ArrayList<>();

        File[] allBranches = GetAllBranches();
        for (File branch : allBranches)  //  crate all commits who have a branch that points to them
        {
            if (!branch.getName().equals("Head") & branch.isFile()) {  // if the branch isnt a file than its the directory of RB witch we'll dill with it later
                String commit = Files.readAllLines(Paths.get(branch.getPath())).get(0);
                if (commit.equals("Null")) {
                    details.add(branch.getName());
                } else {
                    String[] allDetails = Files.readAllLines(Paths.get(MyRepository.getPathToObject() + File.separator + commit)).get(0).split(",");
                    details.add(StaticMethods.convertToString(branch.getName(), allDetails[2], allDetails[3], commit));
                    commits.add(commit);
                }
            }
        }
        if (IsRemote())   // only for remote repo
        {
            String remoteRepo = GetRemoteRepoDetails().get(1);
            File[] allRB = new File(MyRepository.getPathToBranches() + File.separator + remoteRepo).listFiles();
            for (File RB : allRB) {
                String commit = Files.readAllLines(Paths.get(RB.getPath())).get(0);
                if (commit.equals("Null")) {
                    details.add(remoteRepo + File.separator + RB.getName());
                } else {
                    String[] allDetails = Files.readAllLines(Paths.get(MyRepository.getPathToObject() + File.separator + commit)).get(0).split(",");
                    details.add(StaticMethods.convertToString(remoteRepo + File.separator + RB.getName(), allDetails[2], allDetails[3], commit));
                    commits.add(commit);
                }

            }
        }
        for (File branch : allBranches)  //  create all commits who have a branch that points to them
        {
            if (!branch.getName().equals("Head")&branch.isFile()) {
                String commit = Files.readAllLines(Paths.get(branch.getPath())).get(0);
                if (!commit.equals("Null"))
                    findCommitsThatNoBranchPointsToThem(details, commits, commit);
            }
        }
        if (IsRemote())   // only for remote repo
        {
            String remoteRepo = GetRemoteRepoDetails().get(1);
            File[] allRB = new File(MyRepository.getPathToBranches() + File.separator + remoteRepo).listFiles();
            for (File RB : allRB) {
                String commit = Files.readAllLines(Paths.get(RB.getPath())).get(0);
                if (!commit.equals("Null"))
                    findCommitsThatNoBranchPointsToThem(details, commits, commit);
            }
        }
        return details;
    }

    public List<String> getPrevCommitShawan(String commit) throws IOException {
        List<String> prevs = new LinkedList<>();
        String[] details = Files.readAllLines(Paths.get(MyRepository.getPathToObject() + File.separator + commit)).get(0).split(",");
        if (!details[1].equals("Null")) {
            prevs.add(details[1]);
            if (!details[5].equals("Null")) {
                prevs.add(details[5]);
            }
        }
        return prevs;
    }


    public Map<String, String> wrapFindCommits(List<String> branches) throws IOException {
        Map<String, String> result = new TreeMap<>((o1, o2) -> {
            try {
                Date date_o1 = StaticMethods.GetDateFromString(
                        Files.readAllLines(Paths.get(MyRepository.getPathToObject() + File.separator + o1)).get(0).split(",")[3]);
                Date date_o2 = StaticMethods.GetDateFromString(
                        Files.readAllLines(Paths.get(MyRepository.getPathToObject() + File.separator + o2)).get(0).split(",")[3]);
                return date_o1.compareTo(date_o2);
            } catch (IOException e) {
                return -1;
            }
        });
        for (String branch : branches) {
            List<String> commits = new LinkedList<>();
           String branchShawan = GetShawanOfBranch(MyRepository.getPathToBranches() + File.separator + branch);
           if(!branchShawan.equals("Null"))
           {
               ListallPrevCommits(commits, branchShawan);
               for (String commit : commits) {
                   result.put(commit.split(",")[0], branch);
               }
            }

        }

        return result;
    }

    private void findCommitsThatNoBranchPointsToThem(List<String> details, List<String> commits, String commit) throws IOException {
        String[] commitDetails = Files.readAllLines(Paths.get(MyRepository.getPathToObject() + File.separator + commit)).get(0).split(",");

        if (!commits.contains(commit)) {
            details.add(StaticMethods.convertToString(commitDetails[2], commitDetails[3], commit));
            commits.add(commit);
        }
        if (commitDetails[1].equals("Null")) {
            return;
        }
        findCommitsThatNoBranchPointsToThem(details, commits, commitDetails[1]);
    }

    public String ShowDetailsOfCommit(String shawan) throws IOException {
        String[] commitDetails = Files.readAllLines(Paths.get(MyRepository.getPathToObject() + File.separator + shawan)).get(0).split(",");
        return StaticMethods.convertToString(commitDetails[0], commitDetails[1], commitDetails[2], commitDetails[3], commitDetails[4], commitDetails[5]);
    }

    public String getWcPointedFromCommit(String shawan) throws IOException {
        return Files.readAllLines(Paths.get(MyRepository.getPathToObject() + File.separator + shawan)).get(0).split(",")[0];
    }

    private String GetNoteFromCommit(String commit) throws IOException {
        String path = MyRepository.getPathToObject() + File.separator + commit;
        return Files.readAllLines(Paths.get(path)).get(0).split(",")[2];
    }

    public List<String> getBranchHistory(String Shawan) throws IOException {
        List<String> allCommits = new LinkedList<>(); /////////////////////////
        if(!Shawan.equals("Null"))
        {
            ListallPrevCommits(allCommits, Shawan);
        }

        return allCommits;
    }


    public mergeClassobject Merge(String name) throws Exception { //should return 4 maps and list of conflictswhich is a map of (Blob,Blob[])
        mergeClassobject conflictsAndWc = new mergeClassobject();
        AncestorFinder ancestorFinder = new AncestorFinder(new Function<String, CommitRepresentative>() {
            @Override
            public CommitRepresentative apply(String s) {
                String[] commitDetails = new String[0];
                try {
                    commitDetails = Files.readAllLines(Paths.get(MyRepository.getPathToObject() + File.separator + s)).get(0).split(",");
                    if (commitDetails[1].equals("Null")) {
                        commitDetails[1] = "";
                    }
                    if (commitDetails[5].equals("Null")) {
                        commitDetails[5] = "";
                    }
                    return new Commit(commitDetails[0], commitDetails[1], commitDetails[2], StaticMethods.GetDateFromString(commitDetails[3]), commitDetails[4], commitDetails[5]);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        });

        conflictsAndWc.setPrevCommit1(GetShawanOfBranch(PathToActiveBranch()));
        conflictsAndWc.setPrevCommit2(getCommitByBranchName(name));

        String shawanOfAncestorCommit = ancestorFinder.traceAncestor(conflictsAndWc.getPrevCommit1(), conflictsAndWc.getPrevCommit2());

         if(shawanOfAncestorCommit.equals(conflictsAndWc.getPrevCommit2()))
        {
            conflictsAndWc.setFF(true);
            return conflictsAndWc;
        }
        else if(shawanOfAncestorCommit.equals(conflictsAndWc.getPrevCommit1())){
            StaticMethods.BuildFile(PathToActiveBranch(),conflictsAndWc.getPrevCommit2());
            conflictsAndWc.setFF(true);
            conflictsAndWc.setChangeFF(true);
            return conflictsAndWc;
        }
        conflictsAndWc.setAncestorWc((Folder) GetPreviousWc(getWcPointedFromCommit(shawanOfAncestorCommit), MyRepository.getPathToObject(), StaticMethods.GetSuffix(MyRepository.getPath()), MyRepository.getPath()));
        conflictsAndWc.setOursWc((Folder) GetPreviousWc(getWcPointedFromCommit(conflictsAndWc.getPrevCommit1()), MyRepository.getPathToObject(), StaticMethods.GetSuffix(MyRepository.getPath()), MyRepository.getPath()));
        conflictsAndWc.setTheirsWc((Folder) GetPreviousWc(getWcPointedFromCommit(conflictsAndWc.getPrevCommit2()), MyRepository.getPathToObject(), StaticMethods.GetSuffix(MyRepository.getPath()), MyRepository.getPath()));
        return SetConflictsAndWc(conflictsAndWc);
    }

    private mergeClassobject SetConflictsAndWc(mergeClassobject conflictsAndWc) {
        conflictsAndWc.setNewWc(MergeMaps(conflictsAndWc.getOursWc(), MergeMaps(conflictsAndWc.getAncestorWc(), conflictsAndWc.getTheirsWc())));
        MakeConflictList(conflictsAndWc.getConflictMap(), conflictsAndWc.getAncestorWc(), conflictsAndWc.getTheirsWc(), conflictsAndWc.getOursWc(), conflictsAndWc.getNewWc());
        SolveConflictsWithoutUser(conflictsAndWc.getConflictMap(), conflictsAndWc.getFilesToDelete());
        return conflictsAndWc;
    }

    private Folder MergeMaps(Folder ancestorWc, Folder theirsWc) {
        Map<String, BasicFile> content = new TreeMap<>();

        if (ancestorWc.getShawan().equals(theirsWc.getShawan())) {
            return (Folder) StaticMethods.CopyBasicFile(ancestorWc);//return ancestorWc; // copy
        }

        for (String name : ancestorWc.getContent().keySet()) {
            BasicFile ancestorFile = ancestorWc.getContent().get(name);
            BasicFile theirsFile = theirsWc.getContent().get(name);
            if (theirsFile == null) {
                BasicFile file = StaticMethods.CopyBasicFile(ancestorFile);
                content.put(file.getName(), file);//content.put(ancestorFile.getName(), ancestorFile); // copy
            } else if (ancestorFile.GetType() == theirsFile.GetType() & theirsFile.GetType() == TypeOfDoc.FOLDER) {
                Folder changedFolder = (Folder) StaticMethods.CopyBasicFile(MergeMaps((Folder) ancestorFile, (Folder) theirsFile)); //MergeMaps((Folder) ancestorFile, (Folder) theirsFile); //
                content.put(changedFolder.getName(), changedFolder); //copy
            } else {
                BasicFile file = StaticMethods.CopyBasicFile(ancestorFile);
                content.put(file.getName(), file);// content.put(ancestorFile.getName(), ancestorFile); // copy ancestor file
            }
        }
        for (String name : theirsWc.getContent().keySet()) {
            BasicFile theirsFile = theirsWc.getContent().get(name);
            BasicFile ancestorFile = ancestorWc.getContent().get(name);
            if (ancestorFile == null) {
                BasicFile file = StaticMethods.CopyBasicFile(theirsFile);
                content.put(file.getName(), file);  // content.put(theirsFile.getName(), theirsFile); // copy
            }
        }

        return new Folder(ancestorWc.getPath(), content, ancestorWc.getName(), ancestorWc.getLastchangedby(), ancestorWc.getLastmodified());

    }

    private void MakeConflictList(Map<Blob, Blob[]> conflictMap, Folder ancestor, Folder theirs, Folder ours, Folder newFolder) {
        if(ancestor !=null & theirs != null & ours!=null)
        { if (ancestor.getShawan().equals(theirs.getShawan()) & ancestor.getShawan().equals(ours.getShawan())) {
            return;
        }

        }

        for (String name : newFolder.getContent().keySet()) {
            BasicFile newFile = newFolder.getContent().get(name);
            BasicFile ancestorFile;
            BasicFile theirsFile;
            BasicFile oursFile;
            if (!(ancestor == null)) {
                ancestorFile = ancestor.getContent().get(name);
            } else {
                ancestorFile = null;
            }
            if (!(theirs == null)) {
                theirsFile = theirs.getContent().get(name);
            } else {
                theirsFile = null;
            }
            if (!(ours == null)) {
                oursFile = ours.getContent().get(name);
            } else {
                oursFile = null;
            }

            if (newFile.GetType() == TypeOfDoc.BLOB) {
                conflictMap.put((Blob) newFile, new Blob[]{(Blob) ancestorFile, (Blob) theirsFile, (Blob) oursFile});
            } else {
                MakeConflictList(conflictMap, (Folder) ancestorFile, (Folder) theirsFile, (Folder) oursFile, (Folder) newFile);
            }
        }
    }

    private void SolveConflictsWithoutUser(Map<Blob, Blob[]> conflictMap, List<Blob> filesToDelete) {
        List<Blob> conflictsSolved = new ArrayList();
        for (Blob newFile : conflictMap.keySet()) {
            Blob[] currentConflict = conflictMap.get(newFile);
            if (currentConflict[0] == null)   // at least one of the blobs must not be null so if there are 2 blobs that are null we will want the third
            {
                if (currentConflict[1] == null) {
                    StaticMethods.CopyBlob(newFile, currentConflict[2]);
                    conflictsSolved.add(newFile);
                } else if (currentConflict[2] == null) {
                    StaticMethods.CopyBlob(newFile, currentConflict[1]);
                    conflictsSolved.add(newFile);
                }
            } else if (currentConflict[1] == null) {
                if(currentConflict[2] != null)
                {
                    if ( currentConflict[2].getShawan().equals(currentConflict[0].getShawan())) {
                    filesToDelete.add(newFile);
                    conflictsSolved.add((newFile));
                    }
                }

            } else if (currentConflict[2] == null) {
                if (currentConflict[1].getShawan().equals(currentConflict[0].getShawan())) {
                    filesToDelete.add(newFile);
                    conflictsSolved.add((newFile));
                }
            } else {
                if (currentConflict[0].getShawan().equals(currentConflict[1]) & currentConflict[0].getShawan().equals(currentConflict[2].getShawan())) {
                    conflictsSolved.add((newFile));
                } else if (currentConflict[0].getShawan().equals(currentConflict[1].getShawan())) {
                    StaticMethods.CopyBlob(newFile, currentConflict[2]);
                    conflictsSolved.add(newFile);
                } else if (currentConflict[0].getShawan().equals(currentConflict[2].getShawan())) {
                    StaticMethods.CopyBlob(newFile, currentConflict[1]);
                    conflictsSolved.add(newFile);
                }
            }
        }
        for (Blob newFile : conflictsSolved) {
            conflictMap.remove(newFile);
        }
    }

    public void DeleteFiles(Folder folder, List<Blob> filesToDelete) {
        changeContentToDeletedFiles(filesToDelete);
        Map <String,Folder> deleteItems = new HashMap<>();
        recDeleteFiles(folder,deleteItems);
        deleteItemsFromFolder(deleteItems);
        Map <String,Folder> deleteFolders = new HashMap<>();
        deleteItems = deleteEmptyFolders(folder,deleteFolders);
        deleteItemsFromFolder(deleteItems);

    }


    private void recDeleteFiles(Folder folder,Map <String,Folder> deleteItems) {

        for (String name : folder.getContent().keySet()) {
            BasicFile basicFile =folder.getContent().get(name);
            if (basicFile.GetType() == TypeOfDoc.FOLDER) {
                recDeleteFiles((Folder) basicFile,deleteItems);
            } else {
                if (((Blob) basicFile).getContent().equals("Tim Buchbinder 313419814 deleted this file")) {
                    deleteItems.put(name,folder);
                }
            }
        }

    }

    private  Map<String,Folder>  deleteEmptyFolders(Folder folder,Map <String,Folder> deleteFolders) {

        for (String name : folder.getContent().keySet()) {
            BasicFile basicFile = folder.getContent().get(name);
            if (basicFile.GetType() == TypeOfDoc.FOLDER) {
                deleteFolders = deleteEmptyFolders((Folder) basicFile,deleteFolders);
                deleteItemsFromFolder(deleteFolders);
                deleteFolders.clear();
                if (((Folder) basicFile).getContent().size() == 0) {
                    deleteFolders.put(name,folder);
                   // folder.getContent().remove(basicFile);
                }

            } else {
                basicFile.setShawan(DigestUtils.sha1Hex(((Blob) basicFile).getContent()));
            }
        }
        folder.setShawan(StaticMethods.ContentToShawan(folder.getContent()));
        return deleteFolders;
    }

    private void deleteItemsFromFolder(Map<String, Folder> deleteItems) {
        for(String name : deleteItems.keySet())
        {
            Folder itemToDelete = deleteItems.get(name);
            itemToDelete.getContent().remove(name);
            itemToDelete.setShawan(StaticMethods.ContentToShawan(itemToDelete.getContent()));
        }
    }

    private void changeContentToDeletedFiles(List<Blob> filesToDelete) {
        for (Blob file : filesToDelete)
            file.setContent("Tim Buchbinder 313419814 deleted this file");
    }

    public void clone(String path, String name) throws Exception {
        File activeBranch = new File(PathToActiveBranch());
        FileUtils.copyDirectory(new File(MyRepository.getPath()), new File(path));
        String remoteRepoName = Files.readAllLines(Paths.get(path + File.separator + ".Magit" + File.separator + "name")).get(0);
        StaticMethods.BuildDir(path + File.separator + ".Magit" + File.separator + "Branches" + File.separator + remoteRepoName);
        File file = new File(path + File.separator + ".Magit" + File.separator + "Branches");
        File[] files = file.listFiles();
        for (File branch : files) {
            if (branch.isFile() & !branch.getName().equals("Head")) {
                FileUtils.copyFileToDirectory(branch,
                        new File(path + File.separator + ".Magit" + File.separator + "Branches" + File.separator + remoteRepoName));
                branch.delete();
            }


        }
        String content = Files.readAllLines(Paths.get(PathToActiveBranch())).get(0) + "\n" + "true";
        StaticMethods.BuildFile(path + File.separator + ".Magit" + File.separator + "Branches" + File.separator + activeBranch.getName(), content); //RTB
        StaticMethods.BuildFile(path + File.separator + ".Magit" + File.separator + "RemoteRepo", MyRepository.getPath() + "\n" + remoteRepoName); // may be the name of the remote repository is needed as well
        StaticMethods.BuildFile(path + File.separator + ".Magit" + File.separator + "name", name);
    }

    public void fetch() throws IOException {
        List<String> remoteRepoDetails = GetRemoteRepoDetails();
        String nameOfRemoteRepository = remoteRepoDetails.get(1);
        String pathOfRemoteRepository = remoteRepoDetails.get(0);
        String pathToBranchesInRemoteRepository = pathOfRemoteRepository + File.separator + ".Magit" + File.separator + "Branches";
        String pathToObjectInRemoteRepository = pathOfRemoteRepository + File.separator + ".Magit" + File.separator + "Object";
        String RBBranches = MyRepository.getPathToBranches() + File.separator + nameOfRemoteRepository;
        File[] BranchesInRemoteRepository = new File(pathToBranchesInRemoteRepository).listFiles();
        File[] ObjectInRemoteRepository = new File(pathToObjectInRemoteRepository).listFiles();
        for (File RB : BranchesInRemoteRepository) {
            if (!RB.getName().equals("Head"))
                FileUtils.copyFileToDirectory(RB, new File(RBBranches));
        }
        for (File file : ObjectInRemoteRepository) {
            if(file.isFile())
            {
                FileUtils.copyFileToDirectory(file, new File(MyRepository.getPathToObject()));
            }

        }
    }

    public boolean IsRemote() {
        File file = new File(MyRepository.getPath() + File.separator + ".Magit" + File.separator + "RemoteRepo");
        return file.exists();
    }

    public List<String> GetRemoteRepoDetails() throws IOException {

        return Files.readAllLines(Paths.get(MyRepository.getPath() + File.separator + ".Magit" + File.separator + "RemoteRepo"));
    }

    public boolean CheckIfRtbHaveNewData(File RTB) throws IOException {
        List<String> remoteRepoDetails = GetRemoteRepoDetails();
        File RB = new File(MyRepository.getPathToBranches() + File.separator + remoteRepoDetails.get(1) + File.separator + RTB.getName());
        String RBcommit = Files.readAllLines(Paths.get(RB.getPath())).get(0);
        String RTBcommit = Files.readAllLines(Paths.get(RTB.getPath())).get(0);
        List<String> RBHistory = new LinkedList<>();
        ListallPrevCommits(RBHistory, RBcommit);
        String allDetails = Files.readAllLines(Paths.get(MyRepository.getPathToObject() + File.separator + RTBcommit)).get(0);
        String RTBDetails = StaticMethods.convertToString(RTBcommit, allDetails.split(",")[2], allDetails.split(",")[3], allDetails.split(",")[4]);
        return RBHistory.contains(RTBDetails);
    }

    // pull
    public void pull(File RTB) throws Exception {
        List<String> remoteRepoDetails = GetRemoteRepoDetails();
        File RB = new File(MyRepository.getPathToBranches() + File.separator + remoteRepoDetails.get(1) + File.separator + RTB.getName());
        String RBcommit = Files.readAllLines(Paths.get(RB.getPath())).get(0);
        File RTBinRemoteRepo = new File(remoteRepoDetails.get(0) + File.separator + ".Magit" + File.separator + "Branches" + File.separator + RTB.getName());
        String commitFromRemoteBranch = Files.readAllLines(Paths.get(RTBinRemoteRepo.getPath())).get(0);
        RecPull(RBcommit, commitFromRemoteBranch, remoteRepoDetails.get(0) + File.separator + ".Magit" + File.separator + "Object", MyRepository.getPathToObject());
        StaticMethods.BuildFile(RB.getPath(), commitFromRemoteBranch);
        StaticMethods.BuildFile(RTB.getPath(), commitFromRemoteBranch + "\n" +"true"); // build it with true
        DeleteWc(MyRepository.getPath());
        DeployBranch(RTB);
    }

    private void RecPull(String commitFromRB, String commitFromRemoteBranch, String pathToremoteObject, String pathToLocalObject) throws Exception {
        if (commitFromRemoteBranch.equals(commitFromRB) | commitFromRemoteBranch.equals("Null")) {
            return;
        }

        String[] allDetails = Files.readAllLines(Paths.get(pathToremoteObject + File.separator + commitFromRemoteBranch)).get(0).split(",");
        FileUtils.copyFileToDirectory(new File(pathToremoteObject + File.separator + commitFromRemoteBranch), new File(pathToLocalObject)); // copy commit
        copyWcToObject(pathToremoteObject, pathToLocalObject, allDetails[0], TypeOfDoc.FOLDER); // copy Wc

        RecPull(commitFromRB, allDetails[1], pathToremoteObject, pathToLocalObject);
        RecPull(commitFromRB, allDetails[5], pathToremoteObject, pathToLocalObject);
    }

    private void copyWcToObject(String pathToremoteObject, String pathToLocalObject, String folder, TypeOfDoc type) throws Exception {
        FileUtils.copyFileToDirectory(new File(pathToremoteObject + File.separator + folder), new File(pathToLocalObject));
        File Zipfile = new File(pathToremoteObject + File.separator + folder);
        if (!Zipfile.exists()) {
            throw new Exception("Someone deleted file in object"); // needs to change

        }
        File file = (Objects.requireNonNull(Objects.requireNonNull(Zip.Unzip(Zipfile, pathToremoteObject + File.separator + "temp"))));
        if (type == TypeOfDoc.FOLDER) {
            List<String> childFiles;
            childFiles = Files.readAllLines(Paths.get(file.getPath()));
            file.delete();
            for (String string : childFiles) {

                copyWcToObject(pathToremoteObject, pathToLocalObject, string.split(",")[1].trim(), StaticMethods.WhichType(string.split(",")[2]));

            }
        }
        else{
            file.delete();
        }
    }

    // end pull
    //push
    public boolean CheckIfRemoteBranchHaveNewData(File RTB) throws IOException {
        List<String> remoteRepoDetails = GetRemoteRepoDetails();
        File RB = new File(MyRepository.getPathToBranches() + File.separator + remoteRepoDetails.get(1) + File.separator + RTB.getName());
        String RBcommit = Files.readAllLines(Paths.get(RB.getPath())).get(0);
        File RTBinRemoteRepo = new File(remoteRepoDetails.get(0) + File.separator + ".Magit" + File.separator + "Branches" + File.separator + RTB.getName());
        String commitFromREmoteBranch = Files.readAllLines(Paths.get(RTBinRemoteRepo.getPath())).get(0);
        return RBcommit.equals(commitFromREmoteBranch);
    }

    public void push(File RTB) throws Exception {
        List<String> remoteRepoDetails = GetRemoteRepoDetails();
        File RB = new File(MyRepository.getPathToBranches() + File.separator + remoteRepoDetails.get(1) + File.separator + RTB.getName());
        String RTBcommit = Files.readAllLines(Paths.get(RTB.getPath())).get(0);
        File RTBinRemoteRepo = new File(remoteRepoDetails.get(0) + File.separator + ".Magit" + File.separator + "Branches" + File.separator + RTB.getName());
        String commitFromREmoteBranch = Files.readAllLines(Paths.get(RTBinRemoteRepo.getPath())).get(0);
        //
        RecPush(RTBcommit, commitFromREmoteBranch, remoteRepoDetails.get(0) + File.separator + ".Magit" + File.separator + "Object", MyRepository.getPathToObject());
        StaticMethods.BuildFile(RB.getPath(), RTBcommit);
        StaticMethods.BuildFile(RTBinRemoteRepo.getPath(), RTBcommit);
        String activeBranchInRemote = Files.readAllLines(Paths.get(remoteRepoDetails.get(0) + File.separator + ".Magit" + File.separator + "Branches" + File.separator + "Head")).get(0);
        if (activeBranchInRemote.equals(RTBinRemoteRepo.getName())) {
            String pathToLocalRepo = MyRepository.getPath();
            SwitchRepository(remoteRepoDetails.get(0));
           DeleteWc(remoteRepoDetails.get(0));
            DeployBranch(RTBinRemoteRepo);
            SwitchRepository(pathToLocalRepo);
        }
    }
    public void pushNotRTB(File RTB) throws Exception {
        List<String> remoteRepoDetails = GetRemoteRepoDetails();
        File RB = new File(MyRepository.getPathToBranches() + File.separator + remoteRepoDetails.get(1) + File.separator + RTB.getName());
        File RTBinRemoteRepo = new File(remoteRepoDetails.get(0) + File.separator + ".Magit" + File.separator + "Branches" + File.separator + RTB.getName());
        String RTBcommit = Files.readAllLines(Paths.get(RTB.getPath())).get(0);
        //build RR branch
        RecPush(RTBcommit,"Null",remoteRepoDetails.get(0) + File.separator + ".Magit" + File.separator + "Object", MyRepository.getPathToObject());
        StaticMethods.BuildFile(RB.getPath(), RTBcommit);
        StaticMethods.BuildFile(RTBinRemoteRepo.getPath(), RTBcommit);
        StaticMethods.BuildFile(RTB.getPath(),RTBcommit + "\n" + "true");
    }

    private void RecPush(String commitFromRTB, String commitFromRemoteBranch, String pathToremoteObject, String pathToLocalObject) throws Exception {
        if (commitFromRTB.equals(commitFromRemoteBranch) | commitFromRTB.equals("Null")) {
            return;
        }

        String[] allDetails = Files.readAllLines(Paths.get(pathToLocalObject + File.separator + commitFromRTB)).get(0).split(",");
        FileUtils.copyFileToDirectory(new File(pathToLocalObject + File.separator + commitFromRTB), new File(pathToremoteObject)); // copy commit
        copyWcToObject(pathToLocalObject, pathToremoteObject, allDetails[0], TypeOfDoc.FOLDER); // copy Wc

        RecPush(allDetails[1], commitFromRemoteBranch, pathToremoteObject, pathToLocalObject);
        RecPush(allDetails[5], commitFromRemoteBranch, pathToremoteObject, pathToLocalObject);
    }

    public List<String> getListOfBranches() throws Exception {
        File[] allBranches = GetAllBranches();
        List<String> nameOfAllBranches = new ArrayList<>();
        File headBranch = new File(PathToActiveBranch());
        for (File file : allBranches) {
            if (!file.getName().equals("Head") & !file.getName().equals(headBranch.getName())) {
                if (IsRemote()) {
                    if (!file.getName().equals(GetRemoteRepoDetails().get(1))) {
                        nameOfAllBranches.add(file.getName());
                    }
                } else {
                    nameOfAllBranches.add(file.getName());
                }
            }

        }
        return nameOfAllBranches;

    }

    public List<String> GetListOfAllRB() throws Exception {
        String remoteRepo = GetRemoteRepoDetails().get(1);
        File[] allRB = new File(MyRepository.getPathToBranches() + File.separator + GetRemoteRepoDetails().get(1)).listFiles();
        List<String> allbranches = getListOfBranches();
        List<String> nameOfAllBranches = new ArrayList<>();

        for (File RB : allRB) {
            if (!allbranches.contains(RB.getName())) ;
            nameOfAllBranches.add(remoteRepo + File.separator + RB.getName());
        }
        return nameOfAllBranches;
    }

    public List<String> GetListOfAllCommits() throws IOException {

        List<String> commits = new ArrayList<>();

        File[] allBranches = GetAllBranches();
        for (File branch : allBranches)  //  crate all commits who have a branch that points to them
        {
            if (!branch.getName().equals("Head") & branch.isFile()) {  // if the branch isnt a file than its the directory of RB witch we'll dill with it later
                String commit = Files.readAllLines(Paths.get(branch.getPath())).get(0);
                if (!commit.equals("Null")) {
                    List<String> commitsFromBranch = new ArrayList<String>();
                    ListallPrevCommits(commitsFromBranch, commit);
                    for (String name : commitsFromBranch) {
                        if (!commits.contains(name.split(",")[0])) {
                            commits.add(name.split(",")[0]);
                        }
                    }

                }
            }
        }
        if (IsRemote())   // only for remote repo
        {
            String remoteRepo = GetRemoteRepoDetails().get(1);
            File[] allRB = new File(MyRepository.getPathToBranches() + File.separator + remoteRepo).listFiles();
            for (File RB : allRB) {
                String commit = Files.readAllLines(Paths.get(RB.getPath())).get(0);
                if (!commit.equals("Null")) {
                    List<String> commitsFromBranch = new ArrayList<String>();
                    ListallPrevCommits(commitsFromBranch, commit);
                    for (String name : commitsFromBranch) {
                        if (!commits.contains(name.split(",")[0])) {
                            commits.add(name.split(",")[0]);
                        }
                    }
                }
            }
        }
        return commits;
    }
    public boolean CheckIfBranchIsRTB(File branch) throws IOException {
        List<String> details = Files.readAllLines(Paths.get(branch.getPath()));
        return details.size()>1;
    }


    public List<String> AddAllAbandonedCommits(List<String> commitList) { ///
        List<String> abandoned = new ArrayList<>();
        File[] object = new File(MyRepository.getPathToObject()).listFiles();
        for(File file :object )
        {
            if (file.isFile())
             {
                 try {
                    Files.readAllLines(Paths.get(file.getPath()));
                    if(!commitList.contains(file.getName()))
                    {
                        abandoned.add(file.getName());
                    }
                }
                catch( Exception ignored)
                {

                }
            }
        }
        return abandoned;
    }
}






































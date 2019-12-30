package backend;


import mypackage.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.*;

public class XmlExportImport {

    private  MagitRepository xmlRepo = new MagitRepository();
    private  GitManager myGitRepo;
    private final  String JAXB_XML_GAME_PACKAGE_NAME = "mypackage";

    public MagitRepository getXmlRepo() {
        return xmlRepo;
    }

    public XmlExportImport(GitManager Manager) // make constructor XmlExportImport and open instance in GitManager instead of declaring all functions static
    {
        myGitRepo = Manager;
    }

    public XmlExportImport() // make constructor XmlExportImport and open instance in GitManager instead of declaring all functions static
    {
    }

    public void XmlToGit(String pathToXml, boolean deleteRepoOrNot) throws Exception {

        InputStream inputStream = new FileInputStream(pathToXml); // need to change
        deserializeFrom(inputStream);
        if (deleteRepoOrNot) {
            myGitRepo.DeleteRepository(xmlRepo.getLocation());
        }
        loadXml();
    }

    public void deserializeFrom(InputStream in) throws Exception, JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        xmlRepo = (MagitRepository) u.unmarshal(in);
//        XmlValdiation check =new XmlValdiation();
//        check.StartChecking(xmlRepo);
    }

    private void loadXml() throws Exception { //this is section 2  in menu main loading from xml
        myGitRepo.Build(xmlRepo.getLocation(), xmlRepo.getName());
        if(xmlRepo.getMagitRemoteReference()!= null)
        {
            if(xmlRepo.getMagitRemoteReference().getName()!= null & xmlRepo.getMagitRemoteReference().getLocation()!= null) {
                StaticMethods.BuildFile(myGitRepo.getMyRepository().getPathToMagit() + File.separator + "RemoteRepo",
                        xmlRepo.getMagitRemoteReference().getLocation() + "\n" + xmlRepo.getMagitRemoteReference().getName());
                StaticMethods.BuildDir(myGitRepo.getMyRepository().getPathToBranches() + File.separator + xmlRepo.getMagitRemoteReference().getName());
            }
        }
        String pathToBranch = myGitRepo.getMyRepository().getPathToBranches();
        new File(pathToBranch + File.separator + "Master").delete();
        StaticMethods.BuildFile(pathToBranch + File.separator + "Head", xmlRepo.getMagitBranches().getHead());
        createObjectFromXml();
        myGitRepo.DeployBranch(new File(pathToBranch + File.separator + xmlRepo.getMagitBranches().getHead()));

    }
//    public void ifremote ()
//    {
//        List<MagitSingleBranch> branchesList = xmlRepo.getMagitBranches().getMagitSingleBranch();
//        // לעבור על כל הבראנצים אם הבראץ הוא RB להעביר אותו לתיקייה משלו
//        //אם הבראנץ הוא RTB להוסיף לו TRUE בקובץ
//        // להוסיף את פרטי התיקייה ה REMOTE
//    }

    private void createObjectFromXml() throws Exception {
        List<MagitSingleBranch> branchesList = xmlRepo.getMagitBranches().getMagitSingleBranch();
        Map<String, Commit> commitMap = new TreeMap<>();
        MagitCommits magitCommits = xmlRepo.getMagitCommits();

        for (MagitSingleCommit curr : magitCommits.getMagitSingleCommit()) {
            if (!commitMap.containsKey(curr.getId())) {
                BuildCommit(commitMap, curr);
            }

        }
        for (MagitSingleBranch curr : branchesList) {
          String pointedShawan = null;
           if(curr.getPointedCommit().getId().equals(""))
           {
                pointedShawan = "Null";
           }
           else
           {
                pointedShawan = commitMap.get(curr.getPointedCommit().getId()).getName();
           }

            if(curr.isIsRemote())
            {
                StaticMethods.BuildFile(myGitRepo.getMyRepository().getPathToBranches()  + File.separator + curr.getName(), pointedShawan);
            }
            else if(curr.isTracking())
            {
                StaticMethods.BuildFile(myGitRepo.getMyRepository().getPathToBranches() + File.separator  + curr.getName(), pointedShawan + "\n" + "true");
            }
            else
            {
                StaticMethods.BuildFile(myGitRepo.getMyRepository().getPathToBranches() + File.separator + curr.getName(), pointedShawan);
            }

        }

    }

    private void BuildCommit(Map<String, Commit> commitMap, MagitSingleCommit curr) throws Exception {

        String pathToObject = myGitRepo.getMyRepository().getPathToObject();
        if (curr.getPrecedingCommits() == null || curr.getPrecedingCommits().getPrecedingCommit().size() == 0) {
            {
                String rootFolderSawan = createCommit(curr.getId());
                Commit commit = new Commit(rootFolderSawan, "Null", curr.getMessage(), StaticMethods.GetDateFromString(curr.getDateOfCreation()), curr.getAuthor(),"Null");
                StaticMethods.BuildFile(pathToObject + File.separator + commit.getName(), StaticMethods.convertToString(rootFolderSawan, "Null", curr.getMessage(), curr.getDateOfCreation(), curr.getAuthor(),"Null"));
                commitMap.put(curr.getId(), commit);
            }
        } else if(curr.getPrecedingCommits().getPrecedingCommit().size() == 1) {
            MagitSingleCommit prevCommit = getSingleCommit(curr.getPrecedingCommits(),0);//
            if (!commitMap.containsKey(curr.getPrecedingCommits().getPrecedingCommit().get(0).getId())) {
                BuildCommit(commitMap, prevCommit);
            }

            String rootFolderSawan = createCommit(curr.getRootFolder().getId());
            String prevShawan = commitMap.get(curr.getPrecedingCommits().getPrecedingCommit().get(0).getId()).getName();
            Commit commit = new Commit(rootFolderSawan, prevShawan, curr.getMessage(), StaticMethods.GetDateFromString(curr.getDateOfCreation()), curr.getAuthor(),"Null");
            StaticMethods.BuildFile(pathToObject + File.separator + commit.getName(), StaticMethods.convertToString(rootFolderSawan, prevShawan, curr.getMessage(), curr.getDateOfCreation(), curr.getAuthor(),"Null"));
            commitMap.put(curr.getId(), commit);
        }
        else
        {
            MagitSingleCommit prevfirstCommit = getSingleCommit(curr.getPrecedingCommits(),0);
            MagitSingleCommit prevsecondCommit = getSingleCommit(curr.getPrecedingCommits(),1);
            if (!commitMap.containsKey(curr.getPrecedingCommits().getPrecedingCommit().get(0).getId())) {
                BuildCommit(commitMap, prevfirstCommit);
            }
            if (!commitMap.containsKey(curr.getPrecedingCommits().getPrecedingCommit().get(1).getId())) {
                BuildCommit(commitMap, prevsecondCommit);
            }
            String rootFolderSawan = createCommit(curr.getRootFolder().getId());
            String prevFirstShawan = commitMap.get(curr.getPrecedingCommits().getPrecedingCommit().get(0).getId()).getName();
            String prevsecondShawan = commitMap.get(curr.getPrecedingCommits().getPrecedingCommit().get(1).getId()).getName();
            Commit commit = new Commit(rootFolderSawan, prevFirstShawan, curr.getMessage(), StaticMethods.GetDateFromString(curr.getDateOfCreation()), curr.getAuthor(),prevsecondShawan);
            StaticMethods.BuildFile(pathToObject + File.separator + commit.getName(), StaticMethods.convertToString(rootFolderSawan, prevFirstShawan, curr.getMessage(), curr.getDateOfCreation(), curr.getAuthor(),prevsecondShawan));
            commitMap.put(curr.getId(), commit);
        }
    }

    public MagitSingleCommit getSingleCommitFromId(String id) {

        List<MagitSingleCommit> listCommit = xmlRepo.getMagitCommits().getMagitSingleCommit();

        for (MagitSingleCommit curr : listCommit) {
            if (id.equals(curr.getId())) {
                return curr;
            }
        }
        return null;

    }

    public MagitSingleFolder getSingleFolderFromId(String id) {

        List<MagitSingleFolder> listFolder = xmlRepo.getMagitFolders().getMagitSingleFolder();

        for (MagitSingleFolder curr : listFolder) {
            if (id.equals(curr.getId())) {
                return curr;
            }
        }
        return null;

    }

    public MagitBlob getSingleBlobFromId(String id) {

        List<MagitBlob> listBlob = xmlRepo.getMagitBlobs().getMagitBlob();

        for (MagitBlob curr : listBlob) {
            if (id.equals(curr.getId())) {
                return curr;
            }
        }
        return null;

    }

    private MagitSingleCommit getSingleCommit(PrecedingCommits precedingCommits, int whichCommit) {

        MagitCommits magitCommits = xmlRepo.getMagitCommits();

        for (MagitSingleCommit curr : magitCommits.getMagitSingleCommit()) {
            if (precedingCommits.getPrecedingCommit().get(whichCommit).getId().equals(curr.getId())) {
                return curr;
            }
        }
        return null;
    }

    private String createCommit(String id) throws Exception {
        String pathToObject = myGitRepo.getMyRepository().getPathToObject();
        MagitSingleFolder rootFolder = getSingleFolderFromId(id);
        Map<String, BasicFile> content = new TreeMap<>();
        for (int i = 0; i < rootFolder.getItems().getItem().size(); i++) {
            BasicFile file = recCreateCommit(rootFolder.getItems().getItem().get(i).getId(), rootFolder.getItems().getItem().get(i).getType(), xmlRepo.getLocation());
            content.put(file.getName(), file);
        }


        Folder folder = new Folder(xmlRepo.getLocation(), content, rootFolder.getName(), rootFolder.getLastUpdater(), StaticMethods.GetDateFromString(rootFolder.getLastUpdateDate()));
        Zip.CreateDirectoryZip((folder).getContent(), folder.getName(), folder.getPath(), pathToObject);
        return folder.getShawan();

    }

    private BasicFile recCreateCommit(String id, String type, String location) throws Exception {

        String pathToObject = myGitRepo.getMyRepository().getPathToObject();
        if (StaticMethods.WhichType(type) == TypeOfDoc.BLOB) {
            MagitBlob magitBlobData = getSingleBlobFromId(id);

            Blob blob = new Blob(location + File.separator + magitBlobData.getName(), magitBlobData.getContent(), magitBlobData.getName(), magitBlobData.getLastUpdater(), StaticMethods.GetDateFromString(magitBlobData.getLastUpdateDate()));
            StaticMethods.BuildFile(blob.getPath(), blob.getContent());
            Zip.CreatefileZip(pathToObject, blob.getShawan(), blob.getName(), blob.getPath());
            new File(blob.getPath()).delete();
            return blob;

        } else {
            MagitSingleFolder rootFolder = getSingleFolderFromId(id);
            StaticMethods.BuildDir(location + File.separator + rootFolder.getName());
            Map<String, BasicFile> content = new TreeMap<>();
            for (int i = 0; i < rootFolder.getItems().getItem().size(); i++) {
                BasicFile file = recCreateCommit(rootFolder.getItems().getItem().get(i).getId(), rootFolder.getItems().getItem().get(i).getType(), location + File.separator + rootFolder.getName());
                content.put(file.getName(), file);
            }


            Folder folder = new Folder(location + File.separator + rootFolder.getName(), content, rootFolder.getName(), rootFolder.getLastUpdater(), StaticMethods.GetDateFromString(rootFolder.getLastUpdateDate()));
            Zip.CreateDirectoryZip(folder.getContent(), folder.getName(), folder.getPath(), pathToObject);
            new File(folder.getPath()).delete();
            return folder;


        }

    }

    private MagitSingleCommit findSingleCommit(MagitSingleBranch.PointedCommit pointedCommit) {

        for (MagitSingleCommit curr : xmlRepo.getMagitCommits().getMagitSingleCommit()) {
            if (curr.getId().equals(pointedCommit.getId()))
                return curr;
        }
        return null;
    }

    //////////////////////////////////////////////////////// Export
    public void ExportXml(String Path) throws Exception {
        XmlOrder stockManager = new XmlOrder();
        xmlRepo.setMagitBlobs(new MagitBlobs());
        xmlRepo.setMagitFolders(new MagitFolders());
        xmlRepo.setMagitCommits(new MagitCommits());
        xmlRepo.setMagitBranches(new MagitBranches());
        xmlRepo.setLocation(myGitRepo.getMyRepository().getPath());
        File[] allBranches = myGitRepo.GetAllBranches();
        setHead();
        xmlRepo.setName(myGitRepo.getMyRepository().getName());
        xmlRepo.setName(myGitRepo.getMyRepository().getName());
        //MagitRepository xmlClass = new MagitRepository();
        for (File branch : allBranches) {
            if (!branch.getName().equals("Head")) {
                String shawanofCommit = Files.readAllLines(Paths.get(branch.getPath())).get(0);
                if (!stockManager.getCommitMap().containsKey(shawanofCommit)) {
                    BuildcommitFromObject(shawanofCommit, stockManager, xmlRepo);
                }
                MagitSingleBranch magitSingleBranch = new MagitSingleBranch();
                MagitSingleBranch.PointedCommit pointedCommit = new MagitSingleBranch.PointedCommit();
                pointedCommit.setId(stockManager.getCommitMap().get(shawanofCommit));
                magitSingleBranch.setPointedCommit(pointedCommit);
                magitSingleBranch.setName(branch.getName());
                xmlRepo.getMagitBranches().getMagitSingleBranch().add(magitSingleBranch);
            }
        }

        Marshall(xmlRepo, Path);
    }

    private void setHead() throws IOException {
        String headContent = Files.readAllLines(Paths.get(myGitRepo.getMyRepository().getPathToBranches() + File.separator + "Head")).get(0);
        xmlRepo.getMagitBranches().setHead(headContent);
    }

    private void BuildcommitFromObject(String shawanOfCommit, XmlOrder stockManager, MagitRepository xmlClass) throws IOException {
        String[] commitData = Files.readAllLines(Paths.get(myGitRepo.getMyRepository().getPathToObject() + File.separator + shawanOfCommit)).get(0).split(",");

        Commit commit = new Commit(commitData[0], commitData[1], commitData[2], StaticMethods.GetDateFromString(commitData[3]), commitData[4],"Null");

        if (!commit.getPrevShawan1().equals("Null") && !stockManager.getCommitMap().containsKey(commit.getPrevShawan1())) {
            BuildcommitFromObject(commit.getPrevShawan1(), stockManager, xmlClass);
        }
        // build the commit
        xmlClass.getMagitCommits().getMagitSingleCommit().add(createInstanceOfMagitSingleCommit(stockManager, commit, xmlClass));
        stockManager.getCommitMap().put(shawanOfCommit, Integer.toString(stockManager.getCommitCounter()));
        stockManager.commitInc();
    }

    private MagitSingleCommit createInstanceOfMagitSingleCommit(XmlOrder stockManager, Commit commit, MagitRepository xmlClass) throws IOException {
        String idOfRootFolder = BuildRootFolderFromObject(commit.getCurrentShawan(), StaticMethods.DateToString(commit.getCommitDate()), commit.getCommitMaker(), stockManager, xmlClass);
        MagitSingleCommit magitSingleCommit = new MagitSingleCommit();
        RootFolder rootFolder = new RootFolder();
        rootFolder.setId(idOfRootFolder);
        magitSingleCommit.setRootFolder(rootFolder);
        magitSingleCommit.setId(Integer.toString(stockManager.getCommitCounter()));
        magitSingleCommit.setMessage(commit.getCommitMessage());
        magitSingleCommit.setDateOfCreation(StaticMethods.DateToString(commit.getCommitDate()));
        magitSingleCommit.setAuthor(commit.getCommitMaker());
        if(!commit.getPrevShawan1().equals("Null")) {
            PrecedingCommits.PrecedingCommit idOfPrevCommit = new PrecedingCommits.PrecedingCommit();
            idOfPrevCommit.setId(stockManager.getCommitMap().get(commit.getPrevShawan1()));

            PrecedingCommits commitsPrev = new PrecedingCommits();
            commitsPrev.getPrecedingCommit().add(idOfPrevCommit);

            magitSingleCommit.setPrecedingCommits(commitsPrev);
        }
        return magitSingleCommit;
    }

    private String BuildRootFolderFromObject(String shawanOfWc, String Date, String lastUpdater, XmlOrder stockManager, MagitRepository xmlClass) throws IOException {
        File Zipfile = new File(myGitRepo.getMyRepository().getPathToObject() + File.separator + shawanOfWc);
        File file = (Objects.requireNonNull(Objects.requireNonNull(Zip.Unzip(Zipfile, myGitRepo.getMyRepository().getPathToObject() + File.separator + "temp"))));
        List<String> childFiles = Files.readAllLines(Paths.get(file.getPath()));//unzip
        file.delete();//unzip
        String id = Integer.toString(stockManager.getFolderCounter());
        stockManager.getFolderMap().put(shawanOfWc, id);
        stockManager.folderInc();
        MagitSingleFolder.Items items = new MagitSingleFolder.Items();
        for (String child : childFiles) {
            Item item = new Item();
            item.setId(recBuildRootFolderFromObject(child.split(",")[0], child.split(",")[1], child.split(",")[2], child.split(",")[3], child.split(",")[4], stockManager, xmlClass));
            item.setType(child.split(",")[2]);
            items.getItem().add(item);
        }

        MagitSingleFolder magitSingleFolder = BuildMAgitSingleFolder(id, true, items, Date, lastUpdater, "null");
        xmlClass.getMagitFolders().getMagitSingleFolder().add(magitSingleFolder);
        return id;
    }

    private String recBuildRootFolderFromObject(String name, String shawanOfFolder, String Type, String basicFileCreator, String dateOfCreate, XmlOrder stockManager, MagitRepository xmlClass) throws IOException {
        File Zipfile = new File(myGitRepo.getMyRepository().getPathToObject() + File.separator + shawanOfFolder);
        File file = (Objects.requireNonNull(Objects.requireNonNull(Zip.Unzip(Zipfile, myGitRepo.getMyRepository().getPathToObject() + File.separator + "temp"))));
        List<String> childFiles = Files.readAllLines(Paths.get(file.getPath()));//unzip
        file.delete();
        if (StaticMethods.WhichType(Type) == TypeOfDoc.BLOB) {
            if (stockManager.getBlobMap().containsKey(shawanOfFolder))
                return stockManager.getBlobMap().get(shawanOfFolder);
            else {

                String content = StaticMethods.ListToString(childFiles);
                String id = Integer.toString(stockManager.getBlobCounter());
                stockManager.getBlobMap().put(shawanOfFolder, id);
                stockManager.BlobInc();
                MagitBlob magitBlob = new MagitBlob();
                magitBlob.setId(id);
                magitBlob.setContent(content);
                magitBlob.setLastUpdateDate(dateOfCreate);
                magitBlob.setLastUpdater(basicFileCreator);
                magitBlob.setName(name);
                xmlClass.getMagitBlobs().getMagitBlob().add(magitBlob);
                return id;
            }
        } else {
            if (stockManager.getFolderMap().containsKey(shawanOfFolder))
                return stockManager.getBlobMap().get(shawanOfFolder);
            else {

                String id = Integer.toString(stockManager.getFolderCounter());
                stockManager.getFolderMap().put(shawanOfFolder, id);
                stockManager.folderInc();
                MagitSingleFolder.Items items = new MagitSingleFolder.Items();
                for (String child : childFiles) {
                    Item item = new Item();
                    item.setId(recBuildRootFolderFromObject(child.split(",")[0], child.split(",")[1], child.split(",")[2], child.split(",")[3], child.split(",")[4], stockManager, xmlClass));
                    item.setType(child.split(",")[2]);
                    items.getItem().add(item);
                }
                MagitSingleFolder magitSingleFolder = BuildMAgitSingleFolder(id, false, items, dateOfCreate, basicFileCreator, name);
                xmlClass.getMagitFolders().getMagitSingleFolder().add(magitSingleFolder);
                return id;
            }
        }
    }

    private MagitSingleFolder BuildMAgitSingleFolder(String id, boolean isRootFolder, MagitSingleFolder.Items items, String dateOfCreate, String basicFileCreator, String name) {
        MagitSingleFolder magitFolder = new MagitSingleFolder();
        magitFolder.setId(id);
        magitFolder.setIsRoot(isRootFolder);
        magitFolder.setItems(items);
        magitFolder.setLastUpdateDate(dateOfCreate);
        magitFolder.setLastUpdater(basicFileCreator);
        magitFolder.setName(name);
        return magitFolder;
    }

    private void Marshall(MagitRepository xmlClass, String pathToXML) throws JAXBException, IOException {
        OutputStream os = new FileOutputStream(pathToXML + File.separator + myGitRepo.getMyRepository().getName() + ".xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(xmlClass.getClass().getPackage().getName());
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        jaxbMarshaller.marshal(xmlClass, os);
        os.close();
    }

}

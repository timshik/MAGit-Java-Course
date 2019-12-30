package backend;


import org.apache.commons.codec.digest.DigestUtils;


import java.io.*;

import java.nio.file.Paths;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;


import static org.apache.commons.io.FileUtils.listFiles;

public class StaticMethods {

    public static void BuildDir(String Path) {
        File repoFile = new File(Path);
        try {
            repoFile.mkdir();

        } catch (Exception e) {
            System.out.println(" couldnt Build Directory/BasicFile try again (with another path)");
        }
    }

    public static void BuildFile(String Path, String content) throws Exception {
        File file = new File(Path);
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();

        } catch (Exception e) {
            throw new Exception(" couldnt Build Directory/BasicFile try again (with another path");
        } finally {
            if (file.exists()) {
            }
        }
    }

    public static File CreateFile(String content, String path) throws IOException {
        BufferedWriter writer = null;

        writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(path)));
        writer.write(content);
        if (writer != null) {
            writer.close();
        }
        return new File(path);
    }

    public static void WcToStrings(BasicFile file, List<String> historyList) {
        historyList.add(convertToString(file.getPath(), file.GetType().toString(), file.getShawan(), file.getLastmodified().toString(), file.getLastchangedby()));
        if (file.GetType() == TypeOfDoc.FOLDER) {
            for (BasicFile curr : ((Folder) file).getContent().values()) {
                WcToStrings(curr, historyList);
            }
        }
    }

    public static String ContentToString(Map<String, BasicFile> content, boolean pathorname) {
        StringBuilder contentByString = new StringBuilder();
        for (BasicFile curr : content.values()) {
            if (pathorname) {
                contentByString.append(curr.getName() + ",");
            } else {
                contentByString.append(curr.getPath() + ",");
            }
            contentByString.append(curr.getShawan() + ",");
            contentByString.append(curr.GetType() + ",");
            contentByString.append(curr.getLastchangedby() + ",");
            contentByString.append(StaticMethods.DateToString(curr.getLastmodified()));
            contentByString.append(System.getProperty("line.separator"));
        }
        return contentByString.toString();
    }

    public static String ContentToShawan(Map<String, BasicFile> content) {
        StringBuilder contentByString = new StringBuilder();
        for (BasicFile curr : content.values()) {
            contentByString.append(curr.getName() + ",");
            contentByString.append(curr.getShawan() + ",");
            contentByString.append(curr.GetType() + ",");
            contentByString.append(System.getProperty("line.separator")); //might be redundent
        }

        return DigestUtils.sha1Hex(contentByString.toString());
    }

    public static String convertToString(String... var) //bad function if will have the time must change it
    {                                                                                                                   // needs to get an array of strings  instead
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < var.length - 1; i++) {
            string.append(var[i]);
            string.append(",");
        }
        string.append(var[var.length - 1]);                                                 // and then change the methods + show history of branch
        return string.toString();
    }

    public static String ListToString(List<String> listOfStrings) {
        StringBuilder string = new StringBuilder();
        for (int i=0; i<listOfStrings.size()-1 ; i++) {
            string.append(listOfStrings.get(i));
            string.append(System.getProperty("line.separator"));

        }
        if(listOfStrings.size()!= 0) {
            string.append(listOfStrings.get(listOfStrings.size() - 1));
        }
        return string.toString();
    }

    public static void addToChangeLIst(BasicFile fileDirty, LinkedList<BasicFile>[] changeList, int i) { // will add all the files to the change list in place i
        if (fileDirty.GetType() == TypeOfDoc.BLOB) {
            changeList[i].add(fileDirty);
        } else {
            for (BasicFile curr : ((Folder) fileDirty).getContent().values()) {
                addToChangeLIst(curr, changeList, i);
            }
            changeList[i].add(fileDirty);
        }
    }


    public static String DateToString(Date date){

    Format formatter = new SimpleDateFormat("dd.MM.yyyy-hh:mm:ss:SSS");
    String s = formatter.format(date);
    return s;
}
    public static Date GetCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy-hh:mm:ss:SSS");
        Date date= new Date(System.currentTimeMillis());
       return GetDateFromString(formatter.format(date));
    }
    public static Date GetDateFromString(String date)
    {
        Date formatter = new Date();
        try {
            formatter = new SimpleDateFormat("dd.MM.yyyy-hh:mm:ss:SSS").parse(date);
        } catch (ParseException e) {
            System.out.println("the date parsed wrong");
        }
        return formatter;
    }

    public static TypeOfDoc WhichType (String Type)
    {
        if (Type.toUpperCase().equals("BLOB"))// need to delete space
        {
            return TypeOfDoc.BLOB;
        }
        return TypeOfDoc.FOLDER;
    }
    public static String GetSuffix (String Path)
    {
       String[] fullPath= (Paths.get(Path).toAbsolutePath().toString()).split(Pattern.quote(System.getProperty("file.separator")));
       return fullPath[fullPath.length-1];
    }
    public static  LinkedList <String>[] GetChangedFIlesByPath (LinkedList<BasicFile>[] ChangedFiles) {
        LinkedList<String>[] ChangedFilesByName = new LinkedList[3];
        for (int i = 0; i < ChangedFilesByName.length ; i++) {
            ChangedFilesByName[i] = new LinkedList<>();
        }
        for (int i = 0; i < ChangedFiles.length; i++) {
            for (int j = 0; j < ChangedFiles[i].size(); j++) {
                ChangedFilesByName[i].add(ChangedFiles[i].get(j).getPath());
            }
        }
        return ChangedFilesByName;
    }

    public static void CopyBlob (Blob newFile , Blob selectedBlob)
    {
        newFile.setContent(selectedBlob.getContent());
        newFile.setPath(selectedBlob.getPath());
        newFile.setLastchangedby(selectedBlob.getLastchangedby());
        newFile.setLastmodified(selectedBlob.getLastmodified());
        newFile.setName(selectedBlob.getName());
        newFile.setShawan(selectedBlob.getShawan());
    }
    public static BasicFile CopyBasicFile (BasicFile file)
    {
        if(file.GetType() == TypeOfDoc.FOLDER)
        {
            Map <String,BasicFile> content =new TreeMap<>() ;
            for(BasicFile childs : ((Folder)file).getContent().values())
            {
                BasicFile child = CopyBasicFile(childs);
                content.put(child.getName(),child);
            }
            return new Folder(file.getPath(),content,file.getName(),file.getLastchangedby(),file.getLastmodified());
        }
        else
        {
            return new Blob (file.getPath(),((Blob)file).getContent(),file.getName(),file.getLastchangedby(),file.getLastmodified());

        }
    }

}



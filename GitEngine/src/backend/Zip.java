package backend;

import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zip {

    public static void CreateDirectoryZip(Map<String,BasicFile> content,String FileName,String FilePath, String PathToDirectory)
    {
        File newFile = null;
        try {
            newFile = StaticMethods.CreateFile(StaticMethods.ContentToString(content,true),FilePath+ "\\" + FileName +".txt");
        } catch (IOException e) {
            e.printStackTrace(); //print that unable to make file
        }

        Zip.CreatefileZip(PathToDirectory,StaticMethods.ContentToShawan(content),newFile.getName(),newFile.getPath());
        newFile.delete();
    }

    public static void CreatefileZip(String PathToDirectory, String Shawan, String FileName, String FilePath) {

        byte[] buffer = new byte[1024];

        try {

            FileOutputStream fos = new FileOutputStream(PathToDirectory + File.separator +Shawan );
            ZipOutputStream zos = new ZipOutputStream(fos);
            ZipEntry ze = new ZipEntry(FileName);
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream(FilePath);

            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            in.close();
            zos.closeEntry();

            //remember close it
            zos.close();
            //System.out.println("Done");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public static File Unzip(File file, String pathToTempFolder) {
        File dir = new File(pathToTempFolder);
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(file);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(pathToTempFolder + File.separator + fileName);
                //System.out.println("Unzipping to "+newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
            return Objects.requireNonNull(dir.listFiles())[0];///////////////
        } catch (IOException e) {
            return null;
        }
    }
}





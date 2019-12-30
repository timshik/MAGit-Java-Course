//package backend;
//
//import org.apache.commons.codec.digest.DigestUtils;
//
//import java.util.*;
//
//public class Wc {
//    private String Shawan;
//    private Map<String, BasicFile> content = new TreeMap<String, BasicFile> ();
//
//    public Wc() {
//       Map<String,BasicFile> mapToBasicFile = new TreeMap<>();
//        for(String string :content.keySet())
//        {
//         mapToBasicFile.put( content.get(string));
//        }
//        Shawan = DigestUtils.sha1Hex(StaticMethods.ContentToString(mapToBasicFile));
//    }
//
//    public String getShawan() {
//        return Shawan;
//    }
//
//    public void setShawan(String shawan) {
//        Shawan = shawan;
//    }
//
//    public Map<String, BasicFile> getContent() {
//        return content;
//    }
//
//    public void setContent(Map<String, BasicFile> content) {
//        this.content = content;
//    }
//
//    public void add(String key, BasicFile basicFile) {
//        content.put(key, basicFile);
//    }
//}

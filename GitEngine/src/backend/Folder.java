package backend;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;

public class Folder extends BasicFile {

  private Map<String, BasicFile>  content;
  public Folder (String Path,Map<String, BasicFile> content, String name, String lastchangedby, Date lastmodified)
  {
      super( Path, name,lastchangedby,lastmodified);
      this.content = content;
      this.setShawan(StaticMethods.ContentToShawan(content));
  }


    public void add (String Path,BasicFile basicFile)
    {
        content.put(Path, basicFile);
    }

    public Map<String, BasicFile> getContent() {
        return content;
    }

    public void setContent(Map<String, BasicFile> content) {
        this.content = content;
    }
    @Override
    public TypeOfDoc GetType()
    {
        return TypeOfDoc.FOLDER;
    }

}

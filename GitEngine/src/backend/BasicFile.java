package backend;

import java.util.Date;


public abstract class BasicFile {
    private String Path;
    private String name;
    private String Shawan;
    private String lastchangedby;
    private Date lastmodified;  // need to change

    public BasicFile(String Path,String name, String lastchangedby, Date lastmodified) {
        this.Path = Path;
        this.name = name;
        this.lastchangedby = lastchangedby;
        this.lastmodified = lastmodified;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShawan(String shawan) { Shawan = shawan; }

    public void setLastchangedby(String lastchangedby) {
        this.lastchangedby = lastchangedby;
    }

    public void setLastmodified(Date lastmodified) {
        this.lastmodified = lastmodified;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

    public String getName() {
        return name;
    }

    public String getShawan() { return Shawan; }

    public String getLastchangedby() { return lastchangedby; }

    public Date getLastmodified() { return lastmodified; }

    public TypeOfDoc GetType() { return TypeOfDoc.NON; }


    @Override
    public int hashCode() {
        return Path.hashCode();
    }

//    @Override
//    public boolean equals(java.lang.Object o1) {
//        return this.Path.equals(((BasicFile)o1).getPath());
//    }
    @Override
    public String toString()
    {
        return name;
    }

}


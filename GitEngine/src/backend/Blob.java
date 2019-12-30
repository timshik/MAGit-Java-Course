package backend;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Date;

public class Blob extends BasicFile {
        private String content;
        public Blob (String Path,String content, String name, String lastchangedby, Date lastmodified)
        {
            super( Path, name,  lastchangedby,lastmodified);
            this.content = content;
            this.setShawan(DigestUtils.sha1Hex(content));
        }
        @Override
        public TypeOfDoc GetType ()
        {
            return TypeOfDoc.BLOB;
        }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

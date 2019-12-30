package backend;

import java.util.HashMap;

import java.util.Map;


public class Object {
    private Map<String,String> object =  new HashMap<String,String>();
    public  void BuildObj(String Path)
    {
        StaticMethods.BuildDir(Path);
    }

    public Map<String, String> getObject() { return object; }
    public void Addsha1 (String sha1, String Path)
    {
        object.put(sha1,Path);
    }

}

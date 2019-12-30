package ui;

import java.util.HashMap;
import java.util.Map;

public class Menu {
    private Map<Integer,String> MenuMap = new HashMap<Integer, String>() ;

    public Menu() {
        MenuMap.put(1, "Change user name");
        MenuMap.put(2, "Read repository file");
        MenuMap.put(3, "Switch repository");
        MenuMap.put(4, "Show current commit file system info");
        MenuMap.put(5, "Show status");
        MenuMap.put(6, "Commit");
        MenuMap.put(7, "Show all the branches in the system");
        MenuMap.put(8, "Create new branch");
        MenuMap.put(9, "Delete branch");
        MenuMap.put(10, "Checkout branch");
        MenuMap.put(11, "Show history of active branch");
        MenuMap.put(12, "open new repository");
        MenuMap.put(13, "reset active branch");
        MenuMap.put(14,"Export Xml");
        MenuMap.put(15, "Exit");

    }
    public int GetSizeOfMap()
    {
        return MenuMap.size();
    }
    public void printMenu(){

        for (int i = 1; i <= MenuMap.size(); i++) {
            int var =  i;
            System.out.println(var +"." +MenuMap.get(i));
        }

    }

}

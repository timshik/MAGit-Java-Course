package ui;



import backend.Repository;

import java.util.*;

public class MenuMethods {
    static Scanner in = new Scanner(System.in);
    static Map < Integer , String > Outputs= new HashMap<Integer, String>(){{

        put(1,"enter username");
        put(2,"enter path for your repository");
        put(3, " enter Path");
        put(4,"enter the name of your new branch");
        put(5, "enter the name of the branch you want to deploy");
        put(6, "there are changes in the Wc are you sure you want to proceed ?  (y/n)");
        put(7,"Please enter the path to your XML file");
        put(8," please enter your note");
        put(9, "enter the sha-1 of the commit you want to deploy");
        put(10,"would you like to deploy the new branch ? (y/n)");
        put(11," Enter the path you want to Export the Xml to ");
        put(12," the path entered is a path to  exist repository would you like to override? (y/n)");
        put(13," enter name for your repository");
    }};
    static String AskFor(int num)
    {
        System.out.println(Outputs.get(num));
         return(in.nextLine());
    }
    static boolean GetAnswerYesOrNo(int num)
    {
        boolean goodAnswer = true;
        do{
            System.out.println(Outputs.get(num));
            String string = (in.nextLine());
            if(string.equals("Y")||string.equals("y"))
            {
                return true;
            }
            else if(string.equals("N")||string.equals("n"))
            {
                return false;
            }
            else
            {
             goodAnswer = false;
                System.out.println("Please enter y/n");
            }
        }
        while(!goodAnswer);
        return true;
    }
     static void PrintChangedFiles (LinkedList <String>[] ListToPrint)
    {
        for(int i=0; i< ListToPrint.length; i++)
        {
            if( ListToPrint[i].size()!=0)
            {
                if (i==0){System.out.println(" the changed items are:");}
                if (i==1){System.out.println(" the added items are:");}
                if (i==2){System.out.println(" the deleted items are:");}
            }
            if(ListToPrint[i].size()==0)
            {
                System.out.println("no changes in this category");
            }
            for(int j=0; j<ListToPrint[i].size(); j++)
            {
                System.out.println(ListToPrint[i].get(j));
            }
        }
    }
   static void PrintListOfString(List <String> commitHistory, String noteIfEmpty)
   {
       if(commitHistory.size()== 0)
       {
           System.out.println(noteIfEmpty);
       }
       for (String description : commitHistory)
       {
           System.out.println(description);
       }
   }

}

package ui;

import backend.GitManager;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

import java.util.Scanner;

public class UiToEngine {
 private Scanner in = new Scanner(System.in);
    public  void StartEngine (int num, GitManager Manager)  {
        switch (num) {
            case 1:

                try {
                    Manager.ChangeUserName(MenuMethods.AskFor(1));
                } catch (Exception e) {
                    System.out.println("couldnt change name");
                    return;
                }
                System.out.println("name Changed Succesfully");
                break;
            case 2:
                String path = MenuMethods.AskFor(7);
                try {
                  if( !Manager.CheckIfXmlValid(path))
                  {
                      if(MenuMethods.GetAnswerYesOrNo(12))
                      {

                          Manager.ReadRepositoryFile(path,true);
                      }
                  }
                  else{
                      Manager.ReadRepositoryFile(path,false);
                  }

                } catch (JAXBException je) {
                    System.out.println(" couldnt find Xml file");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                break;
            case 3:
                try {
                    Manager.SwitchRepository(MenuMethods.AskFor(2));
                    System.out.println("Repository switched ");
                }catch (FileNotFoundException f)
                {
                    System.out.println("The Path entered isn't a path to repository");
                }
                catch (Exception e) {
                    System.out.println("The file: name not found");

                }
                break;
            case 4:
                try {
                    MenuMethods.PrintListOfString(Manager.ShowCurrentCommitFileSystemInfo(), "There is no commits in the System");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 5:
                try {
                    MenuMethods.PrintChangedFiles(Manager.ShowStatus());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 6:
                try {
                    Manager.Commit(MenuMethods.AskFor(8));
                    System.out.println("commit succesfull");
                } catch (Exception e) {
                    System.out.println("commit failed");
                }
                break;
            case 7:
                try {
                    MenuMethods.PrintListOfString(Manager.ShowAllTheBranchesInTheSystem(), "Somthing horrobly Failed :O");
                } catch (Exception e) {
                    System.out.println(e.getMessage());

                }
                break;
            case 8:
                String name = MenuMethods.AskFor(4);
                  try{
                      Manager.CreateNewBranch(name);
                      System.out.println("branch created succesfully");
                      if(MenuMethods.GetAnswerYesOrNo(10))
                      {
                          if (Manager.CheckIfWcIscLean()) {
                              Manager.SwitchActiveBranch(name);
                              }
                          else{
                              System.out.println("there are changes in the Wc can't deploy the branch");
                              return;
                          }
                      }
                 } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return;
                 }

                break;
            case 9:
                try {
                    Manager.DeleteBrunch(MenuMethods.AskFor(4));
                    System.out.println("branch deleted successfully");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 10:
                boolean proceed = true;
                try {
                    if (!Manager.CheckIfWcIscLean()) {
                        proceed = MenuMethods.GetAnswerYesOrNo(6);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                if (proceed) {
                    try {
                        Manager.CheckoutBranch(MenuMethods.AskFor(5));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                break;
            case 11:
                try {
                    MenuMethods.PrintListOfString(Manager.ShowHistoryOfActiveBranch(), "there is no commits in the active Branch");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                break;
            case 12:
                try {
                    Manager.Build(MenuMethods.AskFor(2),MenuMethods.AskFor(13));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            case 13:
                boolean proceed1 = true;
                try {
                    if (!Manager.CheckIfWcIscLean()) {
                        proceed1 = MenuMethods.GetAnswerYesOrNo(6);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                if (proceed1) {
                    try {
                        Manager.CheckoutBranch(Manager.GetTheCommit(MenuMethods.AskFor(9)));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                break;
            case 14:
                try {
                    Manager.ExportDataToXml(MenuMethods.AskFor(11));
                }
                catch (Exception e)
                {
                System.out.println(e.getMessage());
                }
                break;
            case 15:
                System.out.println("Bye Bye");
                System.exit(0);
                break;
        }
    }
}
// location head
package ui;



import backend.GitManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class StartMagit {
    private GitManager Manager;

    {
        try {
            Manager = new GitManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void genarateMain() {
        Menu menu = new Menu();
        Scanner input = new Scanner(System.in);
        UiToEngine uiToEngine = new UiToEngine();
        int userChoise;
        boolean GoodNumber = true;
        System.out.println("Welcome to magit menu ,choose one");
        while(true)
        {
            if (GoodNumber) { menu.printMenu(); }

        try
        {
            userChoise = input.nextInt();
            if( userChoise < 1 | userChoise > menu.GetSizeOfMap() )
            {
                System.out.println(" please choose valid number ");
                GoodNumber = false;
            }
            else
            {
                uiToEngine.StartEngine(userChoise,Manager);
                GoodNumber = true;
            }
        }
        catch (java.util.InputMismatchException e)
            {
                System.out.println(" please choose valid number ");
                input.nextLine();
                GoodNumber = false;

            }
        }
    }
}

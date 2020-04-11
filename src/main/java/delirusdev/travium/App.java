package delirusdev.travium;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class App {
    
    public static void main(String args[]) throws InterruptedException, FileNotFoundException, IOException {

        Travium bot = new Travium();
        Scanner sc;
        int input;

        do {
            System.out.println("1 - Login");
            System.out.println("2 - Go Farming");
            System.out.println("3 - Check login status");
            System.out.println("4 - Farm all night!");
            System.out.println("0 - Close Browser");
            System.out.print("What should I do next?: ");

            sc = new Scanner(System.in);
            input = Integer.parseInt(sc.nextLine());

            switch (input) {
                case 1:
                    bot.login();
                    break;
                case 2:
                    bot.goFarming();
                    break;
                case 3:
                    bot.checkLoginStatus();
                    break;
                case 4:
                    bot.farmAllNight();
                    break;
                default:
                    break;
            }
        } while(input != 0);

        sc.close();
        bot.stop();
    }
}
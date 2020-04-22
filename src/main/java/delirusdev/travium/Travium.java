package delirusdev.travium;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;

public class Travium {
    private String user;
    private String pass;
    private String world;
    private String tribe; // "romans", "gauls", "teutons"
    private String language; // "DE", "EN"
    private WebDriver browser;
    private int pauseClickMin;
    private int pauseClickMax;
    private int pauseFarmMin;
    private int pauseFarmMax;

    private ArrayList<Integer> farmorder = new ArrayList<Integer>();
    private int[][] farmlist;

    private String[] troops;

    Travium() throws FileNotFoundException, IOException{
        this.printMessage("Welcome to TBot!");

        // Init settings
        Map<String, String> settings = Common.readSettings("settings.json");
        this.user = settings.get("user");
        this.pass = settings.get("pass");
        this.world = settings.get("world");
        this.tribe = settings.get("tribe");
        this.language = settings.get("language");
        this.pauseClickMin = Integer.parseInt(settings.get("pauseClickMin"));
        this.pauseClickMax = Integer.parseInt(settings.get("pauseClickMax"));
        this.pauseFarmMin = Integer.parseInt(settings.get("pauseFarmMin"));
        this.pauseFarmMax = Integer.parseInt(settings.get("pauseFarmMin"));

        // Init troop names depend on tribe and language
        this.troops = Common.readTroopNames("troops" + this.language + ".json", this.tribe);
        
        // Init farmnames
        this.farmlist = Common.readFarmlist("farm.json");

        browser = new ChromeDriver();
    }

    private void printMessage(String message) {
        System.out.println("[" + Common.timeNow() + "] ===> " + message);
    }

    public void printSettings() {
        System.out.println(this.user + ";" + this.pass + ";" + this.world + ";" + this.tribe + ";" + this.language + ";" + this.pauseClickMin + ";" + this.pauseClickMax + ";" + this.pauseFarmMin + ";" + this.pauseFarmMax);
        for(int i = 0; i < this.troops.length; i++) {
            System.out.print(this.troops[i] + ";");
        }
        System.out.println();
    }

    private void pretendBeingHuman(int min, int max) throws InterruptedException {
        int waiting = (int)((Math.random() * ((max - min) + 1)) + min);
        this.printMessage("I'm doing nothing next: " + waiting + "ms");
        Thread.sleep(waiting);
    }

    private void shuffleFarmList() {
        for (int i = 0; i < farmlist.length; i++) {
            this.farmorder.add(i);
        }
        Collections.shuffle(farmorder);
        this.printMessage("Farmlist is shuffled!");
    }

    public void login() throws InterruptedException {
        this.printMessage("Loggin in...");
        browser.get(this.world + "login.php");
        pretendBeingHuman(this.pauseClickMin, this.pauseClickMax);
        WebElement loginField = this.browser.findElement(By.name("name"));
        WebElement passwordField = this.browser.findElement(By.name("password"));
        WebElement submitButton = this.browser.findElement(By.cssSelector("#s1"));
        pretendBeingHuman(this.pauseClickMin, this.pauseClickMax);
        loginField.sendKeys(this.user);
        pretendBeingHuman(this.pauseClickMin, this.pauseClickMax);
        passwordField.sendKeys(this.pass);
        pretendBeingHuman(this.pauseClickMin, this.pauseClickMax);
        submitButton.click();
        pretendBeingHuman(this.pauseClickMin, this.pauseClickMax);
        this.checkLoginStatus();
    }

    public boolean checkLoginStatus() throws InterruptedException {
        String[] linkList = {
            world+"dorf1.php",
            world+"berichte.php",
            world+"dorf2.php",
            world+"statistiken.php",
            world+"messages.php"
        };
        // get random link from linkList
        int randNum = (int)((Math.random() * ((4 - 0) + 1)) + 0);
        browser.get(linkList[randNum]);
        pretendBeingHuman(this.pauseClickMin, this.pauseClickMax);
        if(linkList[randNum].equals(browser.getCurrentUrl())) {
            this.printMessage("User is logged in :)");
            return true;
        } else {
            this.printMessage("User is logged out :(");
            return false;
        }
    }

    public void farmAllNight() throws InterruptedException {
        int farmCount = 0;
        while(true) {
            this.printMessage("I will farm all the night ;)");
            if(!this.checkLoginStatus()){
                this.login();
            }
            // other solution for this
            pretendBeingHuman(this.pauseClickMin, this.pauseClickMax);
            this.goFarming();
            int waiting = (int)((Math.random() * ((this.pauseFarmMax - this.pauseFarmMin) + 1)) + this.pauseFarmMin);
            while(waiting > 0) {
                waiting -= 10000;
                this.printMessage("Waiting: " + waiting + "ms");
                Thread.sleep(10000);
            }
            farmCount++;
            this.printMessage("This was iteration Nr. " + farmCount);
        }
    }

    public void goFarming() throws InterruptedException {
        browser.get(this.world + "dorf1.php");
        pretendBeingHuman(this.pauseClickMin, this.pauseClickMax);

        // create table of troops
        List<WebElement> troops = browser.findElements(By.cssSelector("#troops .num"));
        List<WebElement> troopsNames = browser.findElements(By.cssSelector("#troops .un"));
        this.printMessage("Troops elements: " + troops.size() + "\t\t" + troopsNames.size());
        Map<String,String> troopsTable = new HashMap<String,String>();
        for(int i = 0; i < troops.size(); i++) {
            troopsTable.put(troopsNames.get(i).getAttribute("innerText"), troops.get(i).getAttribute("innerText"));
        }

        // shuffle farmllist
        // pars troop names from farmlist!!!
        int amountTroops = Integer.parseInt(troopsTable.get(this.troops[3]));
        this.shuffleFarmList();
        int counter = 1;

        // go farming
        while(true) {
            pretendBeingHuman(this.pauseClickMin, this.pauseClickMax);
            if(amountTroops > 1 && counter < this.farmlist.length) {
                // here is troops names too
                this.printMessage(this.troops[3] + ": " + amountTroops);
                this.printMessage("Visit farm: " + counter + "/" + this.farmlist.length);
                this.printMessage("Enough troops, happy farming... :)");
                this.sendTroops(this.farmlist[this.farmorder.get(counter)][0], this.farmlist[this.farmorder.get(counter)][1], this.farmlist[this.farmorder.get(counter)][2], this.farmlist[this.farmorder.get(counter)][3]);
                amountTroops -= this.farmlist[this.farmorder.get(counter)][3];
                counter++;
            } else {
                this.printMessage("Not enough troops, staying at home... :(");
                break;
            }
        }
    }

    public void sendTroops(int x, int y, int troopsUnit, int troopsAmount) throws InterruptedException {
        String link = this.world + "build.php?tt=2&id=39&troops[0][t" + troopsUnit + "]=" + troopsAmount + "&x=" + x + "&y=" + y + "&c=4";
        pretendBeingHuman(this.pauseClickMin, this.pauseClickMax);
        this.printMessage("Open link: " + link);
        browser.get(link);
        pretendBeingHuman(this.pauseClickMin, this.pauseClickMax);
        this.printMessage("Sending troops");
        WebElement submitButton = this.browser.findElement(By.name("s1"));
        submitButton.click();
        pretendBeingHuman(this.pauseClickMin, this.pauseClickMax);
        // TODO: if class="error" not found
        try {
            WebElement submitButton2 = this.browser.findElement(By.name("a"));
            submitButton2.click();
        } catch(Exception e) {
            this.printMessage("!!!!!!!!!!!!!!!!!The farm is inactive: " + link);
        }
        
    }

    public void stop() {
        this.printMessage("See you later!");
        this.browser.quit();
    }
}

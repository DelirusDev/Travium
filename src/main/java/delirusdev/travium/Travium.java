package delirusdev.travium;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;

public class Travium {

    private String user = "Username";
    private String pass = "Password";
	// Loginpage
    private String world = "https://ts1.travian.de/";
    private WebDriver browser;
    private int pauseClickMin = 500; // 0,5 Seconds
    private int pauseClickMax = 6000; // 6 Seconds
    private int pauseFarmMin = 1200000; // 20 Minutes
    private int pauseFarmMax = 1800000; // 30 Minutes
    private ArrayList<Integer> farmorder = new ArrayList<Integer>();
    private int[][] farmlist = {
		// {x,y,troopsId,amount}
        {-3,5,1,5},
        {-5,0,1,5},
        {-7,-4,1,5}
		// ...
    };

    Travium() {
        this.printMessage("Welcome to TBot!");
        browser = new ChromeDriver();
    }

    private void printMessage(String message) {
        System.out.println("===> " + message);
    }

    private void pretendBeingHuman(int min, int max) throws InterruptedException {
        int waiting = (int)((Math.random() * ((max - min) + 1)) + min);
        this.printMessage("I'm doing nothing the next: " + waiting + "ms");
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
        pretendBeingHuman(this.pauseClickMin, this.pauseClickMax);  // Let the user actually see something!
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
        int amountTroops = Integer.parseInt(troopsTable.get("Theutates Blitz"));
        this.shuffleFarmList();
        int counter = 1;

        // go farming
        while(true) {
            pretendBeingHuman(this.pauseClickMin, this.pauseClickMax);
            if(amountTroops > 1 && counter < this.farmlist.length) {
                this.printMessage("Theutates Blitz: " + amountTroops);
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
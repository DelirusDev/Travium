# Travium
Farmingbot for legendary browsergame **Travian** written in **Java+Selenium**

## Installing
1. Install Chrome/Chromium driver for your browser
* View your browserversion -> chrome://version/
* Download driver -> https://chromedriver.storage.googleapis.com/index.html
* Extract archive
* Add folder to PATH variables

2. Build and run
* Run in project folder
```
gradle build
```
* Change "settings.json" and "farm.json"
* Put "settings.json", "farm.json" and "troops{LANG}.json" into "Travium.jar" folder
* Run in project folder
```
java -jar build/libs/Travium.jar 
```

3. Have Fun!

## TODOs
* queue for buildings
* GUI
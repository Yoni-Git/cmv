import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import java.util.List;
import java.util.stream.Collectors;
import java.time.Duration;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.Random;


import java.io.FileReader;
import java.io.IOException;

public class BuySellCMV {

     
    private static String LOGIN_URL = "https://www.coinmv.com/#/login";
    private static String CHROME_DRIVER_PATH = "lib/chromedriver_mac64/chromedriver";
    private static String CHROME_DRIVER = "webdriver.chrome.driver";
    private static String ROBOTS_URL = "https://www.coinmv.com/#/vip/task/1";
    private static final String BUTTON_CLASS = "btn-do-task";
    private static final String PENDING_CLASS = "btn-do-task-pending";
    private static final String OK_BUTTON_SELECTOR = "button.van-button";

    public static void main(String[] args) {

         AccountDetails[] accountDetails = retrieveAccountDetails("accounts.json");

        // buy the current selection 
        Arrays.stream(accountDetails)
        .forEach(account -> buy(account.getUsername(), account.getPassword()));
    }
    

    private static void buy(String username, String password) {
        
        System.setProperty(CHROME_DRIVER, CHROME_DRIVER_PATH);
        WebDriver driver = new ChromeDriver();
        driver.get(LOGIN_URL);

        WebElement usernameInput = driver.findElement(By.name("user"));
        WebElement passwdInput = driver.findElement(By.name("password"));

        // Enter login credentials
        usernameInput.sendKeys(username);
        passwdInput.sendKeys(password);


        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
         // Scroll down the page to activate login button , maybe tab works ...
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0, 500)");

        WebElement loginButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("login-button")));
        loginButton.click();
         // wait for 2 seconds until login is complete
        sleep(2); 

        //navigate to the vipList/aka  Robots ;-)
        driver.get(ROBOTS_URL);

        // after 3 seconds , find all buttons with content "Buy/Sell" and click on them
        sleep(3); // Sleep for 3 seconds

         // Find all buttons that have content "Buy/Sell" and not clicked yet , helps incase of interuptions and rerun
        List<WebElement> filteredButtons = filterOutClickedButtons(driver.findElements(By.className("btn-do-task")));


        filteredButtons.forEach(button -> {
            if (!button.getAttribute("class").contains(PENDING_CLASS)) {
                button.click();
            }

            // Wait for the dialog to appear, 4 sec
            sleep(4);

            By buttonLocator = By.cssSelector(OK_BUTTON_SELECTOR);
            try {
                WebElement okButton = driver.findElement(buttonLocator);
                okButton.click();
            } catch (Exception ex) {
                System.out.println("Don't close message didn't appear, no need to click");
                return;
            }

            sleep(getRandomNumber(3)); // Sleep randomly between 1-3 sec before next loop
        });

        //closing my driver
        driver.quit();
    }

    private static AccountDetails[] retrieveAccountDetails(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            JsonArray jsonArray = gson.fromJson(reader, JsonArray.class);

            AccountDetails[] accountDetails = gson.fromJson(jsonArray, AccountDetails[].class);
            return accountDetails;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new AccountDetails[0];
    }

    /**
     * Pauses the execution for the specified number of seconds.
     *
     * @param seconds the number of seconds to sleep
    */
    private static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }    
    }
    private static class AccountDetails {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
    private static List<WebElement> filterOutClickedButtons(List<WebElement> buttons) {
        return buttons.stream()
            .filter(button -> !button.getAttribute("class").contains("btn-do-task-pending"))
            .collect(Collectors.toList());
    }
    private static int getRandomNumber(int max) {
        Random random = new Random();
        return random.nextInt(max) + 1;
    }

}
// DONT Judge me , F u if you do,  you didnt pay for this  :-(

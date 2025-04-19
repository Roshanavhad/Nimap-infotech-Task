

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class LoginTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://testffc.nimapinfotech.com/");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void login(String email, String password) {
        driver.findElement(By.id("mat-input-0")).clear();
        driver.findElement(By.id("mat-input-0")).sendKeys(email);
        driver.findElement(By.id("mat-input-1")).clear();
        driver.findElement(By.id("mat-input-1")).sendKeys(password);
        driver.findElement(By.id("kt_login_signin_submit")).click();
    }

    private boolean isLoginSuccessful() {
        try {
            wait.until(ExpectedConditions.urlContains("/dashboard"));
            return wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h3[contains(text(), 'Dashboard')]"))).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Test(dataProvider = "loginData")
    public void testLogin(String email, String password) {
        login(email, password);
        Assert.assertTrue(isLoginSuccessful(), "Login failed for: " + email);
    }

    @DataProvider(name = "loginData")
    public Object[][] loginData() {
        return new Object[][]{
                {"roshanavhad952@gmail.com", "76742601"},
                {"ravhad952@gmail.com", "76742601"},
                {"invalidemail@example.com", "wrongpassword"}
        };
    }

    @Test(dependsOnMethods = "testLogin")
    public void testPunchIn() {
        try {
            WebElement punchButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@class='punchBtn']")));
            punchButton.click();

            WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(), 'Punch In successful')]")));

            Assert.assertTrue(toastMessage.isDisplayed(), "Punch In success message not displayed");
        } catch (TimeoutException e) {
            Assert.fail("Punch In button or toast message not found.");
        }
    }

    @Test(dataProvider = "customerData", dependsOnMethods = "testLogin")
    public void testAddCustomer(String name, String email) {
        driver.findElement(By.xpath("//button[contains(text(), 'Add Customer')]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("customer_name"))).sendKeys(name);
        driver.findElement(By.id("customer_email")).sendKeys(email);
        driver.findElement(By.xpath("//button[contains(text(), 'Submit')]")).click();

        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(text(), 'Customer added successfully')]")));

        Assert.assertTrue(successMessage.isDisplayed(), "Customer was not added successfully.");
    }

    @DataProvider(name = "customerData")
    public Object[][] customerData() {
        return new Object[][]{
                {"John Doe", "john.doe@example.com"},
                {"Jane Smith", "jane.smith@example.com"}
        };
    }
}

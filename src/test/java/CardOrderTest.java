import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class CardOrderTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    static void setUpAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        options.addArguments("disable-infobars");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        options.addArguments("--disable-extensions");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("http://localhost:9999");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private WebElement waitForElement(By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    @Test
    void shouldTestSuccessOrderIfCorrectFilling() {
        waitForElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван Петров-Иванов");
        waitForElement(By.cssSelector("[data-test-id='tel'] input")).sendKeys("+79277777777");
        waitForElement(By.className("checkbox__box")).click();
        waitForElement(By.className("button")).click();
        String text = waitForElement(By.cssSelector("[data-test-id=order-success]")).getText();
        assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.", text.trim());
    }

    @Test
    void shouldTestWarnIfIncorrectTel() {
        waitForElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван Иван");
        waitForElement(By.cssSelector("[data-test-id='tel'] input")).sendKeys("+792777");
        waitForElement(By.className("checkbox__box")).click();
        waitForElement(By.className("button")).click();
        String text = waitForElement(By.cssSelector("[data-test-id='tel'].input_invalid .input__sub")).getText();
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79999999999.", text.trim());
    }

    @Test
    void shouldTestWarnIfNoName() {
        waitForElement(By.cssSelector("[data-test-id='tel'] input")).sendKeys("+79277777777");
        waitForElement(By.className("checkbox__box")).click();
        waitForElement(By.className("button")).click();
        String text = waitForElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub")).getText();
        assertEquals("Поле обязательно для заполнения", text.trim());
    }

    @Test
    void shouldTestWarnIfIncorrectName() {
        waitForElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Ivan");
        waitForElement(By.cssSelector("[data-test-id='tel'] input")).sendKeys("+79277777777");
        waitForElement(By.className("checkbox__box")).click();
        waitForElement(By.className("button")).click();
        String text = waitForElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub")).getText();
        assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.", text.trim());
    }

    @Test
    void shouldTestWarnIfNoNameAndUncheckedCheckbox() {
        waitForElement(By.cssSelector("[data-test-id='tel'] input")).sendKeys("+79277777777");
        waitForElement(By.className("button")).click();
        String nameError = waitForElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub")).getText();
        assertEquals("Поле обязательно для заполнения", nameError.trim());
        String checkboxError = waitForElement(By.cssSelector(".checkbox__label .input__sub")).getText();
        assertEquals("Вы должны согласиться с условиями", checkboxError.trim());
    }

    @Test
    void shouldTestWarnIfNoPhoneNumber() {
        waitForElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван Петров-Иванов");
        waitForElement(By.className("checkbox__box")).click();
        waitForElement(By.className("button")).click();
        String text = waitForElement(By.cssSelector("[data-test-id='tel'].input_invalid .input__sub")).getText();
        assertEquals("Поле обязательно для заполнения", text.trim());
    }

    @Test
    void shouldTestWarnIfIncorrectPhoneFormat() {
        waitForElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван Петров-Иванов");
        waitForElement(By.cssSelector("[data-test-id='tel'] input")).sendKeys("abc123");
        waitForElement(By.className("checkbox__box")).click();
        waitForElement(By.className("button")).click();
        String phoneError = waitForElement(By.cssSelector("[data-test-id='tel'].input_invalid .input__sub")).getText();
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79999999999.", phoneError.trim());
    }

    @Test
    void shouldTestWarnIfAllFieldsInvalid() {
        waitForElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Ivan");
        waitForElement(By.cssSelector("[data-test-id='tel'] input")).sendKeys("123");
        waitForElement(By.className("button")).click();
        String nameError = waitForElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub")).getText();
        assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.", nameError.trim());
        String phoneError = waitForElement(By.cssSelector("[data-test-id='tel'].input_invalid .input__sub")).getText();
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79999999999.", phoneError.trim());
        String checkboxError = waitForElement(By.cssSelector(".checkbox__label .input__sub")).getText();
        assertEquals("Вы должны согласиться с условиями", checkboxError.trim());
    }
}


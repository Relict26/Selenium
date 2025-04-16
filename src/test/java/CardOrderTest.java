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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CardOrderTest {
    private WebDriver driver;

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
        driver.get("http://localhost:9999");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void shouldTestSuccessOrderIfCorrectFilling() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван Петров-Иванов");
        driver.findElement(By.cssSelector("[data-test-id='tel'] input")).sendKeys("+79277777777");
        driver.findElement(By.className("checkbox__box")).click();
        driver.findElement(By.className("button")).click();
        String text = driver.findElement(By.cssSelector("[data-test-id=order-success]")).getText();
        assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.", text.trim());
    }

    @Test
    void shouldTestWarnIfIncorrectTel() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван Иван");
        driver.findElement(By.cssSelector("[data-test-id='tel'] input")).sendKeys("+792777");
        driver.findElement(By.className("checkbox__box")).click();
        driver.findElement(By.className("button")).click();
        String text = driver.findElement(By.cssSelector("[data-test-id='tel'].input_invalid .input__sub")).getText();
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79999999999.", text.trim());
    }

    @Test
    void shouldTestWarnIfNoName() {
        driver.findElement(By.cssSelector("[data-test-id='tel'] input")).sendKeys("+79277777777");
        driver.findElement(By.className("checkbox__box")).click();
        driver.findElement(By.className("button")).click();
        String text = driver.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub")).getText();
        assertEquals("Поле обязательно для заполнения", text.trim());
    }

    @Test
    void shouldTestWarnIfIncorrectName() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Ivan");
        driver.findElement(By.cssSelector("[data-test-id='tel'] input")).sendKeys("+79277777777");
        driver.findElement(By.className("checkbox__box")).click();
        driver.findElement(By.className("button")).click();
        String text = driver.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub")).getText();
        assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.", text.trim());
    }

    @Test
    void shouldTestWarnIfNoNameAndUncheckedCheckbox() {
        driver.findElement(By.cssSelector("[data-test-id='tel'] input")).sendKeys("+79277777777");
        driver.findElement(By.className("button")).click();
        String nameError = driver.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub")).getText();
        assertEquals("Поле обязательно для заполнения", nameError.trim());
        String checkboxError = driver.findElement(By.cssSelector(".checkbox__label .input__sub")).getText();
        assertEquals("Вы должны согласиться с условиями", checkboxError.trim());
    }

    @Test
    void shouldTestWarnIfNoPhoneNumber() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван Петров-Иванов");
        driver.findElement(By.className("checkbox__box")).click();
        driver.findElement(By.className("button")).click();
        String text = driver.findElement(By.cssSelector("[data-test-id='phone'].input_invalid .input__sub")).getText();
        assertEquals("Поле обязательно для заполнения", text.trim());
    }


    @Test
    void shouldTestWarnIfIncorrectPhoneFormat() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван Петров-Иванов");
        driver.findElement(By.cssSelector("[data-test-id='tel'] input")).sendKeys("abc123");
        driver.findElement(By.className("checkbox__box")).click();
        driver.findElement(By.className("button")).click();
        String phoneError = driver.findElement(By.cssSelector("[data-test-id='tel'].input_invalid .input__sub")).getText();
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79999999999.", phoneError.trim());
    }

    @Test
    void shouldTestWarnIfAllFieldsInvalid() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Ivan");
        driver.findElement(By.cssSelector("[data-test-id='tel'] input")).sendKeys("123");
        driver.findElement(By.className("button")).click();
        String nameError = driver.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub")).getText();
        assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.", nameError.trim());
        String phoneError = driver.findElement(By.cssSelector("[data-test-id='tel'].input_invalid .input__sub")).getText();
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79999999999.", phoneError.trim());
        String checkboxError = driver.findElement(By.cssSelector(".checkbox__label .input__sub")).getText();
        assertEquals("Вы должны согласиться с условиями", checkboxError.trim());
    }
}

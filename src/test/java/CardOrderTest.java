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
        driver.get("http://localhost:9999"); // Открытие страницы вынесено сюда
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void shouldTestSuccessOrderIfCorrectFilling() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван Иванов");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79277777777");
        driver.findElement(By.className("checkbox__box")).click();
        driver.findElement(By.className("button")).click();
        String text = driver.findElement(By.cssSelector("[data-test-id=order-success]")).getText();
        assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.", text.trim());
    }

    @Test
    void shouldTestWarnIfIncorrectTel() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван Иван");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79999999999");
        driver.findElement(By.className("checkbox__box")).click();
        driver.findElement(By.className("button")).click();

        String text = driver.findElement(By.cssSelector("[data-test-id='phone'] .input__sub")).getText();
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79999999999.", text.trim());

        // Проверка на наличие класса input_invalid
        WebElement phoneInput = driver.findElement(By.cssSelector("[data-test-id='phone'] input"));
        assertTrue(phoneInput.getAttribute("class").contains("input_invalid"));
    }

    @Test
    void shouldTestWarnIfNoName() {
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79277777777");
        driver.findElement(By.className("checkbox__box")).click();
        driver.findElement(By.className("button")).click();

        String text = driver.findElement(By.cssSelector("[data-test-id='name'] .input__sub")).getText();
        assertEquals("Поле обязательно для заполнения", text.trim());

        WebElement nameInput = driver.findElement(By.cssSelector("[data-test-id='name'] input"));
        assertTrue(nameInput.getAttribute("class").contains("input_invalid"));
    }

    @Test
    void shouldTestWarnIfIncorrectName() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Ivan");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79277777777");
        driver.findElement(By.className("checkbox__box")).click();
        driver.findElement(By.className("button")).click();

        String text = driver.findElement(By.cssSelector("[data-test-id='name'] .input__sub")).getText();
        assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.", text.trim());

        WebElement nameInput = driver.findElement(By.cssSelector("[data-test-id='name'] input"));
        assertTrue(nameInput.getAttribute("class").contains("input_invalid"));
    }

    @Test
    void shouldTestWarnIfNoNameAndNoCheckbox() {
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79277777777");
        driver.findElement(By.className("button")).click();

        String nameText = driver.findElement(By.cssSelector("[data-test-id='name'] .input__sub")).getText();
        assertEquals("Поле обязательно для заполнения", nameText.trim());

        WebElement nameInput = driver.findElement(By.cssSelector("[data-test-id='name'] input"));
        assertTrue(nameInput.getAttribute("class").contains("input_invalid"));

        String checkboxText = driver.findElement(By.cssSelector(".input_invalid .input__sub")).getText();
        assertEquals("Вы должны согласиться с условиями обработки данных", checkboxText.trim());
    }

    @Test
    void shouldTestWarnIfNoPhone() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван Иванов");
        driver.findElement(By.className("checkbox__box")).click();
        driver.findElement(By.className("button")).click();

        String phoneText = driver.findElement(By.cssSelector("[data-test-id='phone'] .input__sub")).getText();
        assertEquals("Поле обязательно для заполнения", phoneText.trim());

        WebElement phoneInput = driver.findElement(By.cssSelector("[data-test-id='phone'] input"));
        assertTrue(phoneInput.getAttribute("class").contains("input_invalid"));
    }
}
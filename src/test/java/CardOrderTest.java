import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79277777777");
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
        driver.findElement(By.cssSelector(".button")).click();
        String text = driver.findElement(By.cssSelector("[data-test-id='order-success']")).getText();
        assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.", text.trim());
    }

    @Test
    void shouldTestWarnIfIncorrectTel() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван Иван");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+792777");
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
        driver.findElement(By.cssSelector(".button")).click();
        String text = driver.findElement(By.cssSelector("[data-test-id='phone'].input_invalid .input__sub")).getText();
        assertTrue(text.trim().startsWith("Телефон указан неверно. Должно быть 11 цифр"));
    }

    @Test
    void shouldTestWarnIfNoName() {
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79277777777");
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
        driver.findElement(By.cssSelector(".button")).click();
        String text = driver.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub")).getText();
        assertEquals("Поле обязательно для заполнения", text.trim());
    }

    @Test
    void shouldTestWarnIfIncorrectName() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Ivan");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79277777777");
        driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__box")).click();
        driver.findElement(By.cssSelector(".button")).click();
        String text = driver.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub")).getText();
        assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.", text.trim());
    }

    @Test
    void shouldTestWarnIfNoNameAndUncheckedCheckbox() {
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79277777777");
        driver.findElement(By.cssSelector(".button")).click();

        // Проверка ошибки для имени
        String nameError = driver.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub")).getText();
        assertEquals("Поле обязательно для заполнения", nameError.trim());

        // Проверка ошибки для чекбокса
        String checkboxError = driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__text")).getText();
        assertEquals("Я соглашаюсь с условиями обработки и использования моих персональных данных и разрешаю сделать запрос в бюро кредитных историй", checkboxError.trim());
    }

    @Test
    void shouldTestWarnIfUncheckedCheckbox() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Иван Петров-Иванов");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79277777777");
        driver.findElement(By.cssSelector(".button")).click();

        // Проверка ошибки для чекбокса
        String checkboxError = driver.findElement(By.cssSelector("[data-test-id='agreement'] .checkbox__text")).getText();
        assertEquals("Я соглашаюсь с условиями обработки и использования моих персональных данных и разрешаю сделать запрос в бюро кредитных историй", checkboxError.trim());
    }
}



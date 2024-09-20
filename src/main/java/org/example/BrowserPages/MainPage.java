package org.example.BrowserPages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MainPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public MainPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    @Step("Navigate to URL: {url}")
    public void navigateToUrl(String url) {
        driver.get(url);
    }

    @Step("Select category with search term: {searchTerm}")
    public void selectCategory(String searchTerm) {
        try {
            // Находим поле поиска
            By searchBox = By.id("ek-search");
            driver.findElement(searchBox).sendKeys(searchTerm);
            // Завершаем ввод нажатием Enter
            driver.findElement(searchBox).sendKeys(Keys.RETURN);
        } catch (NoSuchElementException e) {
            // Если элемент не найден, выводим сообщение
            System.out.println("Search box not found.");
        }
    }

    @Step("Handle CAPTCHA by giving user time to solve it")
    public void handleCaptchaAndWait() {
        try {
            // Проверяем, появился ли элемент капчи
            By captchaElement = By.className("recaptcha__bg--robot");
            if (driver.findElements(captchaElement).size() > 0) {
                System.out.println("CAPTCHA detected. Please solve it within 1 minute...");

                // Даем пользователю 1 минуту на решение капчи
                Thread.sleep(60000); // Ожидание в миллисекундах (60000 = 1 минута)

                System.out.println("Continuing after CAPTCHA.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step("Go to Books category with search term: {searchTerm}")
    public void goToBooksCategory(String searchTerm, String baseUrl) {
        navigateToUrl(baseUrl); // Ожидание возможной капчи
        selectCategory(searchTerm); // Выполняем поиск по термину
    }
}

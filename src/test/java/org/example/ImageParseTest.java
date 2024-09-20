package org.example;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.example.BrowserPages.MainPage;
import org.example.BrowserPages.ImageSearch;
import org.testng.annotations.Test;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Epic("Amazon Book Scraper")
@Feature("Search and extract book data from Amazon")
public class ImageParseTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private List<String> searchTerms;
    private String baseUrl = "https://ek.ua/ua/";

    @BeforeTest
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-agent=Mozilla Chrome/125.0.6422.61");
        options.setBinary("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 20);

        // Загружаем список терминов из файла
        searchTerms = loadSearchTermsFromFile("D:\\for_parse.txt");
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Step("Load search terms from file")
    private List<String> loadSearchTermsFromFile(String filePath) {
        List<String> terms = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                terms.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return terms;
    }

    @Step("find img url")
    @Test
    public void testScrapeBooks() {
        MainPage homePage = new MainPage(driver, wait);
        ImageSearch searchPage = new ImageSearch(driver, wait);

        // Цикл по каждому термину из файла
        for (String term : searchTerms) {
            System.out.println("Searching for: " + term);
            homePage.goToBooksCategory(term, baseUrl); // Передаем текущий searchTerm и базовый URL

            boolean success = false;
            for (int attempt = 0; attempt < 2; attempt++) {
                try {
                    searchPage.waitForAndScanImage();
                    success = true;
                    break; // Успешное выполнение, выходим из цикла попыток
                } catch (Exception e) {
                    if (attempt == 0) {
                        System.out.println("Element not found on attempt " + (attempt + 1) + ". Retrying...");
                    } else {
                        System.out.println("Element not found on attempt " + (attempt + 1) + ". No more retries.");
                    }
                }
            }

            if (!success) {
                System.out.println("Failed to find element for search term: " + term);
            }

            // Проверка и обработка CAPTCHA
            homePage.handleCaptchaAndWait();  // Вместо перезапуска браузера

            // Добавляем задержку между запросами
            try {
                Random random = new Random();

                // Генерация случайного числа от 1000 до 4000
                int min = 1000;
                int max = 4000;
                int randomNumber = random.nextInt((max - min) + 1) + min;

                Thread.sleep(randomNumber); // Задержка в миллисекундах
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

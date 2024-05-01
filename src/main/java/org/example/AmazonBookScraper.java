package org.example;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

public class AmazonBookScraper {
    private WebDriver driver;
    private WebDriverWait wait;
    private Random random;


    public AmazonBookScraper() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-agent=Mozilla   Chrome/124.0.6367.119");
        options.addArguments("enable-automation");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-browser-side-navigation");
        options.addArguments("--disable-gpu");

        this.driver = new ChromeDriver(options);
        this.wait = new WebDriverWait(driver, 20);
        this.random = new Random();
    }

    public void close() {
        if (driver != null) {
            ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
            for (int i = 0; i < Math.min(tabs.size(), 10); i++) {
                driver.switchTo().window(tabs.get(i));
                driver.close();
            }
            driver.quit();
        }
    }
    public List<Book> scrapeBooks(String category, String searchTerm) {
        randomSleep();
        AmazonPageNavigator navigator = new AmazonPageNavigator(driver);
        navigator.navigateToHomePage();
        randomSleep();
        navigator.selectCategory(category);

        AmazonSearchPageHandler searchPageHandler = new AmazonSearchPageHandler(driver, wait);
        searchPageHandler.enterSearchTerm(searchTerm);
        searchPageHandler.waitForSearchResults();

        AmazonBookExtractor bookExtractor = new AmazonBookExtractor(driver);
        return bookExtractor.extractBooks();
    }

    private void randomSleep() {
        try {

            TimeUnit.SECONDS.sleep(random.nextInt(5) + 1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public static void main(String[] args) {
        AmazonBookScraper scraper = new AmazonBookScraper();
        try {
            List<Book> books = scraper.scrapeBooks("Books", "Java");

            for (Book book : books) {
                System.out.println("Title: " + book.getTitle() + ", Author: " + book.getAuthor() + ", Price: " + book.getPrice() + ", Best Seller: " + book.isBestSeller());
            }


            boolean hasSpecificBook = books.stream().anyMatch(book -> book.getTitle().contains("Head First Java") && book.getAuthor().contains("Kathy Sierra"));
            System.out.println("Contains specific book (Head First Java by Kathy Sierra): " + hasSpecificBook);
        } finally {
            scraper.close();
        }
    }
}








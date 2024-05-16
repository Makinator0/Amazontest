package org.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.ArrayList;
import java.util.List;

public class AmazonBookScraperTest {
    private WebDriver driver;
    private WebDriverWait wait;


    @Before
    public void setUp() {
        driver = WebDriverFactory.createDriver();
        wait = WebDriverFactory.createWait(driver, 20);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
            for (int i = 0; i < Math.min(tabs.size(), 10); i++) {
                driver.switchTo().window(tabs.get(i));
                driver.close();
            }
            driver.quit();
        }
    }

    @Test
    public void testScrapeBooks() {
        AmazonHomePage homePage = new AmazonHomePage(driver);
        homePage.goToBooksCategory();

        AmazonSearchPage searchPage = new AmazonSearchPage(driver, wait);
        List<Book> books = searchPage.searchForBooks("Java");

        BookResultsHandler resultsHandler = new BookResultsHandler();
        resultsHandler.printBooks(books);
        resultsHandler.checkSpecificBook(books, "Head First Java", "Kathy Sierra");
    }

}
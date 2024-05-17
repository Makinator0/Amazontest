package org.example;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebElement;



import java.util.ArrayList;
import java.util.List;

public class AmazonBookScraperTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private String searchTerm;
    private String bookUrl = "https://www.amazon.com/Head-First-Java-Brain-Friendly-Guide-dp-1491910771/dp/1491910771/ref=dp_ob_title_bk";

    @Before
    public void setUp() {
        driver = WebDriverFactory.createDriver();
        wait = WebDriverFactory.createWait(driver, 20);
        searchTerm = System.getProperty("searchTerm", "Java"); // Default to "Java" if property not set
    }

    @After
    public void tearDown() {
        if (driver != null) {
            ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
            for (int i = 0; i < Math.min(tabs.size(), 10); i++) {
                driver.switchTo().window(tabs.get(i));
                driver.close();
            }
            driver.quit();
        }
    }

    @Test
    public void testScrapeBooks() {
        String asin = AmazonUtils.extractAsinFromUrl(bookUrl);
        AmazonHomePage homePage = new AmazonHomePage(driver);
        homePage.goToBooksCategory();

        AmazonSearchPage searchPage = new AmazonSearchPage(driver, wait);
        List<Book> books = searchPage.searchForBooks(searchTerm); // Use the searchTerm

        BookResultsHandler resultsHandler = new BookResultsHandler();
        resultsHandler.printBooks(books);
        // Fetch book details from specific ASIN page
        driver.get("https://www.amazon.com/dp/" + asin);
        WebElement titleElement = driver.findElement(By.id("productTitle"));
        String title = titleElement.getText().trim().toLowerCase();


        Book foundBook = books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title))
                .findFirst()
                .orElse(null);

        Assert.assertNotNull("Expected book not found in the list.", foundBook);
        if (foundBook != null) {
            System.out.println("Found book: " + foundBook.getTitle() + " by " + foundBook.getAuthor() + ", Price: " + foundBook.getPrice() + ", Bestseller: " + foundBook.isBestSeller());
        }
    }
}

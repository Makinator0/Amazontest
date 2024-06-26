package org.example;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.example.BrowserPages.AmazonHomePage;
import org.example.BrowserPages.AmazonSearchPage;
import org.example.BrowserPages.BookDetailsPage;
import org.testng.annotations.Test;
import org.testng.Assert;
import io.qameta.allure.Step;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.util.ArrayList;
import java.util.List;

@Epic("Amazon Book Scraper")
@Feature("Search and extract book data from Amazon")
public class AmazonBookScraperTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private String searchTerm;
    private String bookUrl = "https://www.amazon.com/Python-Crash-Course-Eric-Matthes/dp/1718502702/ref=sr_1_1?crid=12UW02UD2RGIE&dib=eyJ2IjoiMSJ9.ST-6MoO7OqvlsneC5UZBvVCOfb7J2YblNpiogupHg9laMGgEBQ3mCY-r9noo-DVMfcdBJ-QYv8Dy2pMw5G6IbqXizW-Za_c7nEqfw4NAuq8aGjHk7H_88C_DBs7ss_mHVynTySQXBCPwHunzWwkBz5Kegu-sx5iyQ3QoVtdHM_sIkc5XgjKXtfL_x3CQsf0QB3ZymvDDIS1HqdeiZUtYqd922Mwm4FfuZSSfsulshKM.w8j8RFs_K-1xOXhmwSrRvkTWkr6E6zgDxgy1w7mKfPw&dib_tag=se&keywords=python&qid=1715954866&s=books&sprefix=Pyt%2Cstripbooks-intl-ship%2C192&sr=1-1";

    @BeforeTest
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-agent=Mozilla Chrome/ 125.0.6422.61");
        options.setBinary("C:\\Users\\maksi\\AppData\\Local\\Google\\Chrome\\Application\\chrome.exe");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 20);
        searchTerm = System.getProperty("searchTerm", "Java");
    }

    @AfterTest
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

    @Step("Scrape books from Amazon")
    @Test
    public void testScrapeBooks() {
        AmazonHomePage homePage = new AmazonHomePage(driver);
        homePage.goToBooksCategory();

        AmazonSearchPage searchPage = new AmazonSearchPage(driver, wait);
        List<Book> books = searchPage.searchForBooks(searchTerm);

        String asin = BookDetailsPage.extractAsinFromUrl(bookUrl);
        Assert.assertNotNull("ASIN should be extracted from URL", asin);

        BookDetailsPage bookDetailsPage = new BookDetailsPage(driver);
        bookDetailsPage.navigateToBookDetailsPage(asin);
        String extractedTitle = bookDetailsPage.getBookTitle();
        String extractedAuthor = bookDetailsPage.getBookAuthor();

        Book foundBook = books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(extractedTitle) && book.getAuthor().equals(extractedAuthor))
                .findFirst()
                .orElse(null);

        Assert.assertTrue(books.contains(foundBook), "Expected book should be in the list of books");
        if (foundBook != null) {
            System.out.println("Found book: " + foundBook.getTitle() + " by " + foundBook.getAuthor() + ", Price: " + foundBook.getPrice() + ", Bestseller: " + foundBook.isBestSeller());
        }
    }
}

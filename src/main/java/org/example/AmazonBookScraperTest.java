package org.example;

import org.junit.After;
import org.junit.Assert;
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
        AmazonHomePage homePage = new AmazonHomePage(driver);
        homePage.goToBooksCategory();

        AmazonSearchPage searchPage = new AmazonSearchPage(driver, wait);
        List<Book> books = searchPage.searchForBooks("Java");

        BookResultsHandler resultsHandler = new BookResultsHandler();
        resultsHandler.printBooks(books);
        Book foundBook = null;
        for (Book book : books) {
            if (book.getTitle().equals("Head First Java: A Brain-Friendly Guide") && book.getAuthor().contains("Kathy Sierra")) {
                foundBook = book;
                break;
            }
        }
        Assert.assertNotNull("Ожидаемая книга не найдена в списке.", foundBook);
        if (foundBook != null) {
            Assert.assertEquals("Название книги не совпадает.", "Head First Java: A Brain-Friendly Guide", foundBook.getTitle());
            Assert.assertTrue("Автор книги не содержит ожидаемого автора.", foundBook.getAuthor().contains("Kathy Sierra"));
            Assert.assertEquals("Цена книги не совпадает.", "17.60", foundBook.getPrice());
            Assert.assertTrue("Книга не является бестселлером, хотя должна быть.", foundBook.isBestSeller());
            System.out.println("Найдена книга: " + foundBook.getTitle() + " от " + foundBook.getAuthor() + " цена: " + foundBook.getPrice() + " бестселлер: " + foundBook.isBestSeller());
        }
    }
}

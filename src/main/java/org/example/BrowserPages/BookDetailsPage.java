package org.example.BrowserPages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class BookDetailsPage {
    private WebDriver driver;

    public BookDetailsPage(WebDriver driver) {
        this.driver = driver;
    }

    @Step("Navigate to book details page with ASIN: {asin}")
    public void navigateToBookDetailsPage(String asin) {
        driver.get("https://www.amazon.com/dp/" + asin);
    }

    @Step("Get book title")
    public String getBookTitle() {
        WebElement titleElement = driver.findElement(By.id("productTitle"));
        return titleElement.getText().trim().toLowerCase();
    }

    @Step("Extract ASIN from URL: {url}")
    public static String extractAsinFromUrl(String url) {
        String[] parts = url.split("/");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals("dp") && i + 1 < parts.length) {
                return parts[i + 1];
            }
        }
        return null;
    }

    @Step("Get book author")
    public String getBookAuthor() {
        WebElement authorElement = driver.findElement(By.xpath("//span[contains(@class, 'author')]/a[@class='a-link-normal']"));
        return authorElement.getText().trim();
    }
}

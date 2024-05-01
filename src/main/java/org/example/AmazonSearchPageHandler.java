package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AmazonSearchPageHandler {
    private WebDriver driver;
    private WebDriverWait wait;

    public AmazonSearchPageHandler(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void enterSearchTerm(String searchTerm) {
        WebElement searchBox = driver.findElement(By.id("twotabsearchtextbox"));
        searchBox.sendKeys(searchTerm);
        searchBox.submit();
    }

    public void waitForSearchResults() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".s-result-item")));
    }
}

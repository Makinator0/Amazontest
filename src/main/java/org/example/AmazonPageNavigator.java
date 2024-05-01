package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AmazonPageNavigator {
    private WebDriver driver;

    public AmazonPageNavigator(WebDriver driver) {
        this.driver = driver;
    }

    public void navigateToHomePage() {
        driver.get("https://www.amazon.com/");

    }

    public void selectCategory(String category) {
        driver.findElement(By.id("searchDropdownBox")).sendKeys(category);
    }
}

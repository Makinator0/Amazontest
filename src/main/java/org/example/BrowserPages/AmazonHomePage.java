package org.example.BrowserPages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AmazonHomePage {
    private WebDriver driver;

    public AmazonHomePage(WebDriver driver) {
        this.driver = driver;
    }

    @Step("Navigate to Amazon home page")
    public void navigateToHomePage() {
        driver.get("https://www.amazon.com/");
    }

    @Step("Select category: {category}")
    public void selectCategory(String category) {
        driver.findElement(By.id("searchDropdownBox")).sendKeys(category);
    }

    @Step("Go to Books category")
    public void goToBooksCategory() {
        navigateToHomePage();
        selectCategory("Books");
    }
}

package org.example;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.List;
public class AmazonSearchPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public AmazonSearchPage(WebDriver driver, WebDriverWait wait) {
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
    public List<Book> searchForBooks(String searchTerm) {
        enterSearchTerm(searchTerm);
        waitForSearchResults();
        AmazonBookExtractor bookExtractor = new AmazonBookExtractor(driver);
        return bookExtractor.extractBooks();
    }
}

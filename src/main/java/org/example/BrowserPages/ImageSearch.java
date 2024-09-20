package org.example.BrowserPages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ImageSearch {
    private WebDriver driver;
    private WebDriverWait wait;

    public ImageSearch(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    @Step("Wait for search results or 'no product' message")
    public void waitForSearchResultsOrNoProducts() {
        // Ожидаем либо появление изображения, либо сообщения о том, что товаров не найдено
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("img[onload*='checkImg']")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".msg-search-div"))
        ));
    }

    @Step("Check if no products found")
    public boolean noProductsFound() {
        try {
            // Проверяем наличие сообщения о том, что товаров не найдено
            WebElement noProductMessage = driver.findElement(By.cssSelector(".msg-search-div"));
            if (noProductMessage.isDisplayed()) {
                System.out.println("No products found for this search term.");
                return true;
            }
        } catch (NoSuchElementException e) {
            // Если элемент не найден, значит сообщение о ненайденных товарах отсутствует
            return false;
        }
        return false;
    }

    @Step("Scan image link")
    public String scanImageLink() {
        // Находим элемент изображения и получаем ссылку из атрибута src
        WebElement imageElement = driver.findElement(By.cssSelector("img[onload*='checkImg']"));
        String imageUrl = imageElement.getAttribute("src");

        // Выводим ссылку на изображение в консоль
        System.out.println("Image URL: " + imageUrl);

        return imageUrl;
    }

    @Step("Wait for and scan image, retry if necessary")
    public String waitForAndScanImage() {
        for (int attempt = 0; attempt < 3; attempt++) {  // Максимум 3 попытки
            // Ждем результатов или сообщения о том, что товаров не найдено
            waitForSearchResultsOrNoProducts();

            // Проверяем, было ли найдено сообщение о том, что товары не найдены
            if (noProductsFound()) {
                System.out.println("Retrying due to no products found...");
                continue;  // Повторяем попытку
            }

            try {
                // Если товара нет, но изображения есть, возвращаем ссылку на изображение
                return scanImageLink();
            } catch (NoSuchElementException e) {
                System.out.println("Image not found on attempt " + (attempt + 1) + ". Retrying...");
            }
        }

        // Если после всех попыток изображение не было найдено, возвращаем null или сообщение об ошибке
        System.out.println("Failed to find image after multiple attempts.");
        return null;
    }
}

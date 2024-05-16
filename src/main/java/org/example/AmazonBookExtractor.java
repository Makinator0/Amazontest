package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class AmazonBookExtractor {
    private WebDriver driver;

    public AmazonBookExtractor(WebDriver driver) {
        this.driver = driver;
    }

    public List<Book> extractBooks() {
        List<Book> books = new ArrayList<>();
        List<WebElement> bookElements = driver.findElements(By.cssSelector(".s-result-item.s-asin"));
        for (WebElement bookElement : bookElements) {
            try {
                books.add(extractBookData(bookElement));
            } catch (Exception e) {
                System.err.println("Не вдалося видобути дані книги: " + e.getMessage());
            }
        }
        return books;
    }

    private Book extractBookData(WebElement bookElement) {
        String title = "Відсутня інформація";
        String author = "Невідомий";
        String price = "N/A";
        boolean isBestSeller = false;

        try {
            title = bookElement.findElement(By.cssSelector("h2 a")).getText();
            List<WebElement> authorElements = bookElement.findElements(By.cssSelector(".a-row.a-size-base.a-color-secondary .a-size-base, .a-row .a-size-base:not(.a-color-secondary)"));
            if (!authorElements.isEmpty()) {
                StringBuilder authors = new StringBuilder();
                for (int i = 0; i < authorElements.size(); i++) {
                    WebElement element = authorElements.get(i);
                    String authorText = element.getText().trim();
                    if (i == 0 && authorText.startsWith("|")) {
                        authorText = authorText.substring(1).trim();
                    }
                    if (authorText.equalsIgnoreCase("and")  || authorText.equalsIgnoreCase(",")  || authorText.equalsIgnoreCase("by")) {
                        continue;
                    }
                    if (authorText.equals("|") || authorText.equals("e")) {
                        break;
                    }
                    if (authors.length() > 0) {
                        authors.append(", ");
                    }
                    authors.append(authorText);
                    if (i + 1 < authorElements.size()) {
                        String nextText = authorElements.get(i + 1).getText().trim();
                        if (Character.isDigit(nextText.charAt(0))) {
                            break;
                        }
                    }
                }
                author = authors.toString();
            } else {
                System.err.println("Деталі про автора книги не знайдено: " + title);
            }

            WebElement priceElement = bookElement.findElement(By.cssSelector(".a-price"));
            if (priceElement != null) {
                price = priceElement.findElement(By.cssSelector(".a-price-whole")).getText() + "." +
                        priceElement.findElement(By.cssSelector(".a-price-fraction")).getText();
            }

            isBestSeller = !bookElement.findElements(By.cssSelector(".a-badge-label")).isEmpty() &&
                    bookElement.findElement(By.cssSelector(".a-badge-label")).getText().contains("Лідер продажу");
        } catch (NoSuchElementException e) {
            System.err.println("Element not found for book: " + title);
        }

        return new Book(title, author, price, isBestSeller);
    }
}


package org.example;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

public class AmazonBookScraper {
    private WebDriver driver;
    private WebDriverWait wait;
    private Random random;


    public AmazonBookScraper(String driverPath) {
        System.setProperty("webdriver.chrome.driver", driverPath);

        ChromeOptions options = new ChromeOptions();
        // Имитация поведения разных браузеров
        options.addArguments("user-agent=Mozilla/5 (Windows NT 10.0; Win64; x64)  Chrome/124.0.6367.119");
        // Включение поддержки cookies
        options.addArguments("enable-automation");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-browser-side-navigation");
        options.addArguments("--disable-gpu");

        this.driver = new ChromeDriver(options);
        this.wait = new WebDriverWait(driver, 20);
        this.random = new Random();
    }

    public void close() {
        if (driver != null) {
            // Получаем список всех открытых вкладок
            ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
            // Закрываем до 10 вкладок
            for (int i = 0; i < Math.min(tabs.size(), 10); i++) {
                driver.switchTo().window(tabs.get(i)); // Переключаемся на каждую вкладку
                driver.close(); // Закрываем вкладку
            }
            driver.quit(); // Закрываем все оставшиеся окна и завершаем сеанс WebDriver
        }
    }

    public List<Book> scrapeBooks(String category, String searchTerm) {
        // Рандомная задержка перед действиями
        randomSleep();
        AmazonPageNavigator navigator = new AmazonPageNavigator(driver);
        navigator.navigateToHomePage();
        randomSleep();
        navigator.selectCategory(category);

        AmazonSearchPageHandler searchPageHandler = new AmazonSearchPageHandler(driver, wait);
        searchPageHandler.enterSearchTerm(searchTerm);
        searchPageHandler.waitForSearchResults();

        AmazonBookExtractor bookExtractor = new AmazonBookExtractor(driver);
        return bookExtractor.extractBooks();
    }

    private void randomSleep() {
        try {
            // Рандомная задержка от 1 до 5 секунд
            TimeUnit.SECONDS.sleep(random.nextInt(5) + 1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public static void main(String[] args) {
        AmazonBookScraper scraper = new AmazonBookScraper("C:\\Users\\maksi\\OneDrive\\Рабочий стол\\chromedriver-win64\\chromedriver.exe");
        try {
            List<Book> books = scraper.scrapeBooks("Books", "Java");
            // Выводим все книги в списке
            for (Book book : books) {
                System.out.println("Title: " + book.getTitle() + ", Author: " + book.getAuthor() + ", Price: " + book.getPrice() + ", Best Seller: " + book.isBestSeller());
            }

            // Проверяем наличие книги "Head First Java" и выводим сообщение
            boolean hasSpecificBook = books.stream().anyMatch(book -> book.getTitle().contains("Head First Java") && book.getAuthor().contains("Kathy Sierra"));
            System.out.println("Contains specific book (Head First Java by Kathy Sierra): " + hasSpecificBook);
        } finally {
            scraper.close();
        }
    }
}

class AmazonPageNavigator {
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

class AmazonSearchPageHandler {
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

class AmazonBookExtractor {
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
                System.err.println("Failed to extract book data: " + e.getMessage());
            }
        }
        return books;
    }

    private Book extractBookData(WebElement bookElement) {
        String title = "Information missing";
        String author = "Unknown";
        String price = "N/A";
        boolean isBestSeller = false;

        try {
            title = bookElement.findElement(By.cssSelector("h2 a")).getText();

            // Извлекаем автора книги, обрабатывая возможность наличия нескольких авторов
            List<WebElement> authorElements = bookElement.findElements(By.cssSelector(".a-row.a-size-base.a-color-secondary .a-size-base, .a-row .a-size-base:not(.a-color-secondary)"));
            if (!authorElements.isEmpty()) {
                StringBuilder authors = new StringBuilder();
                for (int i = 0; i < authorElements.size(); i++) {
                    WebElement element = authorElements.get(i);
                    String authorText = element.getText().trim();

                    // Удаляем символ | из начала первого элемента
                    if (i == 0 && authorText.startsWith("|")) {
                        authorText = authorText.substring(1).trim();
                    }

                    // Убираем ненужные части авторского текста
                    if (authorText.equalsIgnoreCase("and") || authorText.equalsIgnoreCase(",") || authorText.equalsIgnoreCase("by")) {
                        continue;
                    }
                    // Проверяем, не является ли текущий текст символом, указывающим на конец списка авторов
                    if (authorText.equals("|") || authorText.equals(".")) {
                        break;
                    }
                    if (authors.length() > 0) {
                        authors.append(", ");
                    }
                    authors.append(authorText);
                    // Проверяем, является ли следующий элемент цифрой
                    if (i + 1 < authorElements.size()) {
                        String nextText = authorElements.get(i + 1).getText().trim();
                        if (Character.isDigit(nextText.charAt(0))) {
                            break;
                        }
                    }
                }
                author = authors.toString();
            } else {
                System.err.println("Author details not found for book: " + title);
            }

            // Извлекаем цену книги
            WebElement priceElement = bookElement.findElement(By.cssSelector(".a-price"));
            if (priceElement != null) {
                price = priceElement.findElement(By.cssSelector(".a-price-whole")).getText() + "." +
                        priceElement.findElement(By.cssSelector(".a-price-fraction")).getText();
            }

            isBestSeller = !bookElement.findElements(By.cssSelector(".a-badge-label")).isEmpty() &&
                    bookElement.findElement(By.cssSelector(".a-badge-label")).getText().contains("Best Seller");
        } catch (NoSuchElementException e) {
            System.err.println("Element not found for book: " + title);
        }
        return new Book(title, author, price, isBestSeller);
    }
}

class Book {
    private String title;
    private String author;
    private String price;
    private boolean isBestSeller;

    public Book(String title, String author, String price, boolean isBestSeller) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.isBestSeller = isBestSeller;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPrice() {
        return price;
    }

    public boolean isBestSeller() {
        return isBestSeller;
    }
}

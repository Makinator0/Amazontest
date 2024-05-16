package org.example;

import java.util.Objects;

public class Book {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return isBestSeller == book.isBestSeller &&
                Objects.equals(title, book.title) &&
                Objects.equals(author, book.author) &&
                Objects.equals(price, book.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, price, isBestSeller);
    }
}

package org.example;

import java.util.List;

public class BookResultsHandler {
    public void printBooks(List<Book> books) {
        for (Book book : books) {
            System.out.println("Title: " + book.getTitle() + ", Author: " + book.getAuthor() + ", Price: " + book.getPrice() + ", Bestseller: " + book.isBestSeller());
        }
    }
}
package org.example;

import java.util.List;

public class BookResultsHandler {
    public void printBooks(List<Book> books) {
        for (Book book : books) {
            System.out.println("Title: " + book.getTitle() + ", Author: " + book.getAuthor() + ", Price: " + book.getPrice() + ", Bestseller: " + book.isBestSeller());
        }
    }

    public void checkSpecificBook(List<Book> books, String title, String author) {
        boolean hasSpecificBook = books.stream()
                .anyMatch(book -> book.getTitle().contains(title) && book.getAuthor().contains(author));
        System.out.println("Contains specific book (" + title + " by " + author + "): " + hasSpecificBook);
    }
}
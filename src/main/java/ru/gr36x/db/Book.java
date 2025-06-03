package ru.gr36x.db;

import io.ebean.Model;
import jakarta.persistence.*;

@Entity
public class Book extends Model {
    @Id
    private String isbn;

    private String title;
    private int year;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    public Book(String isbn, String title, int year, Author author) {
        this.isbn = isbn;
        this.title = title;
        this.year = year;
        this.author = author;
    }

    // Getters and Setters
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }
}
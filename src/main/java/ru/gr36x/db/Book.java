package ru.gr36x.db;

import io.ebean.Model;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Book extends Model {
    @Id
    private String isbn;

    private String title;
    private int year;
    private int quantity; // Количество экземпляров книги

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<BookLoan> loans;

    public Book(String isbn, String title, int year, Author author, int quantity) {
        this.isbn = isbn;
        this.title = title;
        this.year = year;
        this.author = author;
        this.quantity = quantity;
    }

    // Геттеры и сеттеры
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public List<BookLoan> getLoans() { return loans; }
    public void setLoans(List<BookLoan> loans) { this.loans = loans; }

    @Override
    public String toString() {
        return title;
    }

}
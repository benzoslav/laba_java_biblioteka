package ru.gr36x.db;

import io.ebean.Model;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity
public class BookLoan extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Client client;

    @ManyToOne
    private Book book;

    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean isReturned;

    public static final io.ebean.Finder<Long, BookLoan> find = new io.ebean.Finder<>(BookLoan.class);


    public BookLoan(Client client, Book book, LocalDate loanDate, LocalDate dueDate) {
        this.client = client;
        this.book = book;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.isReturned = false;
    }

    // геттеры и сеттеры как у тебя
    public Long getId() { return id; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public LocalDate getLoanDate() { return loanDate; }
    public void setLoanDate(LocalDate loanDate) { this.loanDate = loanDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.isReturned = (returnDate != null);
    }
    public boolean isReturned() { return isReturned; }
}

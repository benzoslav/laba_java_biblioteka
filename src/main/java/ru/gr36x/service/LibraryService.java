package ru.gr36x.service;

import io.ebean.DB;
import ru.gr36x.db.Author;
import ru.gr36x.db.Book;
import ru.gr36x.db.Client;
import ru.gr36x.db.BookLoan;
import java.time.LocalDate;
import java.util.List;

public class LibraryService {

    // Методы для авторов (без изменений)
    public void createAuthor(Author author) { author.save(); }
    public void updateAuthor(Long id, String name, String country) {
        Author author = DB.find(Author.class, id);
        if (author != null) {
            author.setName(name);
            author.setCountry(country);
            author.update();
        }
    }
    public void deleteAuthor(Long id) {
        Author author = DB.find(Author.class, id);
        if (author != null) author.delete();
    }
    public List<Author> getAllAuthors() {
        return DB.find(Author.class).findList();
    }

    // Методы для книг (с учетом quantity)
    public void createBook(Book book) { book.save(); }
    public void updateBook(String isbn, String title, int year, int quantity) {
        Book book = DB.find(Book.class, isbn);
        if (book != null) {
            book.setTitle(title);
            book.setYear(year);
            book.setQuantity(quantity);
            book.update();
        }
    }
    public void deleteBook(String isbn) {
        Book book = DB.find(Book.class, isbn);
        if (book != null) book.delete();
    }
    public List<Book> getAllBooks() {
        return DB.find(Book.class).findList();
    }

    // Методы для клиентов (без изменений)
    public void createClient(Client client) { client.save(); }
    public void updateClient(Long id, String fullName, String gender, int age) {
        Client client = DB.find(Client.class, id);
        if (client != null) {
            client.setFullName(fullName);
            client.setGender(gender);
            client.setAge(age);
            client.update();
        }
    }
    public void deleteClient(Long id) {
        Client client = DB.find(Client.class, id);
        if (client != null) client.delete();
    }
    public List<Client> getAllClients() {
        return DB.find(Client.class).findList();
    }

    // Методы для выдачи книг (с проверкой quantity)
    public void loanBook(Client client, Book book, LocalDate loanDate, int daysToReturn) {
        if (book.getQuantity() > 0) {
            LocalDate dueDate = loanDate.plusDays(daysToReturn);
            BookLoan loan = new BookLoan(client, book, loanDate, dueDate);
            book.setQuantity(book.getQuantity() - 1); // Уменьшаем количество
            book.update();
            loan.save();
        } else {
            throw new IllegalStateException("Книга '" + book.getTitle() + "' отсутствует в наличии!");
        }
    }

    public void returnBook(Long loanId, LocalDate returnDate) {
        BookLoan loan = DB.find(BookLoan.class, loanId);
        if (loan != null && !loan.isReturned()) {
            loan.setReturnDate(returnDate);
            Book book = loan.getBook();
            book.setQuantity(book.getQuantity() + 1); // Возвращаем книгу в фонд
            book.update();
            loan.update();
        }
    }

    public List<BookLoan> getActiveLoans() {
        return DB.find(BookLoan.class)
                .where()
                .eq("isReturned", false)
                .findList();
    }

    public List<BookLoan> getLoansByClient(Long clientId) {
        return DB.find(BookLoan.class)
                .where()
                .eq("client.id", clientId)
                .findList();
    }

    public List<BookLoan> getAllLoans() {
        return BookLoan.find.all();
    }

}
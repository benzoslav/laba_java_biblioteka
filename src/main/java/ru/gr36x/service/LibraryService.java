package ru.gr36x.service;

import io.ebean.DB;
import ru.gr36x.db.Author;
import ru.gr36x.db.Book;
import ru.gr36x.db.Client;
import java.util.List;

public class LibraryService {

    // Методы для работы с авторами
    public void createAuthor(Author author) {
        author.save();
    }

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
        if (author != null) {
            author.delete();
        }
    }

    public List<Author> getAllAuthors() {
        return DB.find(Author.class).findList();
    }

    // Методы для работы с книгами
    public void createBook(Book book) {
        book.save();
    }

    public void updateBook(String isbn, String title, int year) {
        Book book = DB.find(Book.class, isbn);
        if (book != null) {
            book.setTitle(title);
            book.setYear(year);
            book.update();
        }
    }

    public void deleteBook(String isbn) {
        Book book = DB.find(Book.class, isbn);
        if (book != null) {
            book.delete();
        }
    }

    public List<Book> getAllBooks() {
        return DB.find(Book.class).findList();
    }

    // Методы для работы с клиентами
    public void createClient(Client client) {
        client.save();
    }

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
        if (client != null) {
            client.delete();
        }
    }

    public List<Client> getAllClients() {
        return DB.find(Client.class).findList();
    }
}
package ru.gr36x.ui;

import ru.gr36x.db.Book;
import ru.gr36x.db.Client;
import ru.gr36x.db.BookLoan;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class BookLoanFormDialog extends JDialog {
    private JComboBox<Client> clientComboBox;
    private JComboBox<Book> bookComboBox;
    private JSpinner daysToReturnSpinner;
    private boolean saved = false;

    public BookLoanFormDialog(Frame owner, List<Client> clients, List<Book> availableBooks) {
        super(owner, true);
        setTitle("Выдать книгу");
        setSize(400, 200);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(4, 2, 5, 5));

        add(new JLabel("Клиент:"));
        clientComboBox = new JComboBox<>(clients.toArray(new Client[0]));
        add(clientComboBox);

        add(new JLabel("Книга:"));
        bookComboBox = new JComboBox<>(availableBooks.toArray(new Book[0]));
        add(bookComboBox);

        add(new JLabel("Срок выдачи (дней):"));
        daysToReturnSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
        add(daysToReturnSpinner);

        JButton saveBtn = new JButton("Выдать");
        saveBtn.addActionListener(e -> {
            if (clientComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Выберите клиента");
                return;
            }
            if (bookComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Выберите книгу");
                return;
            }
            saved = true;
            setVisible(false);
        });
        add(saveBtn);

        JButton cancelBtn = new JButton("Отмена");
        cancelBtn.addActionListener(e -> {
            saved = false;
            setVisible(false);
        });
        add(cancelBtn);
    }

    public boolean isSaved() {
        return saved;
    }

    public Client getSelectedClient() {
        return (Client) clientComboBox.getSelectedItem();
    }

    public Book getSelectedBook() {
        return (Book) bookComboBox.getSelectedItem();
    }

    public int getDaysToReturn() {
        return (int) daysToReturnSpinner.getValue();
    }
}
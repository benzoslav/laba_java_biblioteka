package ru.gr36x.ui;

import ru.gr36x.db.Author;
import ru.gr36x.db.Book;
import ru.gr36x.db.Client;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BookFormDialog extends JDialog {
    private JTextField isbnField;
    private JTextField titleField;
    private JTextField yearField;
    private JTextField quantityField;
    private JComboBox<Author> authorComboBox;
    private boolean saved = false;

    public BookFormDialog(Frame owner, Book book, List<Author> authors) {
        super(owner, true);
        setTitle(book == null ? "Добавить книгу" : "Редактировать книгу");
        setSize(350, 300);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(6, 2, 5, 5));

        add(new JLabel("ISBN:"));
        isbnField = new JTextField();
        add(isbnField);

        add(new JLabel("Название:"));
        titleField = new JTextField();
        add(titleField);

        add(new JLabel("Год:"));
        yearField = new JTextField();
        add(yearField);

        add(new JLabel("Количество:"));
        quantityField = new JTextField();
        add(quantityField);

        add(new JLabel("Автор:"));
        authorComboBox = new JComboBox<>(authors.toArray(new Author[0]));
        add(authorComboBox);

        if (book != null) {
            isbnField.setText(book.getIsbn());
            isbnField.setEnabled(false);
            titleField.setText(book.getTitle());
            yearField.setText(String.valueOf(book.getYear()));
            quantityField.setText(String.valueOf(book.getQuantity()));
            authorComboBox.setSelectedItem(book.getAuthor());
        }

        JButton saveBtn = new JButton("Сохранить");
        saveBtn.addActionListener(e -> {
            if (validateFields()) {
                saved = true;
                setVisible(false);
            }
        });
        add(saveBtn);

        JButton cancelBtn = new JButton("Отмена");
        cancelBtn.addActionListener(e -> {
            saved = false;
            setVisible(false);
        });
        add(cancelBtn);
    }

    private boolean validateFields() {
        if (isbnField.getText().trim().isEmpty() ||
                titleField.getText().trim().isEmpty() ||
                yearField.getText().trim().isEmpty() ||
                quantityField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Все поля должны быть заполнены");
            return false;
        }

        try {
            Integer.parseInt(yearField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, "Количество не может быть отрицательным");
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Год и количество должны быть числами");
            return false;
        }

        if (authorComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Выберите автора");
            return false;
        }

        return true;
    }

    public boolean isSaved() {
        return saved;
    }

    public String getIsbnValue() {
        return isbnField.getText().trim();
    }

    public String getTitleValue() {
        return titleField.getText().trim();
    }

    public int getYearValue() {
        return Integer.parseInt(yearField.getText().trim());
    }

    public int getQuantityValue() {
        return Integer.parseInt(quantityField.getText().trim());
    }

    public Author getSelectedAuthor() {
        return (Author) authorComboBox.getSelectedItem();
    }
}
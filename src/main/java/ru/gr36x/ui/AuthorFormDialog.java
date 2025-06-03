package ru.gr36x.ui;

import ru.gr36x.db.Author;

import javax.swing.*;
import java.awt.*;

public class AuthorFormDialog extends JDialog {
    private JTextField nameField;
    private JTextField countryField;
    private boolean saved = false;

    public AuthorFormDialog(Frame owner, Author author) {
        super(owner, true);
        setTitle(author == null ? "Добавить автора" : "Редактировать автора");
        setSize(300, 200);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(3, 2, 5, 5));

        add(new JLabel("ФИО:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Страна:"));
        countryField = new JTextField();
        add(countryField);

        if (author != null) {
            nameField.setText(author.getName());
            countryField.setText(author.getCountry());
        }

        JButton saveBtn = new JButton("Сохранить");
        saveBtn.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "ФИО не может быть пустым");
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

    public String getNameValue() {
        return nameField.getText().trim();
    }

    public String getCountryValue() {
        return countryField.getText().trim();
    }
}

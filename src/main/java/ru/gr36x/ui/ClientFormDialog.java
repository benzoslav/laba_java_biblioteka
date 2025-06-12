package ru.gr36x.ui;

import ru.gr36x.db.Client;

import javax.swing.*;
import java.awt.*;

public class ClientFormDialog extends JDialog {
    private JTextField fullNameField;
    private JComboBox<String> genderComboBox;
    private JTextField ageField;
    private boolean saved = false;

    public ClientFormDialog(Frame owner, Client client) {
        super(owner, true);
        setTitle(client == null ? "Добавить клиента" : "Редактировать клиента");
        setSize(300, 220);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(4, 2, 5, 5));

        add(new JLabel("ФИО:"));
        fullNameField = new JTextField();
        add(fullNameField);

        add(new JLabel("Возраст:"));
        ageField = new JTextField();
        add(ageField);

        add(new JLabel("Пол:"));
        genderComboBox = new JComboBox<>(new String[]{"Мужской", "Женский"});
        add(genderComboBox);

        if (client != null) {
            fullNameField.setText(client.getFullName());
            genderComboBox.setSelectedItem(client.getGender());
            ageField.setText(String.valueOf(client.getAge()));
        }

        JButton saveBtn = new JButton("Сохранить");
        saveBtn.addActionListener(e -> {
            if (fullNameField.getText().trim().isEmpty() ||
                    ageField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Все поля должны быть заполнены");
                return;
            }
            try {
                Integer.parseInt(ageField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Возраст должен быть числом");
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

    public String getFullName() {
        return fullNameField.getText().trim();
    }

    public String getGender() {
        return (String) genderComboBox.getSelectedItem();
    }

    public int getAge() {
        return Integer.parseInt(ageField.getText().trim());
    }
}

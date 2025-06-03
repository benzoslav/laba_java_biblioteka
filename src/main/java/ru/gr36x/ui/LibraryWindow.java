package ru.gr36x.ui;

import ru.gr36x.db.Author;
import ru.gr36x.db.Book;
import ru.gr36x.db.Client;
import ru.gr36x.service.LibraryService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class LibraryWindow extends JFrame {
    private final LibraryService service;

    private AuthorTableModel authorTableModel;
    private BookTableModel bookTableModel;
    private ClientTableModel clientTableModel;

    public LibraryWindow(LibraryService service) {
        this.service = service;

        setTitle("Библиотека");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Авторы", createAuthorPanel());
        tabs.add("Книги", createBookPanel());
        tabs.add("Клиенты", createClientPanel());

        add(tabs);
        setVisible(true);
    }

    public static void launch(LibraryService service) {
        SwingUtilities.invokeLater(() -> new LibraryWindow(service));
    }

    // ---------------- Автор ----------------

    private JPanel createAuthorPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        authorTableModel = new AuthorTableModel(service.getAllAuthors());
        JTable table = new JTable(authorTableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        TableRowSorter<AuthorTableModel> sorter = new TableRowSorter<>(authorTableModel);
        table.setRowSorter(sorter);

        JPanel filterPanel = new JPanel(new BorderLayout());
        JLabel searchLabel = new JLabel("Поиск автора: ");
        JTextField searchField = new JTextField();

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter();
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter();
            }
            private void applyFilter() {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1)); // 1 = колонка "Имя"
                }
            }
        });

        filterPanel.add(searchLabel, BorderLayout.WEST);
        filterPanel.add(searchField, BorderLayout.CENTER);

        // --- Кнопки ---
        JButton addBtn = new JButton("Добавить");
        addBtn.addActionListener(e -> {
            AuthorFormDialog dialog = new AuthorFormDialog(this, null);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                Author author = new Author(dialog.getNameValue(), dialog.getCountryValue());
                service.createAuthor(author);
                authorTableModel.setAuthors(service.getAllAuthors());
            }
        });

        JButton editBtn = new JButton("Редактировать");
        editBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = table.convertRowIndexToModel(selectedRow);
                Author author = authorTableModel.getAuthorAt(modelRow);
                AuthorFormDialog dialog = new AuthorFormDialog(this, author);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    service.updateAuthor(author.getId(), dialog.getNameValue(), dialog.getCountryValue());
                    authorTableModel.setAuthors(service.getAllAuthors());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Выберите автора для редактирования");
            }
        });

        JButton deleteBtn = new JButton("Удалить");
        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = table.convertRowIndexToModel(selectedRow);
                Author author = authorTableModel.getAuthorAt(modelRow);
                service.deleteAuthor(author.getId());
                authorTableModel.setAuthors(service.getAllAuthors());
            } else {
                JOptionPane.showMessageDialog(this, "Выберите автора для удаления");
            }
        });

        JPanel buttons = new JPanel();
        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(deleteBtn);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        return panel;
    }

    static class AuthorTableModel extends AbstractTableModel {
        private List<Author> authors;
        private final String[] columns = {"ID", "ФИО", "Страна"};

        public AuthorTableModel(List<Author> authors) {
            this.authors = authors;
        }

        public void setAuthors(List<Author> authors) {
            this.authors = authors;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return authors.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Author author = authors.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> author.getId();
                case 1 -> author.getName();
                case 2 -> author.getCountry();
                default -> null;
            };
        }

        public Author getAuthorAt(int row) {
            return authors.get(row);
        }
    }

    // ---------------- Книги ----------------

    private JPanel createBookPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        bookTableModel = new BookTableModel(service.getAllBooks());
        JTable table = new JTable(bookTableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Фильтр по названию книги
        TableRowSorter<BookTableModel> sorter = new TableRowSorter<>(bookTableModel);
        table.setRowSorter(sorter);

        JPanel filterPanel = new JPanel(new BorderLayout());
        JLabel searchLabel = new JLabel("Поиск по названию: ");
        JTextField searchField = new JTextField();

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter();
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter();
            }
            private void applyFilter() {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1)); // 1 = колонка "Название"
                }
            }
        });

        filterPanel.add(searchLabel, BorderLayout.WEST);
        filterPanel.add(searchField, BorderLayout.CENTER);

        JButton addBtn = new JButton("Добавить");
        addBtn.addActionListener(e -> {
            List<Author> authors = service.getAllAuthors();
            if (authors.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Сначала добавьте авторов");
                return;
            }
            BookFormDialog dialog = new BookFormDialog(this, null, authors);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                Book book = new Book(dialog.getIsbnValue(), dialog.getTitleValue(), dialog.getYearValue(), dialog.getSelectedAuthor());
                service.createBook(book);
                bookTableModel.setBooks(service.getAllBooks());
            }
        });

        JButton editBtn = new JButton("Редактировать");
        editBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = table.convertRowIndexToModel(selectedRow);
                Book book = bookTableModel.getBookAt(modelRow);
                List<Author> authors = service.getAllAuthors();
                BookFormDialog dialog = new BookFormDialog(this, book, authors);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    service.updateBook(book.getIsbn(), dialog.getTitleValue(), dialog.getYearValue());
                    // Обновление автора можно добавить отдельно, если нужно
                    bookTableModel.setBooks(service.getAllBooks());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Выберите книгу для редактирования");
            }
        });

        JButton deleteBtn = new JButton("Удалить");
        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = table.convertRowIndexToModel(selectedRow);
                Book book = bookTableModel.getBookAt(modelRow);
                service.deleteBook(book.getIsbn());
                bookTableModel.setBooks(service.getAllBooks());
            } else {
                JOptionPane.showMessageDialog(this, "Выберите книгу для удаления");
            }
        });

        JPanel buttons = new JPanel();
        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(deleteBtn);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        return panel;
    }

    static class BookTableModel extends AbstractTableModel {
        private List<Book> books;
        private final String[] columns = {"ISBN", "Название", "Год", "Автор"};

        public BookTableModel(List<Book> books) {
            this.books = books;
        }

        public void setBooks(List<Book> books) {
            this.books = books;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return books.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Book book = books.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> book.getIsbn();
                case 1 -> book.getTitle();
                case 2 -> book.getYear();
                case 3 -> book.getAuthor() != null ? book.getAuthor().getName() : "";
                default -> null;
            };
        }

        public Book getBookAt(int row) {
            return books.get(row);
        }
    }

    // ---------------- Клиенты ----------------

    private JPanel createClientPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        clientTableModel = new ClientTableModel(service.getAllClients());
        JTable table = new JTable(clientTableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Фильтр по ФИО клиента
        TableRowSorter<ClientTableModel> sorter = new TableRowSorter<>(clientTableModel);
        table.setRowSorter(sorter);

        JPanel filterPanel = new JPanel(new BorderLayout());
        JLabel searchLabel = new JLabel("Поиск по ФИО: ");
        JTextField searchField = new JTextField();

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter();
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter();
            }
            private void applyFilter() {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1)); // 1 = колонка "ФИО"
                }
            }
        });

        filterPanel.add(searchLabel, BorderLayout.WEST);
        filterPanel.add(searchField, BorderLayout.CENTER);

        JButton addBtn = new JButton("Добавить");
        addBtn.addActionListener(e -> {
            ClientFormDialog dialog = new ClientFormDialog(this, null);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                Client client = new Client(dialog.getFullName(), dialog.getGender(), dialog.getAge());
                service.createClient(client);
                clientTableModel.setClients(service.getAllClients());
            }
        });

        JButton editBtn = new JButton("Редактировать");
        editBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = table.convertRowIndexToModel(selectedRow);
                Client client = clientTableModel.getClientAt(modelRow);
                ClientFormDialog dialog = new ClientFormDialog(this, client);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    service.updateClient(client.getId(), dialog.getFullName(), dialog.getGender(), dialog.getAge());
                    clientTableModel.setClients(service.getAllClients());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Выберите клиента для редактирования");
            }
        });

        JButton deleteBtn = new JButton("Удалить");
        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = table.convertRowIndexToModel(selectedRow);
                Client client = clientTableModel.getClientAt(modelRow);
                service.deleteClient(client.getId());
                clientTableModel.setClients(service.getAllClients());
            } else {
                JOptionPane.showMessageDialog(this, "Выберите клиента для удаления");
            }
        });

        JPanel buttons = new JPanel();
        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(deleteBtn);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        return panel;
    }

    static class ClientTableModel extends AbstractTableModel {
        private List<Client> clients;
        private final String[] columns = {"ID", "ФИО", "Пол", "Возраст"};

        public ClientTableModel(List<Client> clients) {
            this.clients = clients;
        }

        public void setClients(List<Client> clients) {
            this.clients = clients;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return clients.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Client client = clients.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> client.getId();
                case 1 -> client.getFullName();
                case 2 -> client.getGender();
                case 3 -> client.getAge();
                default -> null;
            };
        }

        public Client getClientAt(int row) {
            return clients.get(row);
        }
    }
}

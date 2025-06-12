package ru.gr36x.ui;

import ru.gr36x.db.Author;
import ru.gr36x.db.Book;
import ru.gr36x.db.Client;
import ru.gr36x.db.BookLoan;
import ru.gr36x.service.LibraryService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LibraryWindow extends JFrame {
    private final LibraryService service;
    private AuthorTableModel authorTableModel;
    private BookTableModel bookTableModel;
    private ClientTableModel clientTableModel;
    private LoanTableModel loanTableModel;

    public LibraryWindow(LibraryService service) {
        this.service = service;

        setTitle("Библиотека");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Авторы", createAuthorPanel());
        tabs.add("Книги", createBookPanel());
        tabs.add("Клиенты", createClientPanel());
        tabs.add("Выдача", createLoanPanel());

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
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            private void applyFilter() {
                String text = searchField.getText();
                if (text.trim().isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1));
            }
        });

        filterPanel.add(searchLabel, BorderLayout.WEST);
        filterPanel.add(searchField, BorderLayout.CENTER);

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
            int sel = table.getSelectedRow();
            if (sel >= 0) {
                int modelRow = table.convertRowIndexToModel(sel);
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
            int sel = table.getSelectedRow();
            if (sel >= 0) {
                int modelRow = table.convertRowIndexToModel(sel);
                Author author = authorTableModel.getAuthorAt(modelRow);
                service.deleteAuthor(author.getId());
                authorTableModel.setAuthors(service.getAllAuthors());
            } else {
                JOptionPane.showMessageDialog(this, "Выберите автора для удаления");
            }
        });

        JButton refreshBtn = new JButton("Обновить");
        refreshBtn.addActionListener(e -> authorTableModel.setAuthors(service.getAllAuthors()));

        JPanel buttons = new JPanel();
        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(deleteBtn);
        buttons.add(refreshBtn);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    static class AuthorTableModel extends AbstractTableModel {
        private List<Author> authors;
        private final String[] columns = {"ID", "ФИО", "Страна"};

        public AuthorTableModel(List<Author> authors) { this.authors = authors; }

        public void setAuthors(List<Author> authors) {
            this.authors = authors;
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return authors.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int col) { return columns[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            Author a = authors.get(row);
            return switch (col) {
                case 0 -> a.getId();
                case 1 -> a.getName();
                case 2 -> a.getCountry();
                default -> null;
            };
        }

        public Author getAuthorAt(int row) { return authors.get(row); }
    }

    // ---------------- Книги ----------------

    private JPanel createBookPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        bookTableModel = new BookTableModel(service.getAllBooks());
        JTable table = new JTable(bookTableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        TableRowSorter<BookTableModel> sorter = new TableRowSorter<>(bookTableModel);
        table.setRowSorter(sorter);

        JPanel filterPanel = new JPanel(new BorderLayout());
        JLabel searchLabel = new JLabel("Поиск по названию: ");
        JTextField searchField = new JTextField();

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            private void applyFilter() {
                String text = searchField.getText();
                if (text.trim().isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1));
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
                Book book = new Book(dialog.getIsbnValue(), dialog.getTitleValue(),
                        dialog.getYearValue(), dialog.getSelectedAuthor(), dialog.getQuantityValue());
                service.createBook(book);
                bookTableModel.setBooks(service.getAllBooks());
            }
        });

        JButton editBtn = new JButton("Редактировать");
        editBtn.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel >= 0) {
                int mr = table.convertRowIndexToModel(sel);
                Book book = bookTableModel.getBookAt(mr);
                List<Author> authors = service.getAllAuthors();
                BookFormDialog dialog = new BookFormDialog(this, book, authors);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    service.updateBook(book.getIsbn(), dialog.getTitleValue(),
                            dialog.getYearValue(), dialog.getQuantityValue());
                    bookTableModel.setBooks(service.getAllBooks());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Выберите книгу для редактирования");
            }
        });

        JButton deleteBtn = new JButton("Удалить");
        deleteBtn.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel >= 0) {
                int mr = table.convertRowIndexToModel(sel);
                Book book = bookTableModel.getBookAt(mr);
                service.deleteBook(book.getIsbn());
                bookTableModel.setBooks(service.getAllBooks());
            } else {
                JOptionPane.showMessageDialog(this, "Выберите книгу для удаления");
            }
        });

        JButton refreshBtn = new JButton("Обновить");
        refreshBtn.addActionListener(e -> bookTableModel.setBooks(service.getAllBooks()));

        JPanel buttons = new JPanel();
        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(deleteBtn);
        buttons.add(refreshBtn);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    static class BookTableModel extends AbstractTableModel {
        private List<Book> books;
        private final String[] columns = {"ISBN", "Название", "Год", "Автор", "Кол‑во"};

        public BookTableModel(List<Book> books) { this.books = books; }

        public void setBooks(List<Book> books) {
            this.books = books;
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return books.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int col) { return columns[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            Book b = books.get(row);
            return switch (col) {
                case 0 -> b.getIsbn();
                case 1 -> b.getTitle();
                case 2 -> b.getYear();
                case 3 -> b.getAuthor() != null ? b.getAuthor().getName() : "";
                case 4 -> b.getQuantity();
                default -> null;
            };
        }

        public Book getBookAt(int row) { return books.get(row); }
    }

    // ---------------- Клиенты ----------------

    private JPanel createClientPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        clientTableModel = new ClientTableModel(service.getAllClients());
        JTable table = new JTable(clientTableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        TableRowSorter<ClientTableModel> sorter = new TableRowSorter<>(clientTableModel);
        table.setRowSorter(sorter);

        JPanel filterPanel = new JPanel(new BorderLayout());
        JLabel searchLabel = new JLabel("Поиск по ФИО: ");
        JTextField searchField = new JTextField();

        // Добавляем слушатель изменений текста в поле поиска
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            // Срабатывает при добавлении текста
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter();
            }

            // Срабатывает при удалении текста
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter();
            }

            // Срабатывает при изменении стиля текста
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                applyFilter();
            }

            // Основной метод применения фильтра
            private void applyFilter() {
                // Получаем текущий текст из поля поиска
                String text = searchField.getText();

                // Если поле пустое или содержит только пробелы
                if (text.trim().isEmpty()) {
                    // Отключаем фильтр - показываем все строки
                    sorter.setRowFilter(null);
                } else {
            /* Включаем фильтр с параметрами:
               "(?i)" - флаг регистронезависимого поиска
               text   - искомый текст
               1      - номер столбца для поиска (индексация с 0)
                        (в данном случае ищем во втором столбце - "Название")
            */
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1));
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
                Client c = new Client(dialog.getFullName(), dialog.getGender(), dialog.getAge());
                service.createClient(c);
                clientTableModel.setClients(service.getAllClients());
            }
        });

        JButton editBtn = new JButton("Редактировать");
        editBtn.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel >= 0) {
                int mr = table.convertRowIndexToModel(sel);
                Client c = clientTableModel.getClientAt(mr);
                ClientFormDialog dialog = new ClientFormDialog(this, c);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    service.updateClient(c.getId(), dialog.getFullName(), dialog.getGender(), dialog.getAge());
                    clientTableModel.setClients(service.getAllClients());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Выберите клиента для редактирования");
            }
        });

        JButton deleteBtn = new JButton("Удалить");
        deleteBtn.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel >= 0) {
                int mr = table.convertRowIndexToModel(sel);
                Client c = clientTableModel.getClientAt(mr);
                service.deleteClient(c.getId());
                clientTableModel.setClients(service.getAllClients());
            } else {
                JOptionPane.showMessageDialog(this, "Выберите клиента для удаления");
            }
        });

        JButton refreshBtn = new JButton("Обновить");
        refreshBtn.addActionListener(e -> clientTableModel.setClients(service.getAllClients()));

        JPanel buttons = new JPanel();
        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(deleteBtn);
        buttons.add(refreshBtn);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    static class ClientTableModel extends AbstractTableModel {
        private List<Client> clients;
        private final String[] columns = {"ID", "ФИО", "Пол", "Возраст"};

        public ClientTableModel(List<Client> clients) { this.clients = clients; }

        public void setClients(List<Client> clients) {
            this.clients = clients;
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return clients.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int col) { return columns[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            Client c = clients.get(row);
            return switch (col) {
                case 0 -> c.getId();
                case 1 -> c.getFullName();
                case 2 -> c.getGender();
                case 3 -> c.getAge();
                default -> null;
            };
        }

        public Client getClientAt(int row) { return clients.get(row); }
    }

    // ---------------- Выдача ----------------

    private JPanel createLoanPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Загружаем все выдачи сразу
        loanTableModel = new LoanTableModel(service.getAllLoans());
        JTable table = new JTable(loanTableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        TableRowSorter<LoanTableModel> sorter = new TableRowSorter<>(loanTableModel);
        table.setRowSorter(sorter);

        // --- Фильтр по ФИО клиента ---
        JPanel filterPanel = new JPanel(new BorderLayout());
        JLabel searchLabel = new JLabel("Поиск по ФИО клиента: ");
        JTextField searchField = new JTextField();

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            private void applyFilter() {
                String text = searchField.getText();
                if (text.trim().isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1)); // 1 — колонка ФИО клиента
            }
        });

        filterPanel.add(searchLabel, BorderLayout.WEST);
        filterPanel.add(searchField, BorderLayout.CENTER);

        JButton giveBtn = new JButton("Выдать книгу");
        giveBtn.addActionListener(e -> {
            List<Client> clients = service.getAllClients();
            List<Book> books = service.getAllBooks();
            if (clients.isEmpty() || books.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Добавьте клиентов и книги");
                return;
            }
            BookLoanFormDialog dlg = new BookLoanFormDialog(this, clients, books);
            dlg.setVisible(true);
            if (dlg.isSaved()) {
                service.loanBook(dlg.getSelectedClient(), dlg.getSelectedBook(),
                        LocalDate.now(), dlg.getDaysToReturn());
                // Обновляем все записи, а не только активные
                loanTableModel.setLoans(service.getAllLoans());
                bookTableModel.setBooks(service.getAllBooks());
            }
        });

        JButton retBtn = new JButton("Вернуть книгу");
        retBtn.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel >= 0) {
                int mr = table.convertRowIndexToModel(sel);
                BookLoan loan = loanTableModel.getLoanAt(mr);
                if (!loan.isReturned()) {
                    service.returnBook(loan.getId(), LocalDate.now());
                    loanTableModel.setLoans(service.getAllLoans()); // Все записи
                    bookTableModel.setBooks(service.getAllBooks());
                } else {
                    JOptionPane.showMessageDialog(this, "Эта запись уже закрыта");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Выберите выдачу");
            }
        });

        JButton refreshBtn = new JButton("Обновить");
        refreshBtn.addActionListener(e -> loanTableModel.setLoans(service.getAllLoans()));

        JPanel buttons = new JPanel();
        buttons.add(giveBtn);
        buttons.add(retBtn);
        buttons.add(refreshBtn);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);

        return panel;
    }



    static class LoanTableModel extends AbstractTableModel {
        private List<BookLoan> loans;
        private final String[] columns = {"ID", "Клиент", "Книга", "Выдана", "Срок", "Возврат", "Статус"};

        public LoanTableModel(List<BookLoan> loans) { this.loans = loans; }

        public void setLoans(List<BookLoan> loans) {
            this.loans = loans;
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return loans.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int col) { return columns[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            BookLoan l = loans.get(row);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return switch (col) {
                case 0 -> l.getId();
                case 1 -> l.getClient().getFullName();
                case 2 -> l.getBook().getTitle();
                case 3 -> l.getLoanDate().format(fmt);
                case 4 -> l.getDueDate().format(fmt);
                case 5 -> (l.getReturnDate() != null ? l.getReturnDate().format(fmt) : "");
                case 6 -> l.isReturned() ? "Возвращена" : "На руках";
                default -> null;
            };
        }

        public BookLoan getLoanAt(int row) { return loans.get(row); }
    }
}

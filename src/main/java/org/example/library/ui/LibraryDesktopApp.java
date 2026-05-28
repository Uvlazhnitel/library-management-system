package org.example.library.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import org.example.library.exception.ActiveLoanNotFoundException;
import org.example.library.exception.BookAlreadyAvailableException;
import org.example.library.exception.BookNotFoundException;
import org.example.library.exception.BookUnavailableException;
import org.example.library.exception.BorrowedBookStateException;
import org.example.library.exception.LibraryOperationException;
import org.example.library.exception.LoanReaderNotFoundException;
import org.example.library.exception.ReaderNotFoundException;
import org.example.library.model.Book;
import org.example.library.model.Reader;
import org.example.library.service.Library;
import org.example.library.service.Logic;

public final class LibraryDesktopApp {

    private LibraryDesktopApp() {
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Falling back to the default look and feel is fine for this local UI.
        }

        SwingUtilities.invokeLater(() -> {
            LibraryDesktopFrame frame = new LibraryDesktopFrame();
            frame.setVisible(true);
        });
    }

    private static final class LibraryDesktopFrame extends JFrame {
        private final DefaultTableModel booksModel;
        private final DefaultTableModel readersModel;
        private final JTable booksTable;
        private final JTable readersTable;
        private final JTextArea eventLog;

        private final JTextField searchField;
        private final JTextField bookIsbnField;
        private final JTextField bookTitleField;
        private final JTextField bookAuthorField;
        private final JTextField bookYearField;
        private final JTextField bookGenreField;

        private final JTextField readerIdField;
        private final JTextField readerNameField;
        private final JTextField readerEmailField;

        private final JTextField loanIsbnField;
        private final JTextField loanReaderIdField;

        private String currentSearchQuery = "";

        private LibraryDesktopFrame() {
            super("Library Management System");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setMinimumSize(new Dimension(1100, 760));
            setSize(1280, 860);
            setLocationByPlatform(true);

            booksModel = new DefaultTableModel(
                    new Object[] {"ISBN", "Title", "Author", "Year", "Genre", "Status"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            readersModel = new DefaultTableModel(
                    new Object[] {"ID", "Name", "Email", "Borrowed", "Borrowed ISBNs"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            booksTable = new JTable(booksModel);
            readersTable = new JTable(readersModel);
            eventLog = new JTextArea(8, 80);
            eventLog.setEditable(false);
            eventLog.setLineWrap(true);
            eventLog.setWrapStyleWord(true);

            searchField = new JTextField(20);
            bookIsbnField = new JTextField(18);
            bookTitleField = new JTextField(18);
            bookAuthorField = new JTextField(18);
            bookYearField = new JTextField(18);
            bookGenreField = new JTextField(18);
            readerIdField = new JTextField(18);
            readerNameField = new JTextField(18);
            readerEmailField = new JTextField(18);
            loanIsbnField = new JTextField(14);
            loanReaderIdField = new JTextField(14);

            setContentPane(buildContent());
            refreshAllTables();
            appendLog("Desktop UI ready. Local data store is empty until you add or load data.");
        }

        private JPanel buildContent() {
            JPanel root = new JPanel(new BorderLayout(12, 12));
            root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            root.add(buildToolbar(), BorderLayout.NORTH);

            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Books", buildBooksTab());
            tabs.addTab("Readers", buildReadersTab());

            JSplitPane splitPane = new JSplitPane(
                    JSplitPane.VERTICAL_SPLIT,
                    tabs,
                    new JScrollPane(eventLog));
            splitPane.setResizeWeight(0.8);
            splitPane.setBorder(null);

            root.add(splitPane, BorderLayout.CENTER);
            return root;
        }

        private JPanel buildToolbar() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

            JButton refreshButton = new JButton("Refresh");
            refreshButton.addActionListener(event -> refreshAllTables());
            panel.add(refreshButton);

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(event -> {
                Library.saveLibrary();
                appendLog("Library data saved.");
            });
            panel.add(saveButton);

            JButton loadButton = new JButton("Load");
            loadButton.addActionListener(event -> {
                Library.loadLibrary();
                currentSearchQuery = "";
                searchField.setText("");
                refreshAllTables();
                appendLog("Library data loaded and tables refreshed.");
            });
            panel.add(loadButton);

            return panel;
        }

        private JPanel buildBooksTab() {
            JPanel panel = new JPanel(new BorderLayout(12, 12));
            panel.add(buildSearchPanel(), BorderLayout.NORTH);
            panel.add(new JScrollPane(booksTable), BorderLayout.CENTER);

            JPanel forms = new JPanel();
            forms.setLayout(new BoxLayout(forms, BoxLayout.Y_AXIS));
            forms.add(buildAddBookPanel());
            forms.add(Box.createVerticalStrut(12));
            forms.add(buildBookActionsPanel());
            panel.add(forms, BorderLayout.SOUTH);

            return panel;
        }

        private JPanel buildReadersTab() {
            JPanel panel = new JPanel(new BorderLayout(12, 12));
            panel.add(new JScrollPane(readersTable), BorderLayout.CENTER);
            panel.add(buildRegisterReaderPanel(), BorderLayout.SOUTH);
            return panel;
        }

        private JPanel buildSearchPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panel.setBorder(BorderFactory.createTitledBorder("Search Catalog"));

            JButton searchButton = new JButton("Search");
            searchButton.addActionListener(event -> applySearchFilter());

            JButton clearButton = new JButton("Clear Filter");
            clearButton.addActionListener(event -> {
                currentSearchQuery = "";
                searchField.setText("");
                refreshBooksTable();
                appendLog("Search filter cleared.");
            });

            panel.add(new JLabel("Keyword:"));
            panel.add(searchField);
            panel.add(searchButton);
            panel.add(clearButton);
            return panel;
        }

        private JPanel buildAddBookPanel() {
            JPanel panel = createFormPanel("Add Book");
            GridBagConstraints gbc = createBaseConstraints();

            addLabelAndField(panel, gbc, 0, "ISBN", bookIsbnField);
            addLabelAndField(panel, gbc, 1, "Title", bookTitleField);
            addLabelAndField(panel, gbc, 2, "Author", bookAuthorField);
            addLabelAndField(panel, gbc, 3, "Year", bookYearField);
            addLabelAndField(panel, gbc, 4, "Genre", bookGenreField);

            JButton addButton = new JButton("Add Book");
            addButton.addActionListener(event -> addBookFromForm());
            gbc.gridx = 1;
            gbc.gridy = 5;
            gbc.anchor = GridBagConstraints.WEST;
            panel.add(addButton, gbc);

            return panel;
        }

        private JPanel buildBookActionsPanel() {
            JPanel panel = createFormPanel("Book Actions");
            GridBagConstraints gbc = createBaseConstraints();

            addLabelAndField(panel, gbc, 0, "Book ISBN", loanIsbnField);
            addLabelAndField(panel, gbc, 1, "Reader ID", loanReaderIdField);

            JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            JButton borrowButton = new JButton("Borrow");
            borrowButton.addActionListener(event -> borrowBookFromForm());
            JButton returnButton = new JButton("Return");
            returnButton.addActionListener(event -> returnBookFromForm());
            JButton availabilityButton = new JButton("Check Availability");
            availabilityButton.addActionListener(event -> checkAvailabilityFromForm());
            JButton removeButton = new JButton("Remove Book");
            removeButton.addActionListener(event -> removeBookFromForm());

            buttonRow.add(borrowButton);
            buttonRow.add(returnButton);
            buttonRow.add(availabilityButton);
            buttonRow.add(removeButton);

            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.anchor = GridBagConstraints.WEST;
            panel.add(buttonRow, gbc);

            return panel;
        }

        private JPanel buildRegisterReaderPanel() {
            JPanel panel = createFormPanel("Register Reader");
            GridBagConstraints gbc = createBaseConstraints();

            addLabelAndField(panel, gbc, 0, "Reader ID", readerIdField);
            addLabelAndField(panel, gbc, 1, "Name", readerNameField);
            addLabelAndField(panel, gbc, 2, "Email", readerEmailField);

            JButton registerButton = new JButton("Register Reader");
            registerButton.addActionListener(event -> registerReaderFromForm());
            gbc.gridx = 1;
            gbc.gridy = 3;
            gbc.anchor = GridBagConstraints.WEST;
            panel.add(registerButton, gbc);

            return panel;
        }

        private void addBookFromForm() {
            String isbn = trim(bookIsbnField.getText());
            String title = trim(bookTitleField.getText());
            String author = trim(bookAuthorField.getText());
            String genre = trim(bookGenreField.getText());

            if (isbn.isEmpty() || title.isEmpty() || author.isEmpty() || genre.isEmpty()) {
                showValidationMessage("All book fields are required.");
                return;
            }

            int year;
            try {
                year = Integer.parseInt(trim(bookYearField.getText()));
            } catch (NumberFormatException e) {
                showValidationMessage("Year must be a valid number.");
                return;
            }

            Library.addBook(new Book(isbn, title, author, year, genre));
            clearBookForm();
            refreshBooksTable();
            appendLog("Added book: " + isbn + " - " + title);
        }

        private void registerReaderFromForm() {
            String id = trim(readerIdField.getText());
            String name = trim(readerNameField.getText());
            String email = trim(readerEmailField.getText());

            if (id.isEmpty() || name.isEmpty() || email.isEmpty()) {
                showValidationMessage("All reader fields are required.");
                return;
            }

            Library.addReader(new Reader(id, name, email));
            clearReaderForm();
            refreshReadersTable();
            appendLog("Registered reader: " + id + " - " + name);
        }

        private void applySearchFilter() {
            currentSearchQuery = trim(searchField.getText());
            refreshBooksTable();
            if (currentSearchQuery.isEmpty()) {
                appendLog("Showing all books.");
            } else {
                appendLog("Applied search filter: " + currentSearchQuery);
            }
        }

        private void borrowBookFromForm() {
            String isbn = trim(loanIsbnField.getText());
            String readerId = trim(loanReaderIdField.getText());
            if (isbn.isEmpty() || readerId.isEmpty()) {
                showValidationMessage("Borrowing requires both ISBN and Reader ID.");
                return;
            }

            try {
                Logic.borrowBook(isbn, readerId);
                refreshAllTables();
                appendLog("Borrowed book " + isbn + " for reader " + readerId + ".");
            } catch (LibraryOperationException e) {
                showOperationMessage(mapLibraryOperationMessage(e));
            }
        }

        private void returnBookFromForm() {
            String isbn = trim(loanIsbnField.getText());
            if (isbn.isEmpty()) {
                showValidationMessage("Returning requires a book ISBN.");
                return;
            }

            try {
                Logic.returnBook(isbn);
                refreshAllTables();
                appendLog("Returned book " + isbn + ".");
            } catch (LibraryOperationException e) {
                showOperationMessage(mapLibraryOperationMessage(e));
            }
        }

        private void checkAvailabilityFromForm() {
            String isbn = trim(loanIsbnField.getText());
            if (isbn.isEmpty()) {
                showValidationMessage("Availability check requires a book ISBN.");
                return;
            }

            Book book = findBookByExactIsbn(isbn);
            if (book == null) {
                showOperationMessage("Book with the given ISBN not found.");
                return;
            }

            String message = String.format(
                    "Book '%s' is %s.",
                    book.getTitle(),
                    book.isAvailable() ? "available" : "not available");
            appendLog(message);
            JOptionPane.showMessageDialog(this, message, "Availability", JOptionPane.INFORMATION_MESSAGE);
        }

        private void removeBookFromForm() {
            String isbn = trim(loanIsbnField.getText());
            if (isbn.isEmpty()) {
                int selectedRow = booksTable.getSelectedRow();
                if (selectedRow >= 0) {
                    isbn = String.valueOf(booksModel.getValueAt(selectedRow, 0));
                }
            }

            if (isbn.isEmpty()) {
                showValidationMessage("Choose a book row or enter an ISBN to remove.");
                return;
            }

            if (Library.removeBook(isbn)) {
                refreshBooksTable();
                appendLog("Removed book " + isbn + ".");
            } else {
                showOperationMessage("Book with the given ISBN not found.");
            }
        }

        private void refreshAllTables() {
            refreshBooksTable();
            refreshReadersTable();
        }

        private void refreshBooksTable() {
            booksModel.setRowCount(0);
            List<Book> books = currentSearchQuery.isEmpty()
                    ? Library.getBooks()
                    : Library.searchBooks(currentSearchQuery);

            for (Book book : books) {
                booksModel.addRow(new Object[] {
                    safe(book.getIsbn()),
                    safe(book.getTitle()),
                    safe(book.getAuthor()),
                    book.getYear(),
                    safe(book.getGenre()),
                    book.isAvailable() ? "Available" : "Borrowed"
                });
            }
        }

        private void refreshReadersTable() {
            readersModel.setRowCount(0);
            for (Reader reader : Library.getReaders()) {
                readersModel.addRow(new Object[] {
                    safe(reader.getId()),
                    safe(reader.getName()),
                    safe(reader.getEmail()),
                    reader.getBorrowedBookIsbns().size(),
                    String.join(", ", reader.getBorrowedBookIsbns())
                });
            }
        }

        private Book findBookByExactIsbn(String isbn) {
            for (Book book : Library.getBooks()) {
                if (isbn.equals(book.getIsbn())) {
                    return book;
                }
            }

            return null;
        }

        private void showValidationMessage(String message) {
            appendLog(message);
            JOptionPane.showMessageDialog(this, message, "Invalid input", JOptionPane.WARNING_MESSAGE);
        }

        private void showOperationMessage(String message) {
            appendLog(message);
            JOptionPane.showMessageDialog(this, message, "Operation result", JOptionPane.INFORMATION_MESSAGE);
        }

        private void appendLog(String message) {
            eventLog.append(message + System.lineSeparator());
            eventLog.setCaretPosition(eventLog.getDocument().getLength());
        }

        private void clearBookForm() {
            bookIsbnField.setText("");
            bookTitleField.setText("");
            bookAuthorField.setText("");
            bookYearField.setText("");
            bookGenreField.setText("");
        }

        private void clearReaderForm() {
            readerIdField.setText("");
            readerNameField.setText("");
            readerEmailField.setText("");
        }

        private JPanel createFormPanel(String title) {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createTitledBorder(title));
            return panel;
        }

        private GridBagConstraints createBaseConstraints() {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 6, 4, 6);
            gbc.anchor = GridBagConstraints.WEST;
            return gbc;
        }

        private void addLabelAndField(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0;
            gbc.fill = GridBagConstraints.NONE;
            panel.add(new JLabel(label + ":"), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(field, gbc);
        }

        private String mapLibraryOperationMessage(LibraryOperationException e) {
            if (e instanceof BookNotFoundException) {
                return "Book with the given ISBN not found.";
            }
            if (e instanceof BookUnavailableException) {
                return "Book is currently not available for borrowing.";
            }
            if (e instanceof ReaderNotFoundException) {
                return "Reader with the given ID not found.";
            }
            if (e instanceof LoanReaderNotFoundException) {
                return "Reader associated with the active loan not found.";
            }
            if (e instanceof BookAlreadyAvailableException) {
                return "Book is already marked as available.";
            }
            if (e instanceof ActiveLoanNotFoundException) {
                return "No active loan found for this book.";
            }
            if (e instanceof BorrowedBookStateException) {
                return "Reader does not have this book marked as borrowed.";
            }

            return "Library operation failed.";
        }

        private String trim(String value) {
            return value == null ? "" : value.trim();
        }

        private String safe(String value) {
            return value == null ? "" : value;
        }
    }
}

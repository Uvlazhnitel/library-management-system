package org.example.library.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import org.example.library.exception.ActiveLoanNotFoundException;
import org.example.library.exception.BookAlreadyAvailableException;
import org.example.library.exception.BookNotFoundException;
import org.example.library.exception.BookUnavailableException;
import org.example.library.exception.BorrowedBookStateException;
import org.example.library.exception.LibrarianNotFoundException;
import org.example.library.exception.LibraryOperationException;
import org.example.library.exception.LoanReaderNotFoundException;
import org.example.library.exception.ReaderNotFoundException;
import org.example.library.model.Book;
import org.example.library.model.Librarian;
import org.example.library.model.Loan;
import org.example.library.model.Reader;
import org.example.library.service.Library;
import org.example.library.service.Logic;

public class MainFrame extends JFrame {

    private DefaultTableModel booksModel;
    private DefaultTableModel readersModel;
    private DefaultTableModel librariansModel;
    private DefaultTableModel loansModel;

    private JTable booksTable;
    private JTable readersTable;
    private JTable librariansTable;
    private JTable loansTable;

    public MainFrame() {
        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 600);
        setLocationRelativeTo(null);

        setJMenuBar(buildMenuBar());
        add(buildToolBar(), BorderLayout.NORTH);
        add(buildTabbedPane(), BorderLayout.CENTER);

        Library.loadLibrary();
        refreshAll();
    }

    // ── Menu ─────────────────────────────────────────────────────────────────

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");

        JMenuItem save = new JMenuItem("Save Data");
        save.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        save.addActionListener(e -> { Library.saveLibrary(); showInfo("Data saved."); });

        JMenuItem load = new JMenuItem("Load Data");
        load.setAccelerator(KeyStroke.getKeyStroke("ctrl L"));
        load.addActionListener(e -> { Library.loadLibrary(); refreshAll(); showInfo("Data loaded."); });

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));

        file.add(save);
        file.add(load);
        file.addSeparator();
        file.add(exit);
        bar.add(file);
        return bar;
    }

    // ── Toolbar ───────────────────────────────────────────────────────────────

    private JToolBar buildToolBar() {
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);

        JButton saveBtn = new JButton("💾 Save");
        JButton loadBtn = new JButton("📂 Load");

        saveBtn.setToolTipText("Save all data to data/library.json  (Ctrl+S)");
        loadBtn.setToolTipText("Load data from data/library.json  (Ctrl+L)");

        saveBtn.addActionListener(e -> { Library.saveLibrary(); showInfo("Saved to data/library.json"); });
        loadBtn.addActionListener(e -> { Library.loadLibrary(); refreshAll(); showInfo("Loaded from data/library.json"); });

        bar.add(saveBtn);
        bar.add(loadBtn);
        return bar;
    }

    // ── Tabs ──────────────────────────────────────────────────────────────────

    private JTabbedPane buildTabbedPane() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Books", buildBooksPanel());
        tabs.addTab("Readers", buildReadersPanel());
        tabs.addTab("Librarians", buildLibrariansPanel());
        tabs.addTab("Loans", buildLoansPanel());
        return tabs;
    }

    // ── Books panel ───────────────────────────────────────────────────────────

    private JPanel buildBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField searchField = new JTextField();
        JButton searchBtn = new JButton("Search");
        JButton clearBtn  = new JButton("Clear");

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        JPanel searchBtns  = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        searchBtns.add(searchBtn);
        searchBtns.add(clearBtn);
        searchPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtns,  BorderLayout.EAST);

        booksModel = nonEditable("ISBN", "Title", "Author", "Year", "Genre", "Available");
        booksTable = new JTable(booksModel);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton addBtn    = new JButton("Add Book");
        JButton removeBtn = new JButton("Remove Book");
        addBtn.addActionListener(e -> addBook());
        removeBtn.addActionListener(e -> removeBook());
        searchBtn.addActionListener(e -> searchBooks(searchField.getText()));
        clearBtn.addActionListener(e -> { searchField.setText(""); refreshBooks(); });

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(booksTable), BorderLayout.CENTER);
        panel.add(buttonBar(addBtn, removeBtn), BorderLayout.SOUTH);
        return panel;
    }

    // ── Readers panel ─────────────────────────────────────────────────────────

    private JPanel buildReadersPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        readersModel = nonEditable("ID", "Name", "Email", "Borrowed books");
        readersTable = new JTable(readersModel);
        readersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton addBtn    = new JButton("Add Reader");
        JButton removeBtn = new JButton("Remove Reader");
        addBtn.addActionListener(e -> addReader());
        removeBtn.addActionListener(e -> removeReader());

        panel.add(new JScrollPane(readersTable), BorderLayout.CENTER);
        panel.add(buttonBar(addBtn, removeBtn), BorderLayout.SOUTH);
        return panel;
    }

    // ── Librarians panel ──────────────────────────────────────────────────────

    private JPanel buildLibrariansPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        librariansModel = nonEditable("ID", "Name", "Email", "Employee ID");
        librariansTable = new JTable(librariansModel);
        librariansTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton addBtn    = new JButton("Add Librarian");
        JButton removeBtn = new JButton("Remove Librarian");
        addBtn.addActionListener(e -> addLibrarian());
        removeBtn.addActionListener(e -> removeLibrarian());

        panel.add(new JScrollPane(librariansTable), BorderLayout.CENTER);
        panel.add(buttonBar(addBtn, removeBtn), BorderLayout.SOUTH);
        return panel;
    }

    // ── Loans panel ───────────────────────────────────────────────────────────

    private JPanel buildLoansPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        loansModel = nonEditable("Loan ID", "Book ISBN", "Reader ID", "Librarian ID",
                                 "Borrow Date", "Return Date", "Status");
        loansTable = new JTable(loansModel);
        loansTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton borrowBtn = new JButton("Borrow Book");
        JButton returnBtn = new JButton("Return Book");
        borrowBtn.addActionListener(e -> borrowBook());
        returnBtn.addActionListener(e -> returnBook());

        panel.add(new JScrollPane(loansTable), BorderLayout.CENTER);
        panel.add(buttonBar(borrowBtn, returnBtn), BorderLayout.SOUTH);
        return panel;
    }

    // ── Refresh ───────────────────────────────────────────────────────────────

    private void refreshAll() {
        refreshBooks();
        refreshReaders();
        refreshLibrarians();
        refreshLoans();
    }

    private void refreshBooks() {
        booksModel.setRowCount(0);
        for (Book b : Library.getBooks()) {
            booksModel.addRow(new Object[]{
                b.getIsbn(), b.getTitle(), b.getAuthor(),
                b.getYear(), b.getGenre(),
                b.isAvailable() ? "Yes" : "No"
            });
        }
    }

    private void refreshReaders() {
        readersModel.setRowCount(0);
        for (Reader r : Library.getReaders()) {
            readersModel.addRow(new Object[]{
                r.getId(), r.getName(), r.getEmail(),
                r.getBorrowedBookIsbns().size()
            });
        }
    }

    private void refreshLibrarians() {
        librariansModel.setRowCount(0);
        for (Librarian l : Library.getLibrarians()) {
            librariansModel.addRow(new Object[]{
                l.getId(), l.getName(), l.getEmail(), l.getEmployeeId()
            });
        }
    }

    private void refreshLoans() {
        loansModel.setRowCount(0);
        for (Loan l : Library.getLoans()) {
            loansModel.addRow(new Object[]{
                l.getLoanId(), l.getBookIsbn(), l.getReaderId(), l.getLibrarianId(),
                l.getBorrowDate(),
                l.getReturnDate() != null ? l.getReturnDate() : "—",
                l.isActive() ? "Active" : "Returned"
            });
        }
    }

    // ── Book actions ──────────────────────────────────────────────────────────

    private void addBook() {
        JTextField isbn   = new JTextField();
        JTextField title  = new JTextField();
        JTextField author = new JTextField();
        JTextField year   = new JTextField();
        JTextField genre  = new JTextField();

        JPanel form = buildForm(
            "ISBN",   isbn,
            "Title",  title,
            "Author", author,
            "Year",   year,
            "Genre",  genre
        );
        if (confirm(form, "Add Book") != JOptionPane.OK_OPTION) return;

        if (isbn.getText().isBlank() || title.getText().isBlank()) {
            showError("ISBN and Title are required.");
            return;
        }
        String isbnVal = isbn.getText().trim();
        boolean isbnExists = Library.getBooks().stream()
                .anyMatch(b -> isbnVal.equals(b.getIsbn()));
        if (isbnExists) { showError("Book with ISBN \"" + isbnVal + "\" already exists."); return; }

        Integer y = Logic.parseYear(year.getText().trim());
        if (y == null) { showError("Year must be a number."); return; }

        Library.addBook(new Book(
            isbn.getText().trim(), title.getText().trim(),
            author.getText().trim(), y, genre.getText().trim()
        ));
        refreshBooks();
    }

    private void removeBook() {
        int row = booksTable.getSelectedRow();
        if (row < 0) { showError("Select a book to remove."); return; }
        Library.removeBook((String) booksModel.getValueAt(row, 0));
        refreshBooks();
        refreshLoans();
    }

    private void searchBooks(String query) {
        if (query == null || query.isBlank()) { refreshBooks(); return; }
        booksModel.setRowCount(0);
        for (Book b : Library.searchBooks(query)) {
            booksModel.addRow(new Object[]{
                b.getIsbn(), b.getTitle(), b.getAuthor(),
                b.getYear(), b.getGenre(),
                b.isAvailable() ? "Yes" : "No"
            });
        }
    }

    // ── Reader actions ────────────────────────────────────────────────────────

    private void addReader() {
        JTextField id    = new JTextField();
        JTextField name  = new JTextField();
        JTextField email = new JTextField();

        JPanel form = buildForm("ID", id, "Name", name, "Email", email);
        if (confirm(form, "Add Reader") != JOptionPane.OK_OPTION) return;

        if (id.getText().isBlank()) { showError("ID is required."); return; }
        String readerIdVal = id.getText().trim();
        boolean readerExists = Library.getReaders().stream()
                .anyMatch(r -> readerIdVal.equals(r.getId()));
        if (readerExists) { showError("Reader with ID \"" + readerIdVal + "\" already exists."); return; }

        Library.addReader(new Reader(
            id.getText().trim(), name.getText().trim(), email.getText().trim()
        ));
        refreshReaders();
    }

    private void removeReader() {
        int row = readersTable.getSelectedRow();
        if (row < 0) { showError("Select a reader to remove."); return; }
        Library.removeReader((String) readersModel.getValueAt(row, 0));
        refreshReaders();
    }

    // ── Librarian actions ─────────────────────────────────────────────────────

    private void addLibrarian() {
        JTextField id    = new JTextField();
        JTextField name  = new JTextField();
        JTextField email = new JTextField();
        JTextField empId = new JTextField();

        JPanel form = buildForm("ID", id, "Name", name, "Email", email, "Employee ID", empId);
        if (confirm(form, "Add Librarian") != JOptionPane.OK_OPTION) return;

        if (id.getText().isBlank()) { showError("ID is required."); return; }
        String libIdVal = id.getText().trim();
        boolean libExists = Library.getLibrarians().stream()
                .anyMatch(l -> libIdVal.equals(l.getId()));
        if (libExists) { showError("Librarian with ID \"" + libIdVal + "\" already exists."); return; }

        Library.addLibrarian(new Librarian(
            id.getText().trim(), name.getText().trim(),
            email.getText().trim(), empId.getText().trim()
        ));
        refreshLibrarians();
    }

    private void removeLibrarian() {
        int row = librariansTable.getSelectedRow();
        if (row < 0) { showError("Select a librarian to remove."); return; }
        Library.removeLibrarian((String) librariansModel.getValueAt(row, 0));
        refreshLibrarians();
    }

    // ── Loan actions ──────────────────────────────────────────────────────────

    private void borrowBook() {
        JTextField isbn        = new JTextField();
        JTextField readerId    = new JTextField();
        JTextField librarianId = new JTextField();

        JPanel form = buildForm(
            "Book ISBN",    isbn,
            "Reader ID",    readerId,
            "Librarian ID", librarianId
        );
        if (confirm(form, "Borrow Book") != JOptionPane.OK_OPTION) return;

        try {
            Logic.borrowBook(
                isbn.getText().trim(),
                readerId.getText().trim(),
                librarianId.getText().trim()
            );
            refreshBooks();
            refreshReaders();
            refreshLoans();
        } catch (LibraryOperationException e) {
            showError(mapError(e));
        }
    }

    private void returnBook() {
        int row = loansTable.getSelectedRow();
        String isbn;
        if (row >= 0) {
            isbn = (String) loansModel.getValueAt(row, 1);
        } else {
            isbn = JOptionPane.showInputDialog(this, "Enter Book ISBN to return:");
            if (isbn == null || isbn.isBlank()) return;
            isbn = isbn.trim();
        }
        try {
            Logic.returnBook(isbn);
            refreshBooks();
            refreshReaders();
            refreshLoans();
        } catch (LibraryOperationException e) {
            showError(mapError(e));
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private DefaultTableModel nonEditable(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
    }

    private JPanel buttonBar(JButton... buttons) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        for (JButton b : buttons) bar.add(b);
        return bar;
    }

    private JPanel buildForm(Object... pairs) {
        int rows = pairs.length / 2;
        JPanel form = new JPanel(new GridLayout(rows, 2, 8, 6));
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        for (int i = 0; i < pairs.length; i += 2) {
            form.add(new JLabel(pairs[i] + ":"));
            JTextField field = (JTextField) pairs[i + 1];
            field.setPreferredSize(new Dimension(200, 26));
            form.add(field);
        }
        return form;
    }

    private int confirm(Component content, String title) {
        return JOptionPane.showConfirmDialog(this, content, title, JOptionPane.OK_CANCEL_OPTION);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private String mapError(LibraryOperationException e) {
        if (e instanceof BookNotFoundException)       return "Book not found.";
        if (e instanceof BookUnavailableException)    return "Book is not available for borrowing.";
        if (e instanceof ReaderNotFoundException)     return "Reader not found.";
        if (e instanceof LibrarianNotFoundException)  return "Librarian not found.";
        if (e instanceof BookAlreadyAvailableException) return "Book is already available.";
        if (e instanceof ActiveLoanNotFoundException) return "No active loan for this book.";
        if (e instanceof LoanReaderNotFoundException) return "Reader for this loan not found.";
        if (e instanceof BorrowedBookStateException)  return "Borrowed books state is inconsistent.";
        return "Operation failed.";
    }
}
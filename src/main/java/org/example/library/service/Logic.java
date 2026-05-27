package org.example.library.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import org.example.library.model.Book;
import org.example.library.model.Loan;
import org.example.library.model.Reader;

public class Logic {
    private static final String EOF_SIGNAL = "__EOF__";

    public static void logicOut(String text) {
        // TODO: output

        System.out.println(text);
    }

    public static String logicIn(String text) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();
            return input == null ? EOF_SIGNAL : input;
        } catch (IOException e) {
            logicOut("Failed to read input.");
            return EOF_SIGNAL;
        }
    }

    public static boolean handleMenuInput(String input) {
        String normalizedInput = normalizeInput(input);
        if (shouldExitInput(normalizedInput)) {
            return false;
        }

        try {
            int option = Integer.parseInt(normalizedInput);
            if (option < 0 || option > 11) {
                logicOut("Invalid option. Please enter a number from 0 to 11.");
                return true;
            }
            startOption(option);
        } catch (NumberFormatException e) {
            logicOut("Invalid option. Please enter a number from 0 to 11.");
        }

        return true;
    }

    public static void startOption(int option) {
        /*
        1 - add book
        2 - remove book
        3 - view all books
        4 - register reader
        5 - view all readers
        6 - search books
        7 - borrow book
        8 - return book
        9 - check availability
        10 - save data
        11 - load data            
        */
        switch (option) {
            case 1:
                addBook();
                break;
            case 2:
                removeBook();
                break;
            case 3:
                viewAllBooks();
                break;
            case 4:
                registerReader();
                break;
            case 5:
                viewAllReaders();
                break;
            case 6:
                searchBooks();
                break;
            case 7:
                borrowBook();
                break;
            case 8:
                returnBook();
                break;
            case 9:
                checkAvailability();
                break;
            case 10:
                saveData();
                break;
            case 11:
                loadData();
                break;

            default:
                throw new IllegalArgumentException("Invalid option: " + option);
        }
    }

    public static void addBook() {
        Book book = new Book();
        logicOut("Enter book ISBN: ");
        String isbn = normalizeInput(logicIn(""));
        if (shouldExitInput(isbn)) {
            logicOut("Input closed. Returning to menu.");
            return;
        }
        book.setIsbn(isbn);
        logicOut("Enter book title: ");
        String title = normalizeInput(logicIn(""));
        if (shouldExitInput(title)) {
            logicOut("Input closed. Returning to menu.");
            return;
        }
        book.setTitle(title);
        logicOut("Enter book author: ");
        String author = normalizeInput(logicIn(""));
        if (shouldExitInput(author)) {
            logicOut("Input closed. Returning to menu.");
            return;
        }
        book.setAuthor(author);
        Integer year = promptForBookYear();
        if (year == null) {
            logicOut("Input closed. Returning to menu.");
            return;
        }
        book.setYear(year);
        logicOut("Enter book genre: ");
        String genre = normalizeInput(logicIn(""));
        if (shouldExitInput(genre)) {
            logicOut("Input closed. Returning to menu.");
            return;
        }
        book.setGenre(genre);
        Library.addBook(book);
        logicOut("Book added successfully.");
    }

    public static void removeBook() {
        logicOut("Enter book ISBN to remove: ");
        String isbn = logicIn("");
        if (Library.removeBook(isbn)) {
            logicOut("Book removed successfully.");
        } else {
            logicOut("Book with the given ISBN not found.");
        }
    }

    public static void viewAllBooks() {
        List<Book> books = Library.getBooks();
        if (books.isEmpty()) {
            logicOut("No books in the library.");
        } else {
            for (Book book : books) {
                logicOut(String.format("%s - %s by %s (%d) [%s] - %s",
                        book.getIsbn(), book.getTitle(), book.getAuthor(),
                        book.getYear(), book.getGenre(),
                        book.isAvailable() ? "Available" : "Not Available"));
            }
        }
    }

    public static void registerReader() {
        Reader reader = new Reader();
        logicOut("Enter reader ID: ");
        reader.setId(logicIn(""));
        logicOut("Enter reader name: ");
        reader.setName(logicIn(""));
        logicOut("Enter reader email: ");
        reader.setEmail(logicIn(""));
        Library.addReader(reader);
        logicOut("Reader registered successfully.");
    }

    public static void viewAllReaders() {
        List<Reader> readers = Library.getReaders();
        if (readers.isEmpty()) {
            logicOut("No readers registered.");
        } else {
            for (Reader reader : readers) {
                logicOut(String.format("%s - %s (%s)",
                        reader.getId(), reader.getName(), reader.getEmail()));
            }
        }
    }

    public static void searchBooks() {
        logicOut("Enter search query (title, author, or genre): ");
        String rawQuery = normalizeInput(logicIn(""));
        if (shouldExitInput(rawQuery)) {
            logicOut("Input closed. Returning to menu.");
            return;
        }
        String query = rawQuery.toLowerCase();
        List<Book> books = Library.getBooks();
        boolean found = false;
        for (Book book : books) {
            String title = Objects.toString(book.getTitle(), "").toLowerCase();
            String author = Objects.toString(book.getAuthor(), "").toLowerCase();
            String genre = Objects.toString(book.getGenre(), "").toLowerCase();
            if (title.contains(query) ||
                author.contains(query) ||
                genre.contains(query)) {
                logicOut(String.format("%s - %s by %s (%d) [%s] - %s",
                        book.getIsbn(), book.getTitle(), book.getAuthor(),
                        book.getYear(), book.getGenre(),
                        book.isAvailable() ? "Available" : "Not Available"));
                found = true;
            }
        }
        if (!found) {
            logicOut("No books found matching the query.");
        }
    }

    public static void borrowBook() {
        logicOut("Enter the isbn of the book to borrow: ");
        String isbn = normalizeInput(logicIn(""));
        if (shouldExitInput(isbn)) {
            logicOut("Input closed. Returning to menu.");
            return;
        }
        logicOut("Enter reader ID: ");
        String readerId = normalizeInput(logicIn(""));
        if (shouldExitInput(readerId)) {
            logicOut("Input closed. Returning to menu.");
            return;
        }
        borrowBook(isbn, readerId);
    }

    public static boolean borrowBook(String isbn, String readerId) {
        Book bookToBorrow = null;
        for (Book book : Library.getBooks()) {
            if (book.getIsbn().equals(isbn)) {
                bookToBorrow = book;
                break;
            }
        }
        if (bookToBorrow == null) {
            logicOut("Book with the given ISBN not found.");
            return false;
        }
        if (!bookToBorrow.isAvailable()) { 
            logicOut("Book is currently not available for borrowing.");
            return false;
        }

        Reader readerToBorrow = null;
        for (Reader reader : Library.getReaders()) {
            if (reader.getId().equals(readerId)) {
                readerToBorrow = reader;
                break;
            }
        }
        if (readerToBorrow == null) {
            logicOut("Reader with the given ID not found.");
            return false;
        }

        Loan loan = new Loan(isbn, readerId);
        Library.addLoan(loan);
        bookToBorrow.setAvailable(false);
        readerToBorrow.addBorrowedBook(isbn);
        logicOut("Book borrowed successfully.");
        return true;
    }

    public static void returnBook() {
        logicOut("Enter the isbn of the book to return: ");
        String isbn = normalizeInput(logicIn(""));
        if (shouldExitInput(isbn)) {
            logicOut("Input closed. Returning to menu.");
            return;
        }
        returnBook(isbn);
    }

    public static boolean returnBook(String isbn) {
        Book bookToReturn = null;
        for (Book book : Library.getBooks()) {
            if (book.getIsbn().equals(isbn)) {
                bookToReturn = book;
                break;
            }
        }
        if (bookToReturn == null) {
            logicOut("Book with the given ISBN not found.");
            return false;
        }
        if (bookToReturn.isAvailable()) {
            logicOut("Book is already marked as available.");
            return false;
        }

        Loan activeLoan = null;
        for (Loan loan : Library.getLoans()) {
            if (loan.getBookIsbn().equals(isbn) && loan.isActive()) {
                activeLoan = loan;
                break;
            }
        }
        if (activeLoan == null) {
            logicOut("No active loan found for this book.");
            return false;
        }

        Reader activeReader = null;
        for (Reader reader : Library.getReaders()) {
            if (reader.getId().equals(activeLoan.getReaderId())) {
                activeReader = reader;
                break;
            }
        }
        if (activeReader == null) {
            logicOut("Reader associated with the active loan not found.");
            return false;
        }
        if (!activeReader.hasBorrowedBook(isbn)) {
            logicOut("Reader does not have this book marked as borrowed.");
            return false;
        }

        activeReader.removeBorrowedBook(isbn);
        activeLoan.markReturned();
        bookToReturn.setAvailable(true);
        logicOut("Book returned successfully.");
        return true;
    }

    public static void checkAvailability() {
        logicOut("Enter the isbn of the book to check availability: ");
        String isbn = normalizeInput(logicIn(""));
        if (shouldExitInput(isbn)) {
            logicOut("Input closed. Returning to menu.");
            return;
        }
        Book bookToCheck = null;
        for (Book book : Library.getBooks()) {
            if (book.getIsbn().equals(isbn)) {
                bookToCheck = book;
                break;
            }
        }
        if(bookToCheck == null) {
            logicOut("Book with the given ISBN not found.");
            return;
        }
        logicOut(String.format("Book '%s' is %s.",
                bookToCheck.getTitle(),
                bookToCheck.isAvailable() ? "available" : "not available"));
    }

    static Integer parseYear(String input) {
        try {
            return Integer.parseInt(normalizeInput(input));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Integer promptForBookYear() {
        while (true) {
            logicOut("Enter book year: ");
            String yearInput = normalizeInput(logicIn(""));
            if (shouldExitInput(yearInput)) {
                return null;
            }

            Integer parsedYear = parseYear(yearInput);
            if (parsedYear != null) {
                return parsedYear;
            }

            logicOut("Invalid year. Please enter a number.");
        }
    }

    private static String normalizeInput(String input) {
        return input == null ? EOF_SIGNAL : input.trim();
    }

    private static boolean shouldExitInput(String input) {
        return EOF_SIGNAL.equals(input) || "0".equals(input);
    }

    public static void saveData() {
        Library.saveLibrary();
    }

    public static void loadData() {
        Library.loadLibrary();
    }
}

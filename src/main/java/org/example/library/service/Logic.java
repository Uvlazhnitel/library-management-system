package org.example.library.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
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
            if (option < 0 || option > 15) {
                logicOut("Invalid option. Please enter a number from 0 to 15.");
                return true;
            }
            startOption(option);
        } catch (NumberFormatException e) {
            logicOut("Invalid option. Please enter a number from 0 to 15.");
        }

        return true;
    }

    public static void startOption(int option) {
        /*
        1  - add book
        2  - remove book
        3  - view all books
        4  - register reader
        5  - remove reader
        6  - view all readers
        7  - register librarian
        8  - remove librarian
        9  - view all librarians
        10 - search books
        11 - borrow book
        12 - return book
        13 - check availability
        14 - save data
        15 - load data
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
                removeReader();
                break;
            case 6:
                viewAllReaders();
                break;
            case 7:
                registerLibrarian();
                break;
            case 8:
                removeLibrarian();
                break;
            case 9:
                viewAllLibrarians();
                break;
            case 10:
                searchBooks();
                break;
            case 11:
                borrowBook();
                break;
            case 12:
                returnBook();
                break;
            case 13:
                checkAvailability();
                break;
            case 14:
                saveData();
                break;
            case 15:
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

    public static void removeReader() {
        logicOut("Enter reader ID to remove: ");
        String id = normalizeInput(logicIn(""));
        if (shouldExitInput(id)) {
            logicOut("Input closed. Returning to menu.");
            return;
        }
        boolean found = Library.getReaders().stream().anyMatch(r -> Objects.equals(r.getId(), id));
        if (!found) {
            logicOut("Reader with the given ID not found.");
            return;
        }
        Library.removeReader(id);
        logicOut("Reader removed successfully.");
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

    public static void registerLibrarian() {
        Librarian librarian = new Librarian();
        logicOut("Enter librarian ID: ");
        String id = normalizeInput(logicIn(""));
        if (shouldExitInput(id)) {
            logicOut("Input closed. Returning to menu.");
            return;
        }
        librarian.setId(id);
        logicOut("Enter librarian name: ");
        String name = normalizeInput(logicIn(""));
        if (shouldExitInput(name)) {
            logicOut("Input closed. Returning to menu.");
            return;
        }
        librarian.setName(name);
        logicOut("Enter librarian email: ");
        String email = normalizeInput(logicIn(""));
        if (shouldExitInput(email)) {
            logicOut("Input closed. Returning to menu.");
            return;
        }
        librarian.setEmail(email);
        logicOut("Enter employee ID: ");
        String employeeId = normalizeInput(logicIn(""));
        if (shouldExitInput(employeeId)) {
            logicOut("Input closed. Returning to menu.");
            return;
        }
        librarian.setEmployeeId(employeeId);
        Library.addLibrarian(librarian);
        logicOut("Librarian registered successfully.");
    }

    public static void removeLibrarian() {
        logicOut("Enter librarian ID to remove: ");
        String id = normalizeInput(logicIn(""));
        if (shouldExitInput(id)) {
            logicOut("Input closed. Returning to menu.");
            return;
        }
        boolean found = Library.getLibrarians().stream().anyMatch(l -> Objects.equals(l.getId(), id));
        if (!found) {
            logicOut("Librarian with the given ID not found.");
            return;
        }
        Library.removeLibrarian(id);
        logicOut("Librarian removed successfully.");
    }

    public static void viewAllLibrarians() {
        List<Librarian> librarians = Library.getLibrarians();
        if (librarians.isEmpty()) {
            logicOut("No librarians registered.");
        } else {
            for (Librarian librarian : librarians) {
                logicOut(String.format("%s - %s (%s) [emp: %s]",
                        librarian.getId(), librarian.getName(),
                        librarian.getEmail(), librarian.getEmployeeId()));
            }
        }
    }

    public static void searchBooks() {
        logicOut("Enter search query (ISBN, title, author, or genre): ");
        String rawQuery = normalizeInput(logicIn(""));
        if (shouldExitInput(rawQuery)) {
            logicOut("Input closed. Returning to menu.");
            return;
        }

        List<Book> matches = Library.searchBooks(rawQuery);
        if (matches.isEmpty()) {
            logicOut("No books found matching the query.");
            return;
        }

        for (Book book : matches) {
            logicOut(String.format("%s - %s by %s (%d) [%s] - %s",
                    book.getIsbn(), book.getTitle(), book.getAuthor(),
                    book.getYear(), book.getGenre(),
                    book.isAvailable() ? "Available" : "Not Available"));
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
        logicOut("Enter librarian ID: ");
        String librarianId = normalizeInput(logicIn(""));
        if (shouldExitInput(librarianId)) {
            logicOut("Input closed. Returning to menu.");
            return;
        }
        try {
            borrowBook(isbn, readerId, librarianId);
        } catch (LibraryOperationException e) {
            logicOut(mapLibraryOperationMessage(e));
        }
    }

    public static void borrowBook(String isbn, String readerId, String librarianId) {
        Book bookToBorrow = findBookByIsbnOrThrow(isbn);
        if (!bookToBorrow.isAvailable()) {
            throw new BookUnavailableException();
        }

        Reader readerToBorrow = findReaderByIdOrThrow(readerId);
        findLibrarianByIdOrThrow(librarianId);

        Loan loan = new Loan(isbn, readerId, librarianId);
        Library.addLoan(loan);
        bookToBorrow.setAvailable(false);
        readerToBorrow.addBorrowedBook(isbn);
        logicOut("Book borrowed successfully.");
    }

    public static void returnBook() {
        logicOut("Enter the isbn of the book to return: ");
        String isbn = normalizeInput(logicIn(""));
        if (shouldExitInput(isbn)) {
            logicOut("Input closed. Returning to menu.");
            return;
        }
        try {
            returnBook(isbn);
        } catch (LibraryOperationException e) {
            logicOut(mapLibraryOperationMessage(e));
        }
    }

    public static void returnBook(String isbn) {
        Book bookToReturn = findBookByIsbnOrThrow(isbn);
        if (bookToReturn.isAvailable()) {
            throw new BookAlreadyAvailableException();
        }

        Loan activeLoan = findActiveLoanByBookIsbnOrThrow(isbn);
        Reader activeReader = findLoanReaderByIdOrThrow(activeLoan.getReaderId());
        if (!activeReader.hasBorrowedBook(isbn)) {
            throw new BorrowedBookStateException();
        }

        activeReader.removeBorrowedBook(isbn);
        activeLoan.markReturned();
        bookToReturn.setAvailable(true);
        logicOut("Book returned successfully.");
    }

    public static void checkAvailability() {
        logicOut("Enter the isbn of the book to check availability: ");
        String isbn = normalizeInput(logicIn(""));
        if (shouldExitInput(isbn)) {
            logicOut("Input closed. Returning to menu.");
            return;
        }
        try {
            Book bookToCheck = findBookByIsbnOrThrow(isbn);
            logicOut(String.format("Book '%s' is %s.",
                    bookToCheck.getTitle(),
                    bookToCheck.isAvailable() ? "available" : "not available"));
        } catch (LibraryOperationException e) {
            logicOut(mapLibraryOperationMessage(e));
        }
    }

    public static Integer parseYear(String input) {
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

    private static Book findBookByIsbnOrThrow(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            throw new BookNotFoundException();
        }

        for (Book book : Library.getBooks()) {
            if (book.getIsbn() == null || book.getIsbn().isBlank()) {
                continue;
            }
            if (book.getIsbn().equals(isbn)) {
                return book;
            }
        }

        throw new BookNotFoundException();
    }

    private static Reader findReaderByIdOrThrow(String readerId) {
        if (readerId == null || readerId.isBlank()) {
            throw new ReaderNotFoundException();
        }

        for (Reader reader : Library.getReaders()) {
            if (reader.getId() == null || reader.getId().isBlank()) {
                continue;
            }
            if (reader.getId().equals(readerId)) {
                return reader;
            }
        }

        throw new ReaderNotFoundException();
    }

    private static Librarian findLibrarianByIdOrThrow(String librarianId) {
        if (librarianId == null || librarianId.isBlank()) {
            throw new LibrarianNotFoundException();
        }

        for (Librarian librarian : Library.getLibrarians()) {
            if (librarian.getId() == null || librarian.getId().isBlank()) {
                continue;
            }
            if (librarian.getId().equals(librarianId)) {
                return librarian;
            }
        }

        throw new LibrarianNotFoundException();
    }

    private static Loan findActiveLoanByBookIsbnOrThrow(String isbn) {
        for (Loan loan : Library.getLoans()) {
            if (Objects.equals(loan.getBookIsbn(), isbn) && loan.isActive()) {
                return loan;
            }
        }

        throw new ActiveLoanNotFoundException();
    }

    private static Reader findLoanReaderByIdOrThrow(String readerId) {
        if (readerId == null || readerId.isBlank()) {
            throw new LoanReaderNotFoundException();
        }

        for (Reader reader : Library.getReaders()) {
            if (reader.getId() == null || reader.getId().isBlank()) {
                continue;
            }
            if (reader.getId().equals(readerId)) {
                return reader;
            }
        }

        throw new LoanReaderNotFoundException();
    }

    private static String mapLibraryOperationMessage(LibraryOperationException e) {
        if (e instanceof BookNotFoundException) {
            return "Book with the given ISBN not found.";
        }
        if (e instanceof BookUnavailableException) {
            return "Book is currently not available for borrowing.";
        }
        if (e instanceof ReaderNotFoundException) {
            return "Reader with the given ID not found.";
        }
        if (e instanceof LibrarianNotFoundException) {
            return "Librarian with the given ID not found.";
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

    public static void saveData() {
        Library.saveLibrary();
    }

    public static void loadData() {
        Library.loadLibrary();
    }
}
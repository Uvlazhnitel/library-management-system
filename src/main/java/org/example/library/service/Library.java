package org.example.library.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.example.library.exception.DuplicateIsbnException;
import org.example.library.exception.DuplicateLibrarianIdException;
import org.example.library.exception.DuplicateReaderIdException;
import org.example.library.model.Book;
import org.example.library.model.Librarian;
import org.example.library.model.Loan;
import org.example.library.model.Reader;
import org.example.library.storage.JsonFileStorage;
import org.example.library.storage.Storage;

public class Library {
    private static ArrayList<Book> books = new ArrayList<>();
    private static ArrayList<Reader> readers = new ArrayList<>();
    private static ArrayList<Librarian> librarians = new ArrayList<>();
    private static ArrayList<Loan> loans = new ArrayList<>();

    public static void loadLibrary() {
        Storage storage = new JsonFileStorage();
        storage.load();
    }

    public static void saveLibrary() {
        Storage storage = new JsonFileStorage();
        storage.save();
    }

    public static List<Book> getBooks() {
        return books;
    }

    public static void setBooks(ArrayList<Book> books) {
        Library.books = books;
    }

    public static void addBook(Book book) {
        boolean duplicate = books.stream()
                .anyMatch(b -> Objects.equals(b.getIsbn(), book.getIsbn()));
        if (duplicate) throw new DuplicateIsbnException();
        books.add(book);
    }

    public static boolean removeBook(String isbn) {
        return books.removeIf(book -> Objects.equals(book.getIsbn(), isbn));
    }

    public static List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String normalizedKeyword = keyword.trim().toLowerCase(Locale.ROOT);
        List<Book> matches = new ArrayList<>();
        for (Book book : books) {
            String isbn = Objects.toString(book.getIsbn(), "").toLowerCase(Locale.ROOT);
            String title = Objects.toString(book.getTitle(), "").toLowerCase(Locale.ROOT);
            String author = Objects.toString(book.getAuthor(), "").toLowerCase(Locale.ROOT);
            String genre = Objects.toString(book.getGenre(), "").toLowerCase(Locale.ROOT);

            if (isbn.contains(normalizedKeyword) ||
                title.contains(normalizedKeyword) ||
                author.contains(normalizedKeyword) ||
                genre.contains(normalizedKeyword)) {
                matches.add(book);
            }
        }

        return matches;
    }

    public static List<Reader> getReaders() {
        return readers;
    }

    public static void setReaders(ArrayList<Reader> readers) {
        Library.readers = readers;
    }

    public static void addReader(Reader reader) {
        boolean duplicate = readers.stream()
                .anyMatch(r -> Objects.equals(r.getId(), reader.getId()));
        if (duplicate) throw new DuplicateReaderIdException();
        readers.add(reader);
    }

    public static void removeReader(String readerId) {
        readers.removeIf(reader -> Objects.equals(reader.getId(), readerId));
    }

    public static List<Librarian> getLibrarians() {
        return librarians;
    }

    public static void setLibrarians(ArrayList<Librarian> librarians) {
        Library.librarians = librarians;
    }

    public static void addLibrarian(Librarian librarian) {
        boolean duplicate = librarians.stream()
                .anyMatch(l -> Objects.equals(l.getId(), librarian.getId()));
        if (duplicate) throw new DuplicateLibrarianIdException();
        librarians.add(librarian);
    }

    public static void removeLibrarian(String librarianId) {
        librarians.removeIf(librarian -> Objects.equals(librarian.getId(), librarianId));
    }

    public static List<Loan> getLoans() {
        return loans;
    }

    public static void setLoans(ArrayList<Loan> loans) {
        Library.loans = loans;
    }

    public static void addLoan(Loan loan) {
        loans.add(loan);
    }

    public static void removeLoan(String loanId) {
        loans.removeIf(loan -> loan.getLoanId().equals(loanId));
    }
}
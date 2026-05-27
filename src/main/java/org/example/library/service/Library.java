package org.example.library.service;

import java.util.Collections;
import java.util.List;
import org.example.library.model.Book;
import org.example.library.model.Reader;
import org.example.library.model.Librarian;
import org.example.library.model.Loan;

public class Library {
    private static List<Book> books = Collections.emptyList();
    private static List<Reader> readers = Collections.emptyList();
    private static List<Librarian> librarians = Collections.emptyList();
    private static List<Loan> loans = Collections.emptyList();

    public static void loadLibrary() {
        // TODO: load library data from storage file
    }

    public static void saveLibrary() {
        // TODO: save library data to storage file
    }

    public static List<Book> getBooks() {
        return books;
    }

    public static void setBooks(List<Book> books) {
        Library.books = books;
    }

    public static void addBook(Book book) {
        books.add(book);
    }

    public static void removeBook(String isbn) {
        books.removeIf(book -> book.getIsbn().equals(isbn));
    }

    public static List<Reader> getReaders() {
        return readers;
    }

    public static void setReaders(List<Reader> readers) {
        Library.readers = readers;
    }

    public static void addReader(Reader reader) {
        readers.add(reader);
    }

    public static void removeReader(String readerId) {
        readers.removeIf(reader -> reader.getId().equals(readerId));
    }

    public static List<Librarian> getLibrarians() {
        return librarians;
    }

    public static void setLibrarians(List<Librarian> librarians) {
        Library.librarians = librarians;
    }

    public static void addLibrarian(Librarian librarian) {
        librarians.add(librarian);
    }

    public static void removeLibrarian(String librarianId) {
        librarians.removeIf(librarian -> librarian.getId().equals(librarianId));
    }

    public static List<Loan> getLoans() {
        return loans;
    }

    public static void setLoans(List<Loan> loans) {
        Library.loans = loans;
    }

    public static void addLoan(Loan loan) {
        loans.add(loan);
    }
    
    public static void removeLoan(String loanId) {
        loans.removeIf(loan -> loan.getLoanId().equals(loanId));
    }
}

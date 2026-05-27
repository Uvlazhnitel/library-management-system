package org.example.library.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.example.library.model.Book;
import org.example.library.model.Librarian;
import org.example.library.model.Loan;
import org.example.library.model.Reader;

public class Library {
    private static ArrayList<Book> books = new ArrayList<>();
    private static ArrayList<Reader> readers = new ArrayList<>();
    private static ArrayList<Librarian> librarians = new ArrayList<>();
    private static ArrayList<Loan> loans = new ArrayList<>();

    public static void loadLibrary() {
        // TODO: load library data from storage file
    }

    public static void saveLibrary() {
        // TODO: save library data to storage file
    }

    public static List<Book> getBooks() {
        return books;
    }

    public static void setBooks(ArrayList<Book> books) {
        Library.books = books;
    }

    public static void addBook(Book book) {
        books.add(book);
    }

    public static boolean removeBook(String isbn) {
        return books.removeIf(book -> Objects.equals(book.getIsbn(), isbn));
    }

    public static List<Reader> getReaders() {
        return readers;
    }

    public static void setReaders(ArrayList<Reader> readers) {
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

    public static void setLibrarians(ArrayList<Librarian> librarians) {
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

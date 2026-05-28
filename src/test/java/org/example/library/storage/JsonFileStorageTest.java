package org.example.library.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import org.example.library.model.Book;
import org.example.library.model.Librarian;
import org.example.library.model.Loan;
import org.example.library.model.Reader;
import org.example.library.service.Library;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JsonFileStorageTest {

    @TempDir
    java.nio.file.Path tempDir;

    @BeforeEach
    void setUp() {
        Library.setBooks(new ArrayList<>());
        Library.setReaders(new ArrayList<>());
        Library.setLibrarians(new ArrayList<>());
        Library.setLoans(new ArrayList<>());
    }

    @Test
    void saveAndLoadRoundTripRestoresLibraryState() {
        Book book = new Book("111", "Test Book", "Author", 2020, "Fiction", false);
        Reader reader = new Reader("r1", "Reader", "reader@test.com");
        reader.addBorrowedBook("111");
        Librarian librarian = new Librarian("l1", "Lib", "lib@test.com", "emp-1");
        Loan loan = new Loan("loan-1", "111", "r1", "2026-05-28", null, true);

        Library.addBook(book);
        Library.addReader(reader);
        Library.addLibrarian(librarian);
        Library.addLoan(loan);

        JsonFileStorage storage = new JsonFileStorage(tempDir.resolve("library.json").toString());
        storage.save();

        Library.setBooks(new ArrayList<>());
        Library.setReaders(new ArrayList<>());
        Library.setLibrarians(new ArrayList<>());
        Library.setLoans(new ArrayList<>());

        storage.load();

        assertEquals(1, Library.getBooks().size());
        assertEquals("111", Library.getBooks().get(0).getIsbn());
        assertFalse(Library.getBooks().get(0).isAvailable());

        assertEquals(1, Library.getReaders().size());
        assertEquals("r1", Library.getReaders().get(0).getId());
        assertEquals(1, Library.getReaders().get(0).getBorrowedBookIsbns().size());

        assertEquals(1, Library.getLibrarians().size());
        assertEquals("emp-1", Library.getLibrarians().get(0).getEmployeeId());

        assertEquals(1, Library.getLoans().size());
        assertEquals("111", Library.getLoans().get(0).getBookIsbn());
        assertEquals("r1", Library.getLoans().get(0).getReaderId());
    }
}

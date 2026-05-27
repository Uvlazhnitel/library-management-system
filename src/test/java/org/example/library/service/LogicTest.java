package org.example.library.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import org.example.library.model.Book;
import org.example.library.model.Loan;
import org.example.library.model.Reader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LogicTest {
    private InputStream originalIn;

    @BeforeEach
    void setUp() {
        originalIn = System.in;
        Library.setBooks(new ArrayList<>());
        Library.setReaders(new ArrayList<>());
        Library.setLibrarians(new ArrayList<>());
        Library.setLoans(new ArrayList<>());
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setIn(originalIn);
    }

    @Test
    void borrowBookFindsBookBeyondFirstEntry() {
        Book firstBook = new Book("111", "First", "Author A", 2020, "Drama");
        Book targetBook = new Book("222", "Second", "Author B", 2021, "Sci-Fi");
        Reader reader = new Reader("r1", "Reader One", "reader@example.com");

        Library.addBook(firstBook);
        Library.addBook(targetBook);
        Library.addReader(reader);

        boolean result = Logic.borrowBook("222", "r1");

        assertTrue(result);
        assertFalse(targetBook.isAvailable());
        assertTrue(reader.hasBorrowedBook("222"));
        assertEquals(1, Library.getLoans().size());
    }

    @Test
    void borrowBookReturnsFalseWhenBookDoesNotExist() {
        Reader reader = new Reader("r1", "Reader One", "reader@example.com");
        Library.addBook(new Book("111", "First", "Author A", 2020, "Drama"));
        Library.addReader(reader);

        boolean result = Logic.borrowBook("999", "r1");

        assertFalse(result);
        assertTrue(Library.getLoans().isEmpty());
        assertFalse(reader.hasBorrowedBook("999"));
    }

    @Test
    void returnBookFindsBookBeyondFirstEntry() {
        Book firstBook = new Book("111", "First", "Author A", 2020, "Drama");
        Book targetBook = new Book("222", "Second", "Author B", 2021, "Sci-Fi", false);
        Reader reader = new Reader("r1", "Reader One", "reader@example.com");
        Loan loan = new Loan("loan-1", "222", "r1", "2026-05-27", null, true);
        reader.addBorrowedBook("222");

        Library.addBook(firstBook);
        Library.addBook(targetBook);
        Library.addReader(reader);
        Library.addLoan(loan);

        boolean result = Logic.returnBook("222");

        assertTrue(result);
        assertTrue(targetBook.isAvailable());
        assertFalse(reader.hasBorrowedBook("222"));
        assertFalse(loan.isActive());
    }

    @Test
    void removeBookReportsOperationResult() {
        Library.addBook(new Book("111", "First", "Author A", 2020, "Drama"));

        assertTrue(Library.removeBook("111"));
        assertFalse(Library.removeBook("missing"));
    }

    @Test
    void handleMenuInputDoesNotThrowForNonNumericInput() {
        assertDoesNotThrow(() -> assertTrue(Logic.handleMenuInput("abc")));
    }

    @Test
    void handleMenuInputTreatsEofSignalAsExit() {
        assertFalse(Logic.handleMenuInput("__EOF__"));
    }

    @Test
    void removeBookHandlesNullIsbnSafely() {
        Library.addBook(new Book());

        assertDoesNotThrow(() -> assertFalse(Library.removeBook("111")));
    }

    @Test
    void parseYearReturnsNullForInvalidInput() {
        assertNull(Logic.parseYear("abc"));
        assertEquals(2026, Logic.parseYear("2026"));
    }

    @Test
    void searchBooksHandlesNullFieldsWithoutThrowing() {
        Book incompleteBook = new Book();
        incompleteBook.setIsbn("111");
        Library.addBook(incompleteBook);
        System.setIn(new ByteArrayInputStream("test\n".getBytes()));

        assertDoesNotThrow(Logic::searchBooks);
    }
}

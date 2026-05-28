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

    @Test
    void shouldAddBook() {
        Book book = new Book("isbn123", "Test Book", "John Doe", 2024, "Fiction");
        Library.addBook(book);

        assertEquals(1, Library.getBooks().size());
        assertEquals("isbn123", Library.getBooks().get(0).getIsbn());
        assertEquals("Test Book", Library.getBooks().get(0).getTitle());
    }

    @Test
    void shouldRegisterReader() {
        Reader reader = new Reader("r1", "John Smith", "john@test.com");
        Library.addReader(reader);

        assertEquals(1, Library.getReaders().size());
        assertEquals("r1", Library.getReaders().get(0).getId());
        assertEquals("John Smith", Library.getReaders().get(0).getName());
    }

    @Test
    void shouldSearchBook() {
        Library.addBook(new Book("111", "Java Programming", "James Gosling", 2020, "Education"));
        Library.addBook(new Book("222", "Python Basics", "Guido", 2021, "Education"));
        System.setIn(new ByteArrayInputStream("java\n".getBytes()));

        assertDoesNotThrow(Logic::searchBooks);
    }

    @Test
    void shouldBorrowAvailableBook() {
        Book book = new Book("111", "Test Book", "Author", 2020, "Fiction");
        Reader reader = new Reader("r1", "John", "john@test.com");
        Library.addBook(book);
        Library.addReader(reader);

        boolean result = Logic.borrowBook("111", "r1");

        assertTrue(result);
        assertFalse(book.isAvailable());
        assertTrue(reader.hasBorrowedBook("111"));
        assertEquals(1, Library.getLoans().size());
    }

    @Test
    void shouldNotBorrowAlreadyBorrowedBook() {
        Book book = new Book("111", "Test Book", "Author", 2020, "Fiction", false);
        Reader reader = new Reader("r1", "John", "john@test.com");
        Library.addBook(book);
        Library.addReader(reader);

        boolean result = Logic.borrowBook("111", "r1");

        assertFalse(result);
        assertTrue(Library.getLoans().isEmpty());
    }

    @Test
    void shouldReturnBorrowedBook() {
        Book book = new Book("111", "Test Book", "Author", 2020, "Fiction", false);
        Reader reader = new Reader("r1", "John", "john@test.com");
        Loan loan = new Loan("loan-1", "111", "r1", "2026-05-27", null, true);
        reader.addBorrowedBook("111");
        Library.addBook(book);
        Library.addReader(reader);
        Library.addLoan(loan);

        boolean result = Logic.returnBook("111");

        assertTrue(result);
        assertTrue(book.isAvailable());
        assertFalse(reader.hasBorrowedBook("111"));
        assertFalse(loan.isActive());
    }

    @Test
    void shouldCheckBookAvailability() {
        Book book = new Book("111", "Test Book", "Author", 2020, "Fiction");
        Library.addBook(book);
        System.setIn(new ByteArrayInputStream("111\n".getBytes()));

        assertDoesNotThrow(Logic::checkAvailability);
        assertTrue(book.isAvailable());
    }

    @Test
    void librarySearchBooksFindsByTitle() {
        Book book = new Book("111", "Java Programming", "James Gosling", 2020, "Education");
        Library.addBook(book);

        var result = Library.searchBooks("Programming");

        assertEquals(1, result.size());
        assertEquals("111", result.get(0).getIsbn());
    }

    @Test
    void librarySearchBooksFindsByAuthor() {
        Book book = new Book("111", "Java Programming", "James Gosling", 2020, "Education");
        Library.addBook(book);

        var result = Library.searchBooks("gosling");

        assertEquals(1, result.size());
        assertEquals("111", result.get(0).getIsbn());
    }

    @Test
    void librarySearchBooksFindsByGenre() {
        Book book = new Book("111", "Java Programming", "James Gosling", 2020, "Education");
        Library.addBook(book);

        var result = Library.searchBooks("education");

        assertEquals(1, result.size());
        assertEquals("111", result.get(0).getIsbn());
    }

    @Test
    void librarySearchBooksFindsByIsbn() {
        Book book = new Book("ISBN-111", "Java Programming", "James Gosling", 2020, "Education");
        Library.addBook(book);

        var result = Library.searchBooks("111");

        assertEquals(1, result.size());
        assertEquals("ISBN-111", result.get(0).getIsbn());
    }

    @Test
    void librarySearchBooksIsCaseInsensitive() {
        Book book = new Book("111", "Java Programming", "James Gosling", 2020, "Education");
        Library.addBook(book);

        var result = Library.searchBooks("jAvA");

        assertEquals(1, result.size());
        assertEquals("111", result.get(0).getIsbn());
    }

    @Test
    void librarySearchBooksReturnsEmptyListForNullOrBlankKeyword() {
        Library.addBook(new Book("111", "Java Programming", "James Gosling", 2020, "Education"));

        assertTrue(Library.searchBooks(null).isEmpty());
        assertTrue(Library.searchBooks("   ").isEmpty());
    }

    @Test
    void librarySearchBooksHandlesNullBookFields() {
        Book incompleteBook = new Book();
        incompleteBook.setIsbn("111");
        Library.addBook(incompleteBook);

        assertDoesNotThrow(() -> {
            var result = Library.searchBooks("111");
            assertEquals(1, result.size());
        });
    }
}

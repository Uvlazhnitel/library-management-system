package org.example.library.model;

import java.time.LocalDate;
import java.util.UUID;

public class Loan {

    private String loanId;
    private String bookIsbn;
    private String readerId;
    private String borrowDate;
    private String returnDate;
    private boolean active;

    public Loan() {
    }

    public Loan(String bookIsbn, String readerId) {
        this.loanId = UUID.randomUUID().toString();
        this.bookIsbn = bookIsbn;
        this.readerId = readerId;
        this.borrowDate = LocalDate.now().toString();
        this.returnDate = null;
        this.active = true;
    }

    public Loan(String loanId, String bookIsbn, String readerId, String borrowDate, String returnDate, boolean active) {
        this.loanId = loanId;
        this.bookIsbn = bookIsbn;
        this.readerId = readerId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.active = active;
    }

    public void markReturned() {
        this.returnDate = LocalDate.now().toString();
        this.active = false;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public String getReaderId() {
        return readerId;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
        this.active = returnDate == null;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            this.returnDate = null;
        } else if (this.returnDate == null) {
            this.returnDate = LocalDate.now().toString();
        }
    }

    @Override
    public String toString() {
        return "Loan{" +
                "loanId='" + loanId + '\'' +
                ", bookIsbn='" + bookIsbn + '\'' +
                ", readerId='" + readerId + '\'' +
                ", borrowDate='" + borrowDate + '\'' +
                ", returnDate='" + returnDate + '\'' +
                ", active=" + active +
                '}';
    }
}

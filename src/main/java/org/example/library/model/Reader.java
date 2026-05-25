package org.example.library.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class Reader extends User {

    private List<String> borrowedBookIsbns;

    public Reader() {
        this.borrowedBookIsbns = new ArrayList<>();
    }

    public Reader(String id, String name, String email) {
        super(id, name, email);
        this.borrowedBookIsbns = new ArrayList<>();
    }

    @Override
    public String getRole() {
        return "Reader";
    }

    public List<String> getBorrowedBookIsbns() {
        return new ArrayList<>(borrowedBookIsbns);
    }

    public void setBorrowedBookIsbns(List<String> borrowedBookIsbns) {
        if (borrowedBookIsbns == null) {
            this.borrowedBookIsbns = new ArrayList<>();
            return;
        }

        LinkedHashSet<String> uniqueIsbns = new LinkedHashSet<>();
        for (String isbn : borrowedBookIsbns) {
            if (isbn != null) {
                uniqueIsbns.add(isbn);
            }
        }
        this.borrowedBookIsbns = new ArrayList<>(uniqueIsbns);
    }

    public void addBorrowedBook(String isbn) {
        if (isbn != null && !borrowedBookIsbns.contains(isbn)) {
            borrowedBookIsbns.add(isbn);
        }
    }

    public void removeBorrowedBook(String isbn) {
        borrowedBookIsbns.remove(isbn);
    }

    public boolean hasBorrowedBook(String isbn) {
        return borrowedBookIsbns.contains(isbn);
    }

    @Override
    public String toString() {
        return "Reader{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", borrowedBookIsbns=" + borrowedBookIsbns +
                '}';
    }
}

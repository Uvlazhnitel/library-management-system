package org.example.library.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        return borrowedBookIsbns;
    }

    public void setBorrowedBookIsbns(List<String> borrowedBookIsbns) {
        this.borrowedBookIsbns = borrowedBookIsbns == null ? new ArrayList<>() : borrowedBookIsbns;
    }

    public void addBorrowedBook(String isbn) {
        if (!borrowedBookIsbns.contains(isbn)) {
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Reader reader = (Reader) o;
        return getId() != null && Objects.equals(getId(), reader.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
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

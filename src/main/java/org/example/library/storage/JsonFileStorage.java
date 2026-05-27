package org.example.library.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.example.library.model.Book;
import org.example.library.model.Loan;
import org.example.library.model.Reader;
import org.example.library.service.Library;

public class JsonFileStorage implements Storage {

    private static final String FILE_PATH = "data/library.json";

    private static class LibraryData {
        ArrayList<Book> books;
        ArrayList<Reader> readers;
        ArrayList<Loan> loans;
    }

    @Override
    public void save() {
        LibraryData data = new LibraryData();
        data.books = new ArrayList<>(Library.getBooks());
        data.readers = new ArrayList<>(Library.getReaders());
        data.loans = new ArrayList<>(Library.getLoans());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(data);

        new File("data").mkdirs();

        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            writer.write(json);
            System.out.println("Data saved to " + FILE_PATH);
        } catch (IOException e) {
            System.out.println("Failed to save data: " + e.getMessage());
        }
    }

    @Override
    public void load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("No saved data found at " + FILE_PATH);
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            LibraryData data = gson.fromJson(reader, LibraryData.class);
            if (data != null) {
                if (data.books != null) Library.setBooks(data.books);
                if (data.readers != null) Library.setReaders(data.readers);
                if (data.loans != null) Library.setLoans(data.loans);
                System.out.println("Data loaded from " + FILE_PATH);
            }
        } catch (IOException e) {
            System.out.println("Failed to load data: " + e.getMessage());
        }
    }
}
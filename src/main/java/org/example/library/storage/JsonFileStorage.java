package org.example.library.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import org.example.library.model.Book;
import org.example.library.model.Librarian;
import org.example.library.model.Loan;
import org.example.library.model.Reader;
import org.example.library.service.Library;
import org.example.library.service.Logic;

public class JsonFileStorage implements Storage {

    private static final String DEFAULT_FILE_PATH = "data/library.json";
    private final String filePath;

    public JsonFileStorage() {
        this(DEFAULT_FILE_PATH);
    }

    public JsonFileStorage(String filePath) {
        this.filePath = filePath;
    }

    private static class LibraryData {
        ArrayList<Book> books;
        ArrayList<Reader> readers;
        ArrayList<Librarian> librarians;
        ArrayList<Loan> loans;
    }

    @Override
    public void save() {
        LibraryData data = new LibraryData();
        data.books = new ArrayList<>(Library.getBooks());
        data.readers = new ArrayList<>(Library.getReaders());
        data.librarians = new ArrayList<>(Library.getLibrarians());
        data.loans = new ArrayList<>(Library.getLoans());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(data);

        File targetFile = new File(filePath);
        File parentDir = targetFile.getParentFile();
        if (parentDir != null) {
            if (parentDir.exists() && !parentDir.isDirectory()) {
                Logic.logicOut("Failed to save data: " + parentDir.getPath() + " is not a directory.");
                return;
            }
            if (!parentDir.exists() && !parentDir.mkdirs()) {
                Logic.logicOut("Failed to save data: could not create directory " + parentDir.getPath());
                return;
            }
        }

        try (var writer = Files.newBufferedWriter(targetFile.toPath(), StandardCharsets.UTF_8)) {
            writer.write(json);
            Logic.logicOut("Data saved to " + filePath);
        } catch (IOException e) {
            Logic.logicOut("Failed to save data: " + e.getMessage());
        }
    }

    @Override
    public void load() {
        File file = new File(filePath);
        if (!file.exists()) {
            Logic.logicOut("No saved data found at " + filePath);
            return;
        }

        try (var reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            LibraryData data = gson.fromJson(reader, LibraryData.class);
            if (data != null) {
                Library.setBooks(data.books != null ? data.books : new ArrayList<>());
                Library.setReaders(data.readers != null ? data.readers : new ArrayList<>());
                Library.setLibrarians(data.librarians != null ? data.librarians : new ArrayList<>());
                Library.setLoans(data.loans != null ? data.loans : new ArrayList<>());
                Logic.logicOut("Data loaded from " + filePath);
            }
        } catch (JsonParseException e) {
            Logic.logicOut("Failed to load data: invalid JSON format.");
        } catch (IOException e) {
            Logic.logicOut("Failed to load data: " + e.getMessage());
        }
    }
}

# Library Management System

Library Management System is a Swing-based Java desktop application created as an OOP course final project. The application manages books, readers, librarians, loans, and JSON persistence through a graphical interface.

## Technologies

- Java 17
- Maven
- Swing
- Gson
- JUnit 5

## Current Functionality

- Add and remove books
- Register and remove readers
- Register and remove librarians
- Borrow and return books
- Search books by ISBN, title, author, or genre
- Check book availability
- Save and load data from `data/library.json`

## Business Rules

- A book cannot be borrowed if it is already unavailable
- A book, reader, or librarian with an active loan cannot be removed
- Duplicate ISBNs are rejected
- Duplicate reader IDs are rejected
- Duplicate librarian IDs are rejected
- IDs that already appear in loan history cannot be reused

## User Interface

The application starts with a Swing window and provides:

- `Books` tab for browsing, searching, adding, and removing books
- `Readers` tab for adding and removing readers
- `Librarians` tab for adding and removing librarians
- `Loans` tab for borrowing and returning books
- menu and toolbar actions for save/load

## Project Structure

```text
library-management-system/
├── pom.xml
├── README.md
├── data/
│   └── library.json
└── src/
    ├── main/
    │   └── java/
    │       └── org/example/library/
    │           ├── Main.java
    │           ├── exception/
    │           ├── model/
    │           ├── service/
    │           ├── storage/
    │           └── ui/
    └── test/
        └── java/
            └── org/example/library/
```

## Build

```bash
mvn clean package
```

## Run

```bash
mvn exec:java
```

This command launches the Swing desktop interface.

## Run Tests

```bash
mvn clean test
```

## Notes

- Application data is stored in `data/library.json`
- Persistence uses UTF-8 JSON files
- The service layer still contains console-oriented logic helpers, but the main entrypoint launches the Swing UI

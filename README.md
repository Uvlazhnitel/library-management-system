# Library Management System

Library Management System is a console-based Java application created as an OOP course final project. The system will manage books, readers, librarians, and book borrowing operations.

## Technologies Used

- Java 17
- Maven
- Gson
- JUnit 5

## Current Status

Completed:

- Initial Maven project structure
- Model package
- Basic Main class

In progress:

- Business logic
- Custom exceptions
- Console menu
- JSON file storage
- Unit tests

## Implemented Model Classes

### Book

Represents a book in the library.
Main fields: ISBN, title, author, year, genre, availability status.

### User

Abstract parent class for users.
Main fields: ID, name, email.
Contains abstract method `getRole()`.

### Reader

Extends `User`.
Represents a library reader.
Stores borrowed book ISBNs.
Provides methods to add, remove, and check borrowed books.

### Librarian

Extends `User`.
Represents a librarian.
Stores employee ID.

### Loan

Represents a borrowing operation.
Stores loan ID, book ISBN, reader ID, borrow date, return date, and active status.

## OOP Principles Demonstrated in the Model Package

- Encapsulation: all fields are private and accessed through getters and setters.
- Inheritance: `Reader` and `Librarian` extend `User`.
- Abstraction: `User` is an abstract class.
- Polymorphism: `Reader` and `Librarian` override `getRole()` differently.

## Important Note for Teammates

`Reader.getBorrowedBookIsbns()` returns a copy of the list.

Library service should use:

- `reader.addBorrowedBook(isbn)`
- `reader.removeBorrowedBook(isbn)`
- `reader.hasBorrowedBook(isbn)`

Do not use:

```java
reader.getBorrowedBookIsbns().add(isbn);
```

## Project Structure

```text
library-management-system/
├── pom.xml
├── README.md
├── data/
│   └── .gitkeep
└── src/
    ├── main/
    │   └── java/
    │       └── org/example/library/
    │           ├── Main.java
    │           ├── model/
    │           ├── service/
    │           ├── storage/
    │           └── exception/
    └── test/
        └── java/
            └── org/example/library/
```

## How to Build

```bash
mvn clean package
```

## How to Run

```bash
mvn exec:java
```

## How to Run Tests

```bash
mvn clean test
```

## Team Workflow

Keep `main` branch stable.
Work in feature branches.
Open Pull Requests before merging to `main`.

# Library Management System

Library Management System is a console-based Java application created as an OOP course final project. This repository currently contains only the initial Maven project structure and starter documentation.

## Technologies Used

- Java 17
- Maven
- Gson
- JUnit 5

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

## How to Run the Project

Build the project:

```bash
mvn clean package
```

Run the application:

```bash
mvn exec:java
```

## How to Run Tests

```bash
mvn clean test
```

## Team Branch Workflow

Use `main` only for stable, working code. All development work should be done in feature branches and merged back only after the branch is ready.

Suggested workflow:

1. Create a new feature branch from `main`.
2. Implement and test your changes in that branch.
3. Open a pull request or merge the feature branch after review.

## Current Scope

Business logic, model classes, and application features are intentionally not implemented yet. This stage only provides the initial Maven project structure.

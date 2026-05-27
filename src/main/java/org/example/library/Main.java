package org.example.library;

import org.example.library.service.Logic;

public class Main {

    public static void main(String[] args) {
        System.out.println("Library Management System started.");

        while (true) {
            System.out.println("Select an option:\r\n" + //
                                "        0 - exit\r\n" + //
                                "        1 - add book\r\n" + //
                                "        2 - remove book\r\n" + //
                                "        3 - view all books\r\n" + //
                                "        4 - register reader\r\n" + //
                                "        5 - view all readers\r\n" + //
                                "        6 - search books\r\n" + //
                                "        7 - borrow book\r\n" + //
                                "        8 - return book\r\n" + //
                                "        9 - check availability\r\n" + //
                                "       10 - save data\r\n" + //
                                "       11 - load data");

            String input = Logic.logicIn("");
            if (!Logic.handleMenuInput(input)) {
                System.out.println("Exiting Library Management System.");
                break;
            }
        }
    }
}

package org.example.library;
//
// import org.example.library.service.Logic;
//
// public class Main {
//
//     public static void main(String[] args) {
//         System.out.println("Library Management System started.");
//
//         while (true) {
//             System.out.println("Select an option:\r\n" + //
//                                 "        0 - exit\r\n" + //
//                                 "        1 - add book\r\n" + //
//                                 "        2 - remove book\r\n" + //
//                                 "        3 - view all books\r\n" + //
//                                 "        4 - register reader\r\n" + //
//                                 "        5 - remove reader\r\n" + //
//                                 "        6 - view all readers\r\n" + //
//                                 "        7 - register librarian\r\n" + //
//                                 "        8 - remove librarian\r\n" + //
//                                 "        9 - view all librarians\r\n" + //
//                                 "       10 - search books\r\n" + //
//                                 "       11 - borrow book\r\n" + //
//                                 "       12 - return book\r\n" + //
//                                 "       13 - check availability\r\n" + //
//                                 "       14 - save data\r\n" + //
//                                 "       15 - load data");
//
//             String input = Logic.logicIn("");
//             if (!Logic.handleMenuInput(input)) {
//                 System.out.println("Exiting Library Management System.");
//                 break;
//             }
//         }
//     }
// }

import javax.swing.SwingUtilities;
import org.example.library.ui.MainFrame;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}


package app;

import database.DatabaseInitializer;
import menu.MainMenu;
import util.Login;

public class Main {
    public static void main(String[] args) {
        if (!DatabaseInitializer.initializeDatabase()) {
            System.out.println("Application cannot start because the database is not available.");
            return;
        }

        while (true) {
            if (Login.login()) {
                MainMenu.showMenu();
            }
        }
    }
}

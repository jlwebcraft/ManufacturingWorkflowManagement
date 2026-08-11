package app;

import database.DatabaseInitializer;
import menu.MainMenu;
import util.Login;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.initializeDatabase();
        while (true) {
            if (Login.login()) {
                MainMenu.showMenu();
            }
        }
    }
}
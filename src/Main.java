public class Main {
    public static void main(String[] args) {
//        DatabaseInitializer.initializeDatabase(); //used for table creation and initialization, not useful anymore
        while (true) {
            if (Login.login()) {
                MainMenu.showMenu();
            }
        }
    }
}
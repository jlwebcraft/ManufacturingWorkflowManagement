public class Main {
    public static void main(String[] args) {
//        DatabaseInitializer.initializeDatabase();
        Thread productionWorker = new Thread(new ProductionWorker());
        productionWorker.setDaemon(true);
        productionWorker.start();
        while (true) {
            if (Login.login()) {
                MainMenu.showMenu();
            }
        }
    }
}
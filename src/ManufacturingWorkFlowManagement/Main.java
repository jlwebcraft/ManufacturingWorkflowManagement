package ManufacturingWorkFlowManagement;

import database.DatabaseInitializer;
import menu.MainMenu;
import thread.ProductionWorker;
import util.Login;

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
package workflow;

import DSA.BinarySearchTree;
import DSA.HashMap;
import DSA.HashSet;
import DSA.LinkedList;
import DSA.PriorityQueue;
import DSA.Queue;
import DSA.Stack;
import DSA.Vector;
import database.DatabaseConnection;
import exception.InsufficientMaterialException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

public class ManufacturingConceptPractice {

    private int reservedResourceCount;

    public void runAllExamples() {
        ManufacturingWorkflowGuide.printWorkflow();
        showAbstractClassAndInterface();
        showLambdaPredicate();
        showCustomException();
        showDataStructures();
        showFileIo();
        showSynchronization();
        showMetadata();
        showCallableStatement();
    }

    public void showAbstractClassAndInterface() {
        WorkflowStep step = new ProductPlanningTask("Gaming Performance PC", 10);
        step.executeStep();
    }

    public void showLambdaPredicate() {
        Predicate<Integer> validProductionQuantity = quantity -> quantity > 0 && quantity <= 100;
        System.out.println("Lambda + Predicate result for quantity 25 : " + validProductionQuantity.test(25));
    }

    public void showCustomException() {
        try {
            validateMaterialStock("Graphics Card", 5, 2);
        } catch (InsufficientMaterialException e) {
            System.out.println("Custom Exception : " + e.getMessage());
        }
    }

    public void showDataStructures() {
        LinkedList productionHistory = new LinkedList();
        productionHistory.addLast(101);
        productionHistory.addLast(102);
        productionHistory.insertAfter(101, 150);
        productionHistory.display();

        Stack workflowUndo = new Stack(5);
        workflowUndo.push(1);
        workflowUndo.push(2);
        System.out.println("Stack pop for workflow undo : " + workflowUndo.pop());

        Queue waitingOrders = new Queue(5);
        waitingOrders.enqueue(201);
        waitingOrders.enqueue(202);
        System.out.println("Queue dequeue waiting order : " + waitingOrders.dequeue());

        PriorityQueue priorityQueue = new PriorityQueue(5);
        priorityQueue.enqueue(301, 2);
        priorityQueue.enqueue(302, 1);
        System.out.println("PriorityQueue next order : " + priorityQueue.dequeue());

        HashMap productLookup = new HashMap(10);
        productLookup.put(1, "Office Desktop PC");
        System.out.println("HashMap product lookup : " + productLookup.get(1));

        HashSet allocatedMachines = new HashSet(10);
        allocatedMachines.add(1);
        System.out.println("HashSet machine allocated : " + allocatedMachines.contains(1));

        Vector dailyCompletedOrders = new Vector();
        dailyCompletedOrders.add(10);
        dailyCompletedOrders.add(20);
        dailyCompletedOrders.display();

        BinarySearchTree productCatalogTree = new BinarySearchTree();
        productCatalogTree.insert(50);
        productCatalogTree.insert(25);
        productCatalogTree.insert(75);
        System.out.print("BST sorted catalog IDs : ");
        productCatalogTree.inOrder();
    }

    public void showFileIo() {
        String fileName = "workflow_log.txt";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write("Workflow examples executed at " + LocalDateTime.now().format(formatter));
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Unable to write workflow log file.");
            e.printStackTrace();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            System.out.println("FileReader + BufferedReader output:");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Unable to read workflow log file.");
            e.printStackTrace();
        }
    }

    public synchronized void reserveResource(String resourceName) {
        reservedResourceCount++;
        System.out.println("Synchronized reservation for " + resourceName
                + ". Total reserved resources: " + reservedResourceCount);
    }

    public void showSynchronization() {
        reserveResource("Assembly Line A");
    }

    public void showCallableStatement() {
        String sql = "{CALL sp_record_material_transaction(?, ?, ?, ?, ?)}";

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                CallableStatement callableStatement = connection.prepareCall(sql)
        ) {
            callableStatement.setInt(1, 1);
            callableStatement.setString(2, "IN");
            callableStatement.setDouble(3, 1.00);
            callableStatement.setString(4, "CallableStatement example");
            callableStatement.setNull(5, java.sql.Types.INTEGER);
            callableStatement.execute();
            System.out.println("CallableStatement stored procedure executed.");
        } catch (SQLException e) {
            System.out.println("CallableStatement example needs initialized database procedure.");
            e.printStackTrace();
        }
    }

    public void showMetadata() {
        String sql = "SELECT * FROM vw_inventory_summary LIMIT 1";

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            System.out.println("Database Product : " + databaseMetaData.getDatabaseProductName());

            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            System.out.println("ResultSet Column Count : " + resultSetMetaData.getColumnCount());

            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                System.out.println("Column " + i + " : " + resultSetMetaData.getColumnName(i));
            }
        } catch (SQLException e) {
            System.out.println("Metadata example needs initialized database view.");
            e.printStackTrace();
        }
    }

    private void validateMaterialStock(String materialName,
                                       int requiredQuantity,
                                       int availableQuantity) throws InsufficientMaterialException {
        if (availableQuantity < requiredQuantity) {
            throw new InsufficientMaterialException(
                    materialName + " required " + requiredQuantity + " but available " + availableQuantity
            );
        }
    }
}

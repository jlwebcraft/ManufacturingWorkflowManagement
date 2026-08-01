package util;

import model.ProductionOrder;

import java.util.Comparator;

public class ProductionOrderComparator
        implements Comparator<ProductionOrder> {

    @Override
    public int compare(ProductionOrder order1,
                       ProductionOrder order2) {

        return getPriorityValue(order2.getPriority())
                - getPriorityValue(order1.getPriority());

    }

    private int getPriorityValue(String priority) {

        switch (priority) {

            case "HIGH":
                return 3;

            case "MEDIUM":
                return 2;

            case "LOW":
                return 1;

            default:
                return 0;

        }

    }

}
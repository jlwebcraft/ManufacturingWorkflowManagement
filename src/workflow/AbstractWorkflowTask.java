package workflow;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class AbstractWorkflowTask implements WorkflowStep {

    private final String stepName;
    private final LocalDateTime createdAt;

    protected AbstractWorkflowTask(String stepName) {
        this.stepName = stepName;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public String stepName() {
        return stepName;
    }

    public String formattedCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return createdAt.format(formatter);
    }

    protected void printHeader() {
        System.out.println("Workflow Step : " + stepName);
        System.out.println("Created At    : " + formattedCreatedAt());
    }
}
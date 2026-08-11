package workflow;

public interface WorkflowStep {

    void executeStep();

    String stepName();
}
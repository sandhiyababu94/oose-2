import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SprintManager class manages all sprints and sprint-related operations
 */
public class SprintManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private String sprintManagerId;
    private List<Sprint> sprints;
    private Map<String, SprintBacklog> sprintBacklogs;
    private int nextSprintId;
    private Sprint currentSprint;

    // Constructor
    public SprintManager(String sprintManagerId) {
        this.sprintManagerId = sprintManagerId;
        this.sprints = new ArrayList<>();
        this.sprintBacklogs = new HashMap<>();
        this.nextSprintId = 1;
        this.currentSprint = null;
    }

    // Create new sprint
    public Sprint createSprint(String sprintName, LocalDate startDate, LocalDate endDate, String goal) {
        String sprintId = "SPRINT-" + String.format("%03d", nextSprintId++);
        Sprint sprint = new Sprint(sprintId, sprintName, startDate, endDate, goal);
        sprints.add(sprint);
        
        String sprintBacklogId = sprintId + "-BACKLOG";
        SprintBacklog sprintBacklog = new SprintBacklog(sprintBacklogId, sprint);
        sprintBacklogs.put(sprintId, sprintBacklog);
        
        return sprint;
    }

    // Get sprint by ID
    public Sprint getSprintById(String sprintId) {
        return sprints.stream()
                .filter(sprint -> sprint.getSprintId().equals(sprintId))
                .findFirst()
                .orElse(null);
    }

    // Get sprint backlog
    public SprintBacklog getSprintBacklog(String sprintId) {
        return sprintBacklogs.get(sprintId);
    }

    // Add task to sprint
    public boolean addTaskToSprint(String sprintId, Task task) {
        Sprint sprint = getSprintById(sprintId);
        SprintBacklog sprintBacklog = getSprintBacklog(sprintId);
        
        if (sprint != null && sprintBacklog != null) {
            sprint.addTaskToSprint(task);
            sprintBacklog.addTaskToBacklog(task);
            task.setStatus(Task.Status.TODO);
            return true;
        }
        return false;
    }

    // Remove task from sprint
    public boolean removeTaskFromSprint(String sprintId, String taskId) {
        Sprint sprint = getSprintById(sprintId);
        SprintBacklog sprintBacklog = getSprintBacklog(sprintId);
        Task task = sprintBacklog != null ? sprintBacklog.getTaskById(taskId) : null;
        
        if (sprint != null && task != null) {
            sprint.removeTaskFromSprint(task);
            sprintBacklog.removeTaskFromBacklog(taskId);
            task.setStatus(Task.Status.BACKLOG);
            return true;
        }
        return false;
    }

    // Start sprint
    public boolean startSprint(String sprintId) {
        Sprint sprint = getSprintById(sprintId);
        if (sprint != null && sprint.canStartSprint()) {
            sprint.startSprint();
            this.currentSprint = sprint;
            return true;
        }
        return false;
    }

    // Complete sprint
    public boolean completeSprint(String sprintId) {
        Sprint sprint = getSprintById(sprintId);
        if (sprint != null && sprint.canCompleteSprint()) {
            sprint.completeSprint();
            if (currentSprint == sprint) {
                this.currentSprint = null;
            }
            return true;
        }
        return false;
    }

    // Cancel sprint
    public boolean cancelSprint(String sprintId) {
        Sprint sprint = getSprintById(sprintId);
        if (sprint != null) {
            sprint.cancelSprint();
            if (currentSprint == sprint) {
                this.currentSprint = null;
            }
            return true;
        }
        return false;
    }

    // Get all sprints
    public List<Sprint> getAllSprints() {
        return new ArrayList<>(sprints);
    }

    // Get sprints by status
    public List<Sprint> getSprintsByStatus(Sprint.SprintStatus status) {
        return sprints.stream()
                .filter(sprint -> sprint.getStatus() == status)
                .collect(Collectors.toList());
    }

    // Get active sprint
    public Sprint getActiveSprint() {
        return sprints.stream()
                .filter(sprint -> sprint.getStatus() == Sprint.SprintStatus.ACTIVE)
                .findFirst()
                .orElse(null);
    }

    // Get current sprint
    public Sprint getCurrentSprint() {
        return currentSprint;
    }

    // Get upcoming sprints
    public List<Sprint> getUpcomingSprints() {
        return sprints.stream()
                .filter(sprint -> sprint.getStatus() == Sprint.SprintStatus.PLANNED)
                .collect(Collectors.toList());
    }

    // Get completed sprints
    public List<Sprint> getCompletedSprints() {
        return sprints.stream()
                .filter(sprint -> sprint.getStatus() == Sprint.SprintStatus.COMPLETED)
                .collect(Collectors.toList());
    }

    // Update task status in sprint
    public boolean updateTaskStatusInSprint(String sprintId, String taskId, Task.Status newStatus) {
        SprintBacklog sprintBacklog = getSprintBacklog(sprintId);
        if (sprintBacklog != null) {
            return sprintBacklog.updateTaskStatus(taskId, newStatus);
        }
        return false;
    }

    // Assign task to student in sprint
    public boolean assignTaskInSprint(String sprintId, String taskId, String studentName) {
        SprintBacklog sprintBacklog = getSprintBacklog(sprintId);
        if (sprintBacklog != null) {
            return sprintBacklog.assignTaskToStudent(taskId, studentName);
        }
        return false;
    }

    // Get metrics
    public int getTotalSprints() {
        return sprints.size();
    }

    public int getActiveSprints() {
        return (int) sprints.stream()
                .filter(sprint -> sprint.getStatus() == Sprint.SprintStatus.ACTIVE)
                .count();
    }

    public double getAverageVelocity() {
        List<Sprint> completedSprints = getCompletedSprints();
        if (completedSprints.isEmpty()) return 0;
        return completedSprints.stream()
                .mapToInt(Sprint::getTotalStoryPoints)
                .average()
                .orElse(0);
    }

    public int getTotalCompletedStoryPoints() {
        return getCompletedSprints().stream()
                .mapToInt(Sprint::getCompletedStoryPoints)
                .sum();
    }

    public double getOverallCompletionRate() {
        List<Sprint> completedSprints = getCompletedSprints();
        if (completedSprints.isEmpty()) return 0;
        return completedSprints.stream()
                .mapToDouble(Sprint::getCompletionPercentage)
                .average()
                .orElse(0);
    }

    // Get burndown chart data for a sprint
    public List<Integer> getBurndownData(String sprintId) {
        SprintBacklog sprintBacklog = getSprintBacklog(sprintId);
        if (sprintBacklog != null) {
            List<Integer> burndownData = new ArrayList<>();
            burndownData.add(sprintBacklog.getRemainingStoryPoints());
            return burndownData;
        }
        return new ArrayList<>();
    }

    // Generate sprint report
    public String generateSprintReport(String sprintId) {
        Sprint sprint = getSprintById(sprintId);
        SprintBacklog sprintBacklog = getSprintBacklog(sprintId);
        
        if (sprint == null || sprintBacklog == null) {
            return "Sprint not found";
        }
        
        StringBuilder report = new StringBuilder();
        report.append("\n========== SPRINT REPORT ==========\n");
        report.append("Sprint ID: ").append(sprint.getSprintId()).append("\n");
        report.append("Sprint Name: ").append(sprint.getSprintName()).append("\n");
        report.append("Goal: ").append(sprint.getGoal()).append("\n");
        report.append("Status: ").append(sprint.getStatus().getDisplayName()).append("\n");
        report.append("Duration: ").append(sprint.getSprintDuration()).append(" days\n");
        report.append("Start Date: ").append(sprint.getStartDate()).append("\n");
        report.append("End Date: ").append(sprint.getEndDate()).append("\n");
        report.append("\n--- Story Points ---\n");
        report.append("Total Story Points: ").append(sprint.getTotalStoryPoints()).append("\n");
        report.append("Completed Story Points: ").append(sprint.getCompletedStoryPoints()).append("\n");
        report.append("Remaining Story Points: ").append(sprint.getRemainingStoryPoints()).append("\n");
        report.append("Completion %: ").append(String.format("%.2f%%", sprint.getCompletionPercentage())).append("\n");
        report.append("\n--- Task Breakdown ---\n");
        report.append("Total Tasks: ").append(sprintBacklog.getTotalTasksInBacklog()).append("\n");
        report.append("To Do: ").append(sprintBacklog.getTaskCountByStatus(Task.Status.TODO)).append("\n");
        report.append("In Progress: ").append(sprintBacklog.getTaskCountByStatus(Task.Status.IN_PROGRESS)).append("\n");
        report.append("In Review: ").append(sprintBacklog.getTaskCountByStatus(Task.Status.IN_REVIEW)).append("\n");
        report.append("Done: ").append(sprintBacklog.getTaskCountByStatus(Task.Status.DONE)).append("\n");
        report.append("\n====================================\n");
        
        return report.toString();
    }

    @Override
    public String toString() {
        return String.format("SprintManager{" +
                "ID='%s', TotalSprints=%d, ActiveSprints=%d, CompletedSprints=%d, " +
                "AverageVelocity=%.2f}",
                sprintManagerId, getTotalSprints(), getActiveSprints(),
                getCompletedSprints().size(), getAverageVelocity());
    }
}

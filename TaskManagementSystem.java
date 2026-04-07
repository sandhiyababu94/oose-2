import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * TaskManagementSystem - Main class for Agile-Based Student Task Management System
 * Implements Scrum principles with sprint tracking and backlog management
 * Supports SDG 4 - Quality Education
 */
public class TaskManagementSystem {
    private ProductBacklog productBacklog;
    private SprintManager sprintManager;
    private String projectName;

    // Constructor
    public TaskManagementSystem(String projectName) {
        this.projectName = projectName;
        this.productBacklog = new ProductBacklog("BACKLOG-001");
        this.sprintManager = new SprintManager("SPRINT-MGR-001");
    }

    // ===================== BACKLOG MANAGEMENT =====================
    
    public void addTaskToBacklog(String title, String description, Task.Priority priority,
                                  int storyPoints, LocalDate dueDate) {
        Task task = productBacklog.addTask(title, description, priority, storyPoints, dueDate);
        System.out.println("✓ Task added: " + task.getTaskId());
    }

    public void removeTaskFromBacklog(String taskId) {
        if (productBacklog.removeTask(taskId)) {
            System.out.println("✓ Task removed: " + taskId);
        } else {
            System.out.println("✗ Task not found: " + taskId);
        }
    }

    public void prioritizeBacklog() {
        productBacklog.prioritizeBacklog();
        System.out.println("✓ Backlog prioritized");
    }

    public void displayBacklog() {
        System.out.println("\n========== PRODUCT BACKLOG ==========");
        List<Task> tasks = productBacklog.getBacklogItems();
        if (tasks.isEmpty()) {
            System.out.println("No tasks in backlog");
            return;
        }
        
        int index = 1;
        for (Task task : tasks) {
            System.out.printf("%d. %s | Priority: %s | Points: %d | Status: %s%n",
                    index++, task.getTaskId() + " - " + task.getTitle(),
                    task.getPriority(), task.getStoryPoints(), 
                    task.getStatus().getDisplayName());
        }
        System.out.println("====================================\n");
    }

    // ===================== SPRINT MANAGEMENT =====================
    
    public void createNewSprint(String sprintName, LocalDate startDate, LocalDate endDate, String goal) {
        Sprint sprint = sprintManager.createSprint(sprintName, startDate, endDate, goal);
        System.out.println("✓ Sprint created: " + sprint.getSprintId());
    }

    public void addTaskToSprint(String sprintId, String taskId) {
        Task task = productBacklog.getTaskById(taskId);
        if (task == null) {
            System.out.println("✗ Task not found: " + taskId);
            return;
        }
        
        if (sprintManager.addTaskToSprint(sprintId, task)) {
            System.out.println("✓ Task added to sprint: " + taskId);
        } else {
            System.out.println("✗ Failed to add task to sprint");
        }
    }

    public void removeTaskFromSprint(String sprintId, String taskId) {
        if (sprintManager.removeTaskFromSprint(sprintId, taskId)) {
            System.out.println("✓ Task removed from sprint: " + taskId);
        } else {
            System.out.println("✗ Failed to remove task");
        }
    }

    public void startSprint(String sprintId) {
        if (sprintManager.startSprint(sprintId)) {
            System.out.println("✓ Sprint started: " + sprintId);
        } else {
            System.out.println("✗ Cannot start sprint");
        }
    }

    public void completeSprint(String sprintId) {
        if (sprintManager.completeSprint(sprintId)) {
            System.out.println("✓ Sprint completed: " + sprintId);
        } else {
            System.out.println("✗ Cannot complete sprint");
        }
    }

    public void displaySprints() {
        System.out.println("\n========== ALL SPRINTS ==========");
        List<Sprint> sprints = sprintManager.getAllSprints();
        if (sprints.isEmpty()) {
            System.out.println("No sprints created");
            return;
        }
        
        for (Sprint sprint : sprints) {
            System.out.println(sprint);
        }
        System.out.println("================================\n");
    }

    public void displaySprintDetails(String sprintId) {
        Sprint sprint = sprintManager.getSprintById(sprintId);
        SprintBacklog sprintBacklog = sprintManager.getSprintBacklog(sprintId);
        
        if (sprint == null) {
            System.out.println("✗ Sprint not found: " + sprintId);
            return;
        }
        
        System.out.println("\n========== SPRINT DETAILS ==========");
        System.out.println("Sprint ID: " + sprint.getSprintId());
        System.out.println("Name: " + sprint.getSprintName());
        System.out.println("Goal: " + sprint.getGoal());
        System.out.println("Status: " + sprint.getStatus().getDisplayName());
        System.out.println("Duration: " + sprint.getSprintDuration() + " days");
        System.out.println("Start Date: " + sprint.getStartDate());
        System.out.println("End Date: " + sprint.getEndDate());
        System.out.println("\nStory Points: " + sprint.getTotalStoryPoints());
        System.out.println("Completed: " + sprint.getCompletedStoryPoints());
        System.out.println("Remaining: " + sprint.getRemainingStoryPoints());
        System.out.println("Progress: " + String.format("%.2f%%", sprint.getCompletionPercentage()));
        
        if (sprintBacklog != null) {
            System.out.println("\nTasks:");
            System.out.println("  To Do: " + sprintBacklog.getTaskCountByStatus(Task.Status.TODO));
            System.out.println("  In Progress: " + sprintBacklog.getTaskCountByStatus(Task.Status.IN_PROGRESS));
            System.out.println("  In Review: " + sprintBacklog.getTaskCountByStatus(Task.Status.IN_REVIEW));
            System.out.println("  Done: " + sprintBacklog.getTaskCountByStatus(Task.Status.DONE));
        }
        System.out.println("=====================================\n");
    }

    // ===================== TASK MANAGEMENT =====================
    
    public void updateTaskStatus(String sprintId, String taskId, Task.Status newStatus) {
        if (sprintManager.updateTaskStatusInSprint(sprintId, taskId, newStatus)) {
            System.out.println("✓ Task status updated: " + taskId + " -> " + newStatus.getDisplayName());
        } else {
            System.out.println("✗ Failed to update task status");
        }
    }

    public void assignTaskToStudent(String sprintId, String taskId, String studentName) {
        if (sprintManager.assignTaskInSprint(sprintId, taskId, studentName)) {
            System.out.println("✓ Task assigned to " + studentName + ": " + taskId);
        } else {
            System.out.println("✗ Failed to assign task");
        }
    }

    public void displayTasksByStudent(String sprintId, String studentName) {
        SprintBacklog sprintBacklog = sprintManager.getSprintBacklog(sprintId);
        if (sprintBacklog == null) {
            System.out.println("✗ Sprint not found");
            return;
        }
        
        List<Task> tasks = sprintBacklog.getTasksAssignedTo(studentName);
        System.out.println("\n========== Tasks for " + studentName + " ==========");
        if (tasks.isEmpty()) {
            System.out.println("No tasks assigned");
        } else {
            for (Task task : tasks) {
                System.out.println(task);
            }
        }
        System.out.println("=========================================\n");
    }

    // ===================== DAILY STANDUP =====================
    
    public void generateDailyStandup(String sprintId) {
        SprintBacklog sprintBacklog = sprintManager.getSprintBacklog(sprintId);
        if (sprintBacklog == null) {
            System.out.println("✗ Sprint not found");
            return;
        }
        
        System.out.println("\n========== DAILY STANDUP ==========");
        Map<String, List<Task>> tasksByAssignee = sprintBacklog.getTasksByAssignee();
        
        for (Map.Entry<String, List<Task>> entry : tasksByAssignee.entrySet()) {
            String assignee = entry.getKey();
            List<Task> tasks = entry.getValue();
            
            System.out.println("\n[" + assignee + "]");
            
            List<Task> inProgress = tasks.stream()
                    .filter(t -> t.getStatus() == Task.Status.IN_PROGRESS)
                    .toList();
            List<Task> done = tasks.stream()
                    .filter(t -> t.getStatus() == Task.Status.DONE)
                    .toList();
            List<Task> todo = tasks.stream()
                    .filter(t -> t.getStatus() == Task.Status.TODO)
                    .toList();
            
            System.out.println("  What's done: " + (done.isEmpty() ? "Nothing" : done.size() + " task(s)"));
            System.out.println("  What's in progress: " + (inProgress.isEmpty() ? "Nothing" : inProgress.size() + " task(s)"));
            System.out.println("  What's next: " + (todo.isEmpty() ? "Nothing" : todo.size() + " task(s)"));
        }
        System.out.println("\n===================================\n");
    }

    // ===================== REPORTING & ANALYTICS =====================
    
    public void displayProductMetrics() {
        System.out.println("\n========== PRODUCT METRICS ==========");
        System.out.println("Total Backlog Items: " + productBacklog.getTotalBacklogItems());
        System.out.println("Total Story Points: " + productBacklog.getTotalStoryPoints());
        System.out.println("Items in Backlog: " + productBacklog.getBacklogItemCount(Task.Status.BACKLOG));
        System.out.println("Items in Progress: " + productBacklog.getBacklogItemCount(Task.Status.IN_PROGRESS));
        System.out.println("Items Done: " + productBacklog.getBacklogItemCount(Task.Status.DONE));
        
        Map<Task.Priority, Integer> priorityDist = productBacklog.getPriorityDistribution();
        System.out.println("\nPriority Distribution:");
        for (Task.Priority priority : Task.Priority.values()) {
            System.out.println("  " + priority + ": " + priorityDist.get(priority));
        }
        System.out.println("====================================\n");
    }

    public void displaySprintMetrics() {
        System.out.println("\n========== SPRINT METRICS ==========");
        System.out.println("Total Sprints: " + sprintManager.getTotalSprints());
        System.out.println("Active Sprints: " + sprintManager.getActiveSprints());
        System.out.println("Completed Sprints: " + sprintManager.getCompletedSprints().size());
        System.out.println("Average Velocity: " + String.format("%.2f", sprintManager.getAverageVelocity()));
        System.out.println("Total Completed Story Points: " + sprintManager.getTotalCompletedStoryPoints());
        System.out.println("Overall Completion Rate: " + String.format("%.2f%%", sprintManager.getOverallCompletionRate()));
        System.out.println("====================================\n");
    }

    public void displaySprintReport(String sprintId) {
        System.out.println(sprintManager.generateSprintReport(sprintId));
    }

    // ===================== PERSISTENCE =====================
    
    public void saveProject(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
            System.out.println("✓ Project saved: " + filename);
        } catch (IOException e) {
            System.out.println("✗ Error saving project: " + e.getMessage());
        }
    }

    public static TaskManagementSystem loadProject(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            TaskManagementSystem system = (TaskManagementSystem) ois.readObject();
            System.out.println("✓ Project loaded: " + filename);
            return system;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("✗ Error loading project: " + e.getMessage());
            return null;
        }
    }

    // ===================== MAIN METHOD - DEMONSTRATION =====================
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║   AGILE-BASED STUDENT TASK MANAGEMENT SYSTEM (SDG 4)        ║");
        System.out.println("║   Scrum Principles with Sprint & Backlog Management         ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        TaskManagementSystem system = new TaskManagementSystem("Education Platform Project");

        // ===================== STEP 1: Create Product Backlog =====================
        System.out.println(">>> STEP 1: Creating Product Backlog...\n");
        
        system.addTaskToBacklog("Design User Interface", 
                "Create mockups and design system for the platform",
                Task.Priority.HIGH, 13, LocalDate.now().plusDays(14));
        
        system.addTaskToBacklog("Setup Database Schema",
                "Design and implement database tables",
                Task.Priority.CRITICAL, 8, LocalDate.now().plusDays(10));
        
        system.addTaskToBacklog("User Authentication",
                "Implement login and signup functionality",
                Task.Priority.CRITICAL, 13, LocalDate.now().plusDays(12));
        
        system.addTaskToBacklog("API Development",
                "Create RESTful APIs for core features",
                Task.Priority.HIGH, 21, LocalDate.now().plusDays(20));
        
        system.addTaskToBacklog("Testing Framework",
                "Setup unit and integration testing",
                Task.Priority.MEDIUM, 8, LocalDate.now().plusDays(15));
        
        system.addTaskToBacklog("Documentation",
                "Write API and user documentation",
                Task.Priority.MEDIUM, 5, LocalDate.now().plusDays(25));
        
        system.addTaskToBacklog("Deployment Setup",
                "Configure CI/CD pipeline",
                Task.Priority.HIGH, 8, LocalDate.now().plusDays(30));

        system.displayBacklog();
        system.prioritizeBacklog();
        system.displayBacklog();
        system.displayProductMetrics();

        // ===================== STEP 2: Plan Sprint 1 =====================
        System.out.println(">>> STEP 2: Planning Sprint 1...\n");
        
        LocalDate sprint1Start = LocalDate.now();
        LocalDate sprint1End = sprint1Start.plusWeeks(2);
        system.createNewSprint("Sprint 1 - Core Setup", sprint1Start, sprint1End, 
                "Setup database and basic authentication");

        String sprintId1 = "SPRINT-001";
        system.addTaskToSprint(sprintId1, "TASK-0002");
        system.addTaskToSprint(sprintId1, "TASK-0003");
        system.addTaskToSprint(sprintId1, "TASK-0005");

        system.displaySprintDetails(sprintId1);

        // ===================== STEP 3: Plan Sprint 2 =====================
        System.out.println(">>> STEP 3: Planning Sprint 2...\n");
        
        LocalDate sprint2Start = sprint1End.plusDays(1);
        LocalDate sprint2End = sprint2Start.plusWeeks(2);
        system.createNewSprint("Sprint 2 - Features Development", sprint2Start, sprint2End,
                "Develop API and UI components");

        String sprintId2 = "SPRINT-002";
        system.addTaskToSprint(sprintId2, "TASK-0001");
        system.addTaskToSprint(sprintId2, "TASK-0004");

        system.displaySprintDetails(sprintId2);

        // ===================== STEP 4: Start Sprint 1 =====================
        System.out.println(">>> STEP 4: Starting Sprint 1...\n");
        
        system.startSprint(sprintId1);
        system.displaySprints();

        // ===================== STEP 5: Assign Tasks & Update Status =====================
        System.out.println(">>> STEP 5: Assigning Tasks and Updating Status...\n");
        
        system.assignTaskToStudent(sprintId1, "TASK-0002", "Priya");
        system.assignTaskToStudent(sprintId1, "TASK-0003", "Rajesh");
        system.assignTaskToStudent(sprintId1, "TASK-0005", "Ananya");

        system.displayTasksByStudent(sprintId1, "Priya");

        // Simulate work progress
        System.out.println(">>> Simulating task progress...\n");
        system.updateTaskStatus(sprintId1, "TASK-0002", Task.Status.IN_PROGRESS);
        system.updateTaskStatus(sprintId1, "TASK-0003", Task.Status.IN_PROGRESS);
        system.updateTaskStatus(sprintId1, "TASK-0002", Task.Status.IN_REVIEW);
        system.updateTaskStatus(sprintId1, "TASK-0002", Task.Status.DONE);
        system.updateTaskStatus(sprintId1, "TASK-0003", Task.Status.DONE);

        system.displaySprintDetails(sprintId1);

        // ===================== STEP 6: Daily Standup =====================
        System.out.println(">>> STEP 6: Daily Standup Meeting...\n");
        system.generateDailyStandup(sprintId1);

        // ===================== STEP 7: Complete Sprint 1 =====================
        System.out.println(">>> STEP 7: Completing Sprint 1...\n");
        system.completeSprint(sprintId1);
        system.displaySprintReport(sprintId1);

        // ===================== STEP 8: Metrics & Analytics =====================
        System.out.println(">>> STEP 8: Project Metrics & Analytics...\n");
        system.displaySprintMetrics();
        system.displayProductMetrics();

        // ===================== STEP 9: Persistence =====================
        System.out.println(">>> STEP 9: Saving Project...\n");
        system.saveProject("project_state.dat");

        // ===================== Final Summary =====================
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    PROJECT SUMMARY                         ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        System.out.println("Project Name: " + system.projectName);
        System.out.println(system.sprintManager);
        System.out.println(system.productBacklog);
        
        System.out.println("\n✓ System demonstration completed successfully!");
        System.out.println("✓ All features implemented with Scrum principles");
        System.out.println("✓ Project data persisted for future sessions");
    }
}

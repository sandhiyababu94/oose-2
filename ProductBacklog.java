import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ProductBacklog class manages all tasks, backlog items, and backlog ordering
 */
public class ProductBacklog implements Serializable {
    private static final long serialVersionUID = 1L;
    private String backlogId;
    private List<Task> backlogItems;
    private int nextTaskId;

    // Constructor
    public ProductBacklog(String backlogId) {
        this.backlogId = backlogId;
        this.backlogItems = new ArrayList<>();
        this.nextTaskId = 1;
    }

    // Getters
    public String getBacklogId() {
        return backlogId;
    }

    public List<Task> getBacklogItems() {
        return new ArrayList<>(backlogItems);
    }

    public List<Task> getBacklogItems(Task.Status status) {
        return backlogItems.stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    // Add task to backlog
    public Task addTask(String title, String description, Task.Priority priority, 
                        int storyPoints, LocalDate dueDate) {
        String taskId = "TASK-" + String.format("%04d", nextTaskId++);
        Task task = new Task(taskId, title, description, priority, storyPoints, dueDate);
        backlogItems.add(task);
        return task;
    }

    // Add existing task
    public boolean addExistingTask(Task task) {
        if (task != null && !backlogItems.contains(task)) {
            backlogItems.add(task);
            return true;
        }
        return false;
    }

    // Remove task from backlog
    public boolean removeTask(String taskId) {
        return backlogItems.removeIf(task -> task.getTaskId().equals(taskId));
    }

    // Get task by ID
    public Task getTaskById(String taskId) {
        return backlogItems.stream()
                .filter(task -> task.getTaskId().equals(taskId))
                .findFirst()
                .orElse(null);
    }

    // Update task
    public boolean updateTask(String taskId, String title, String description, 
                              Task.Priority priority, int storyPoints, LocalDate dueDate) {
        Task task = getTaskById(taskId);
        if (task != null) {
            task.setTitle(title);
            task.setDescription(description);
            task.setPriority(priority);
            task.setStoryPoints(storyPoints);
            task.setDueDate(dueDate);
            return true;
        }
        return false;
    }

    // Backlog ordering and prioritization
    public void prioritizeBacklog() {
        backlogItems.sort(Comparator.naturalOrder());
    }

    public void moveTaskUp(String taskId) {
        Task task = getTaskById(taskId);
        if (task != null) {
            int currentIndex = backlogItems.indexOf(task);
            if (currentIndex > 0) {
                Collections.swap(backlogItems, currentIndex, currentIndex - 1);
            }
        }
    }

    public void moveTaskDown(String taskId) {
        Task task = getTaskById(taskId);
        if (task != null) {
            int currentIndex = backlogItems.indexOf(task);
            if (currentIndex < backlogItems.size() - 1) {
                Collections.swap(backlogItems, currentIndex, currentIndex + 1);
            }
        }
    }

    public void moveTaskToPosition(String taskId, int newPosition) {
        Task task = getTaskById(taskId);
        if (task != null && newPosition >= 0 && newPosition < backlogItems.size()) {
            backlogItems.remove(task);
            backlogItems.add(newPosition, task);
        }
    }

    // Query methods
    public int getTotalBacklogItems() {
        return backlogItems.size();
    }

    public int getBacklogItemCount(Task.Status status) {
        return (int) backlogItems.stream()
                .filter(task -> task.getStatus() == status)
                .count();
    }

    public int getTotalStoryPoints() {
        return backlogItems.stream()
                .mapToInt(Task::getStoryPoints)
                .sum();
    }

    public int getTotalStoryPoints(Task.Status status) {
        return backlogItems.stream()
                .filter(task -> task.getStatus() == status)
                .mapToInt(Task::getStoryPoints)
                .sum();
    }

    public List<Task> getTasksByPriority(Task.Priority priority) {
        return backlogItems.stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
    }

    public List<Task> getOverdueTasks() {
        LocalDate today = LocalDate.now();
        return backlogItems.stream()
                .filter(task -> task.getDueDate() != null && task.getDueDate().isBefore(today))
                .collect(Collectors.toList());
    }

    public List<Task> getDueWithinDays(int days) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);
        return backlogItems.stream()
                .filter(task -> task.getDueDate() != null && 
                        !task.getDueDate().isBefore(today) && 
                        !task.getDueDate().isAfter(futureDate))
                .collect(Collectors.toList());
    }

    public List<Task> getTasksAssignedTo(String studentName) {
        return backlogItems.stream()
                .filter(task -> task.getAssignedTo().equals(studentName))
                .collect(Collectors.toList());
    }

    public List<Task> getUnassignedTasks() {
        return backlogItems.stream()
                .filter(task -> "Unassigned".equals(task.getAssignedTo()))
                .collect(Collectors.toList());
    }

    // Statistics
    public Map<Task.Priority, Integer> getPriorityDistribution() {
        Map<Task.Priority, Integer> distribution = new EnumMap<>(Task.Priority.class);
        for (Task.Priority priority : Task.Priority.values()) {
            distribution.put(priority, (int) backlogItems.stream()
                    .filter(task -> task.getPriority() == priority)
                    .count());
        }
        return distribution;
    }

    public Map<Task.Status, Integer> getStatusDistribution() {
        Map<Task.Status, Integer> distribution = new EnumMap<>(Task.Status.class);
        for (Task.Status status : Task.Status.values()) {
            distribution.put(status, (int) backlogItems.stream()
                    .filter(task -> task.getStatus() == status)
                    .count());
        }
        return distribution;
    }

    // Clear backlog
    public void clear() {
        backlogItems.clear();
        nextTaskId = 1;
    }

    @Override
    public String toString() {
        return String.format("ProductBacklog{" +
                "ID='%s', TotalItems=%d, TotalStoryPoints=%d}",
                backlogId, getTotalBacklogItems(), getTotalStoryPoints());
    }
}

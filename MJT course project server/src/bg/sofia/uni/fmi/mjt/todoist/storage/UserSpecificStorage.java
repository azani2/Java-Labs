package bg.sofia.uni.fmi.mjt.todoist.storage;

import bg.sofia.uni.fmi.mjt.todoist.task.Task;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserSpecificStorage {
    private Set<Task> inbox;
    private ConcurrentHashMap<LocalDate, Set<Task>> schedule;
    private Set<Task> completed;

    private static final String COMPLETION_PREFIX = "COMPLETED ";
    private static final int COMPLETION_PREFIX_LEN = 10;

    public UserSpecificStorage() {
        this.inbox = Collections.synchronizedSet(new HashSet<>());
        this.schedule = new ConcurrentHashMap<>();
        this.completed = new HashSet<>();
    }

    public UserSpecificStorage(Set<Task> tasks) {
        completed = new HashSet<>();
        initTasks(tasks);
    }

    private void initTasks(Collection<Task> tasks) {
        if (tasks == null) {
            throw new IllegalArgumentException();
        }

        inbox = Collections.synchronizedSet(new HashSet<>());
        schedule = new ConcurrentHashMap<>();

        for (Task t : tasks) {
            addTask(t);
        }
    }

    private void validateTask(Task task) {
        if (task == null || task.name() == null || task.name().isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    private boolean isCompleted(Task task) {
        validateTask(task);

        if (task.name().length() < COMPLETION_PREFIX_LEN + 1) {
            return false;
        }
        return task.name().startsWith(COMPLETION_PREFIX);
    }

    public boolean addTask(Task task) {
        validateTask(task);

        if (isCompleted(task)) {
            return completed.add(task);
        }

        if (task.date() != null) {
            if (schedule.containsKey(task.date())) {
                if (schedule.get(task.date()).contains(task)) {
                    return false;
                }
                schedule.get(task.date()).add(task);
            } else {
                Set<Task> currentDateTasks = Collections.synchronizedSet(new HashSet<>());
                currentDateTasks.add(task);
                schedule.put(task.date(), currentDateTasks);
            }
        } else {
            return inbox.add(task);
        }
        return true;
    }

    private boolean setContainsTask(Set<Task> set, Task task) {
        for (Task t : set) {
            if (t.name().equals(task.name())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasTask(Task task) {
        validateTask(task);

        if (isCompleted(task)) {
            return setContainsTask(completed, task);
        }

        if (task.date() == null) {
            if (setContainsTask(inbox, task)) {
                return true;
            }
        } else if (schedule.containsKey(task.date())) {
            Set<Task> taskSet = schedule.get(task.date());
            if (taskSet != null) {
                return setContainsTask(taskSet, task);
            }
        }

        for (var entry : schedule.entrySet()) {
            Set<Task> taskSet = entry.getValue();
            if (taskSet != null) {
                if (setContainsTask(taskSet, task)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Task updateTaskFields(Task toUpdate, Task pattern) {
        String newName = pattern.name();
        LocalDate newDate = (pattern.date() == null ? toUpdate.date() : pattern.date());
        LocalDate newDueDate = (pattern.dueDate() == null ? toUpdate.dueDate() : pattern.dueDate());
        String newDesc = (pattern.description() == null ? toUpdate.description() : pattern.description());
        return new Task(newName, newDate, newDueDate, newDesc);
    }

    public Task updateTask(Task task) {
        validateTask(task);

        for (Task t : inbox) {
            if (t.name().equals(task.name())) {
                deleteTask(t);
                Task newTask = updateTaskFields(t, task);
                addTask(newTask);
                return newTask;
            }
        }

        for (var entry : schedule.entrySet()) {
            Set<Task> taskSet = entry.getValue();
            if (taskSet != null) {
                for (Task t : taskSet) {
                    if (t.name().equals(task.name())) {
                        deleteTask(t);
                        Task newTask = updateTaskFields(t, task);
                        addTask(newTask);
                        return newTask;
                    }
                }
            }
        }
        return null;
    }

    public boolean deleteTask(Task task) {
        validateTask(task);

        if (task.date() == null) {
            for (Task t : inbox) {
                if (t.name().equals(task.name())) {
                    inbox.remove(t);
                    return true;
                }
            }
        } else if (schedule.containsKey(task.date())) {
            Set<Task> taskSet = schedule.get(task.date());
            if (taskSet != null && setContainsTask(taskSet, task)) {
                taskSet.remove(task);
                return true;
            }
        }

        for (var taskSet : schedule.entrySet()) {
            Set<Task> currSet = taskSet.getValue();
            if (currSet != null && setContainsTask(currSet, task)) {
                for (Task t : taskSet.getValue()) {
                    if (t.name().equals(task.name())) {
                        currSet.remove(t);
                        return  true;
                    }
                }
            }
        }

        for (Task t : completed) {
            if (t.name().equals(task.name())) {
                completed.remove(t);
                return true;
            }
        }
        return false;
    }

    public Task getTask(Task task) {
        validateTask(task);

        if (task.date() == null) {
            for (Task t : inbox) {
                if (t.name().equals(task.name())) {
                    return t;
                }
            }
        } else if (schedule.containsKey(task.date())) {
            Set<Task> taskSet = schedule.get(task.date());
            if (taskSet != null) {
                for (Task t : taskSet) {
                    if (t.name().equals(task.name())) {
                        return t;
                    }
                }
                return null;
            }
        }

        for (var entry : schedule.entrySet()) {
            Set<Task> taskSet = entry.getValue();
            if (taskSet != null) {
                for (Task t : taskSet) {
                    if (t.name().equals(task.name())) {
                        return t;
                    }
                }
            }
        }

        for (Task t : completed) {
            if (t.name().equals(task.name())) {
                return t;
            }
        }

        return null;
    }

    public Set<Task> getInbox() {
        return Collections.unmodifiableSet(inbox);
    }

    public Set<Task> getCompleted() {
        return Collections.unmodifiableSet(completed);
    }

    public boolean finishTask(Task task) {
        if (isCompleted(task) || !hasTask(task)) {
            return false;
        }

        Task actual = getTask(task);
        Task completedTask = new Task(COMPLETION_PREFIX + actual.name(),
                actual.date(), actual.dueDate(), actual.description());
        deleteTask(task);

        return completed.add(completedTask);
    }

    public Set<Task> getTasksOnDate(LocalDate date) {
        if (date == null || !schedule.containsKey(date)) {
            return null;
        }
        if (schedule.get(date) != null) {
            return Collections.unmodifiableSet(schedule.get(date));
        }
        return null;
    }
}

package bg.sofia.uni.fmi.mjt.todoist.storage;

import bg.sofia.uni.fmi.mjt.todoist.task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserSpecificStorageTest {
    private UserSpecificStorage storage;

    private Task datedTask = new Task("task 1", LocalDate.of(2020,2, 2),
                LocalDate.of(2021, 2, 2), "description 1");
    private Task completedTask = new Task("COMPLETED task", LocalDate.of(2020,2, 2),
            LocalDate.of(2021, 2, 2), "description 2");
    private Task nullDateTask = new Task("no date task", null, datedTask.dueDate(), datedTask.description());
    private Task includedTask = new Task("task 3", null, datedTask.dueDate(), datedTask.description());
    private Task includedDatedTask = new Task("task 2", LocalDate.of(2002, 2, 2),
                datedTask.dueDate(), datedTask.description());

    @BeforeEach
    public void setup() {
        storage = new UserSpecificStorage();
    }

    @Test
    public void testCreateWithNullSet() {
        assertThrows(IllegalArgumentException.class,
                () -> new UserSpecificStorage(null),
                "Expected to throw IllegalArgumentException when initializing storage with " +
                        "null tasks set argument, but was not thrown.");
    }

    @Test
    public void testAddTaskNull() {
        assertThrows(IllegalArgumentException.class,
                () -> storage.addTask(null),
                "Expected to throw IllegalArgumentException when adding a task with " +
                        "null task argument, but was not thrown.");
    }

    @Test
    public void testAddTaskEmptyName() {
        assertThrows(IllegalArgumentException.class,
                () -> storage.addTask(new Task("", null, null, "ok desc")),
                "Expected to throw IllegalArgumentException when adding a task with " +
                        "empty name, but was not thrown.");
    }

    @Test
    public void testAddTaskNullName() {
        assertThrows(IllegalArgumentException.class,
                () -> storage.addTask(new Task(null, null, null, "ok desc")),
                "Expected to throw IllegalArgumentException when adding a task with" +
                        " null name, but was not thrown.");
    }

    @Test
    public void testAddTaskCompletedNoOtherTasks() {
        assertDoesNotThrow(() -> storage.addTask(completedTask),
                "Did not expect to throw an exception when adding" +
                        "a completed task to empty storage, but threw.");
        setup();
        assertTrue(storage.addTask(completedTask),
                "Expected to return true when adding" +
                        "a completed task to empty storage, but was false.");
    }

    @Test
    public void testAddTaskNullDateNoOtherTasks() {
        assertDoesNotThrow(() -> storage.addTask(nullDateTask),
                "Did not expect to throw an exception when adding" +
                        "a task to empty storage, but threw.");
        setup();
        assertTrue(storage.addTask(nullDateTask),
                "Expected to return true when adding" +
                        "a task to empty storage, but was false.");
    }

    private Set<Task> initManyTasksSet() {
        Set<Task> taskSet = new HashSet<>();
        for (int i = 2; i <= 10; i++) {
            LocalDate date = null;
            if (i % 2 == 0) {
                date = LocalDate.of(2000+i, i, i);
            }
            taskSet.add(new Task("task " + i, date, datedTask.dueDate(), datedTask.description()));
        }
        return taskSet;
    }
    private void initWithManyTasks() {
        Set<Task> taskSet = initManyTasksSet();
        storage = new UserSpecificStorage(taskSet);
    }

    private void initWithManyTasksAndCompleted() {
        Set<Task> taskSet = initManyTasksSet();
        taskSet.add(completedTask);
        storage = new UserSpecificStorage(taskSet);
    }

    @Test
    public void testAddTaskCompletedHasOtherTasks() {
        initWithManyTasks();
        assertDoesNotThrow(() -> storage.addTask(completedTask),
                "Did not expect to throw an exception when adding" +
                        "a not-registered completed task, but threw.");
        initWithManyTasks();
        assertTrue(storage.addTask(completedTask),
                "Expected to return true when adding" +
                        "a not-registered completed task, but was false.");
    }

    @Test
    public void testAddTaskWithDateHasManyTasks() {
        initWithManyTasks();
        assertDoesNotThrow(() -> storage.addTask(datedTask),
                "Did not expect to throw an exception when adding" +
                        "a not-registered dated task, but threw.");
        initWithManyTasks();
        assertTrue(storage.addTask(datedTask),
                "Expected to return true when adding" +
                        "a not-registered dated task, but was false.");
    }

    @Test
    public void testAddTaskNullDateHasManyTasks() {
        initWithManyTasks();
        assertDoesNotThrow(() -> storage.addTask(nullDateTask),
                "Did not expect to throw an exception when adding" +
                        "a not-registered task, but threw.");

        initWithManyTasks();
        assertTrue(storage.addTask(nullDateTask),
                "Expected to return true when adding" +
                        "a not-registered task, but was false.");
    }

    @Test
    public void testAddTaskAlreadyAddedNullDateHasNoOtherTasks() {
        storage = new UserSpecificStorage(Set.of(nullDateTask));
        assertDoesNotThrow(() -> storage.addTask(nullDateTask),
                "Did not expect to throw an exception when adding" +
                        "a registered task, but threw.");
        assertFalse(storage.addTask(nullDateTask),
                "Expected to return false when adding" +
                        "a registered task, but was true.");
    }

    @Test
    public void testAddTaskAlreadyAddedNullDateHasOtherTasks() {
        initWithManyTasks();
        assertDoesNotThrow(() -> storage.addTask(includedTask),
                "Did not expect to throw an exception when adding" +
                        "a registered task, but threw.");
        assertFalse(storage.addTask(includedTask),
                "Expected to return false when adding" +
                        "a registered task, but was true.");
    }

    @Test
    public void testAddTaskAlreadyAddedWithDateHasOtherTasks() {
        initWithManyTasks();
        assertDoesNotThrow(() -> storage.addTask(includedDatedTask),
                "Did not expect to throw an exception when adding" +
                        "a registered dated task, but threw.");
        assertFalse(storage.addTask(includedDatedTask),
                "Expected to return false when adding" +
                        "a registered dated task, but was true.");
    }

    @Test
    public void testAddTaskWithDateHasOtherTasksWithSameDate() {
        initWithManyTasks();
        LocalDate date = LocalDate.of(2002, 2, 2);
        Task newTask = new Task("unique task", date, null, "something");

        assertDoesNotThrow(() -> storage.addTask(newTask),
                "Did not expect to throw an exception when adding" +
                        "a not-registered dated task, but threw.");
        initWithManyTasks();

        assertTrue(storage.addTask(newTask),
                "Expected to return true when adding" +
                        "a not-registered dated task, but was false.");
    }

    @Test
    public void testAddTaskWithDateHasLongDescriptionAdded() {
        initWithManyTasks();
        LocalDate date = LocalDate.of(2002, 2, 2);
        Task newTask = new Task("task 2", date, null,
                "something Something, Something/ Something? ;;; [[] }{ 2433");

        assertDoesNotThrow(() -> storage.addTask(newTask),
                "Did not expect to throw an exception when adding" +
                        "a registered dated task with a long description, but threw.");
        assertFalse(storage.addTask(newTask),
                "Expected to return false when adding" +
                        "a registered dated task with a long description, but was true.");
    }

    @Test
    public void testAddTaskWithDateHasLongDescriptionNotAdded() {
        initWithManyTasks();
        LocalDate date = LocalDate.of(2002, 2, 2);
        Task newTask = new Task("unique task", date, null,
                "something Something, Something/ Something? ;;; []] } 3451");

        assertDoesNotThrow(() -> storage.addTask(newTask),
                "Did not expect to throw an exception when adding" +
                        "a not-registered dated task with a long description, but threw.");
        initWithManyTasks();

        assertTrue(storage.addTask(newTask),
                "Expected to return true when adding" +
                        "a not-registered dated task with a long description, but was false.");
    }

    @Test
    public void testHasTaskDoesNotHaveAnyTasksNullDate() {
        assertDoesNotThrow(() -> storage.hasTask(nullDateTask),
                "Did not expect to throw an exception when checking if empty storage has" +
                        "a task, but threw.");
        setup();
        assertFalse(storage.hasTask(nullDateTask),
                "Expected to return true when checking if empty storage has" +
                        "a dated task, but was false.");
    }

    @Test
    public void testHasTaskWithDateDoesNotHaveAnyTasks() {
        assertDoesNotThrow(() -> storage.hasTask(datedTask),
                "Did not expect to throw an exception when checking if storage has" +
                        "a task with registered date with no tasks in storage, but threw.");
        setup();
        assertFalse(storage.hasTask(datedTask),
                "Expected to return false when checking if storage has" +
                        "a task with registered date with no tasks in storage, but was true.");
    }

    @Test
    public void testHasTaskWithNullDateHasManyOtherTasksFalse() {
        initWithManyTasks();
        assertDoesNotThrow(() -> storage.hasTask(nullDateTask),
                "Did not expect to throw an exception when checking if storage has" +
                        "a not-registered task, but threw.");
        initWithManyTasks();
        assertFalse(storage.hasTask(nullDateTask),
                "Expected to return false when checking if storage has" +
                        "a not-registered task, but was true.");
    }

    @Test
    public void testHasTaskNullDateHasManyOtherTasksTrue() {
        initWithManyTasks();
        assertDoesNotThrow(() -> storage.hasTask(includedTask),
                "Did not expect to throw an exception when checking if storage has" +
                        "a registered task, but threw.");
        initWithManyTasks();
        assertTrue(storage.hasTask(includedTask),
                "Expected to return true when checking if storage has" +
                        "a registered task, but was false.");
    }

    @Test
    public void testHasTaskWithDateHasManyOtherTasksTrue() {
        initWithManyTasks();
        assertDoesNotThrow(() -> storage.hasTask(includedDatedTask),
                "Did not expect to throw an exception when checking if storage has" +
                        "a registered task, but threw.");

        initWithManyTasks();
        assertTrue(storage.hasTask(includedDatedTask),
                "Expected to return true when checking if storage has" +
                        "a registered task, but was false.");
    }

    @Test
    public void testHasTaskCompletedHasManyOtherTasksTrue() {
        initWithManyTasksAndCompleted();
        assertDoesNotThrow(() -> storage.hasTask(completedTask),
                "Did not expect to throw an exception when checking if storage has" +
                        "a registered task, but threw.");
        assertTrue(storage.hasTask(completedTask),
                "Expected to return true when checking if storage has" +
                        "registered completed task.");
    }

    @Test
    public void testHasTaskNullDateButActualTaskHasDate() {
        initWithManyTasks();
        Task toCheck = new Task("task 2", null, null, null);
        assertDoesNotThrow(() -> storage.hasTask(toCheck));
        assertTrue(storage.hasTask(toCheck),
                "Expected to return true when checking if storage has" +
                        "registered task without the date argument.");
    }

    @Test
    public void testUpdateTaskNullTask() {
        assertThrows(IllegalArgumentException.class,
                () -> storage.updateTask(null),
                "Expected to throw IllegalArgumentException when updating task with" +
                        "task argument null, but was not thrown.");
    }

    @Test
    public void testUpdateTaskDateStorageEmpty() {
        assertNull(storage.updateTask(datedTask),
                "Expected to return null when updating a task from empty storage," +
                        "but was not null.");
    }

    @Test
    public void testUpdateTaskDateNotInStorageTaskHasManyTasks() {
        initWithManyTasks();
        assertNull(storage.updateTask(datedTask),
                "Expected to return null when updating a task not in the storage," +
                        "but was not null.");
    }

    private void assertTasksAreTheSame(Task expected, Task actual) {
        assertEquals(expected.name(), actual.name(),
                String.format("Expected task name was %s, but actual was %s", expected.name(), actual.name()));

        assertEquals(expected.date(), actual.date(),
                String.format("Expected task date was %s, but actual was %s", expected.date(), actual.date()));

        assertEquals(expected.dueDate(), actual.dueDate(),
                String.format("Expected task due date was %s, but actual was %s",
                        expected.dueDate(), actual.dueDate()));

        assertEquals(expected.description(), actual.description(),
                String.format("Expected task description was %s, but actual was %s",
                        expected.description(), actual.description()));

    }

    @Test
    public void testUpdateTaskDateInStorageTask() {
        initWithManyTasks();
        LocalDate newDate = LocalDate.of(2024, 12, 12);
        Task toUpdate = new Task(includedTask.name(), newDate, null, null);
        Task expectedUpdated = new Task(includedTask.name(), newDate,
                includedTask.dueDate(), includedTask.description());

        assertDoesNotThrow(() -> storage.updateTask(toUpdate),
                "Did not expect to throw an exception when updating date of" +
                        "a registered task, but threw.");
        initWithManyTasks();

        assertNotNull(storage.updateTask(toUpdate),
                "Did not expect returned task to be null when updating date of" +
                        "a registered task, but was null.");
        Task actual = storage.updateTask(toUpdate);

        assertTasksAreTheSame(expectedUpdated, actual);
        }

    @Test
    public void testUpdateTaskDueDateInStorageTask() {
        initWithManyTasks();
        LocalDate newDate = LocalDate.of(2024, 12, 12);

        Task toUpdate = new Task(includedTask.name(), null, newDate, null);
        Task expectedUpdated = new Task(includedTask.name(), includedTask.date(),
                newDate, includedTask.description());

        assertDoesNotThrow(() -> storage.updateTask(toUpdate),
                "Did not expect to throw an exception when updating due date of" +
                        "a registered task, but threw.");
        initWithManyTasks();

        assertNotNull(storage.updateTask(toUpdate),
                "Did not expect returned task to be null when updating due date of" +
                        "a registered task, but was null.");
        Task actual = storage.updateTask(toUpdate);

        assertTasksAreTheSame(expectedUpdated, actual);
        }

    @Test
    public void testUpdateTaskDescriptionInStorageTask() {
        initWithManyTasks();

        Task toUpdate = new Task(includedTask.name(), null, null, "new Description");
        Task expectedUpdated = new Task(includedTask.name(), includedTask.date(),
                includedTask.dueDate(), "new Description");

        assertDoesNotThrow(() -> storage.updateTask(toUpdate),
                "Did not expect to throw an exception when updating description of" +
                        "a registered task, but threw.");
        initWithManyTasks();

        assertNotNull(storage.updateTask(toUpdate),
                "Did not expect returned task to be null when updating description of" +
                        "a registered task, but was null.");
        Task actual = storage.updateTask(toUpdate);

        assertTasksAreTheSame(expectedUpdated, actual);
    }

    @Test
    public void testUpdateTaskDateAndDueDateInStorageTask() {
        initWithManyTasks();
        LocalDate newDate = LocalDate.of(2024, 11, 12);
        LocalDate newDueDate = LocalDate.of(2024, 12, 12);

        Task toUpdate = new Task(includedTask.name(), newDate, newDueDate, null);
        Task expectedUpdated = new Task(includedTask.name(), newDate,
                newDueDate, includedTask.description());

        assertDoesNotThrow(() -> storage.updateTask(toUpdate),
                "Did not expect to throw an exception when updating 2 fields of" +
                        "a registered task, but threw.");
        initWithManyTasks();

        assertNotNull(storage.updateTask(toUpdate),
                "Did not expect returned task to be null when updating 2 fields of" +
                        "a registered task, but was null.");

        Task actual = storage.updateTask(toUpdate);
        assertTasksAreTheSame(expectedUpdated, actual);
    }

    @Test
    public void testUpdateTaskDateAndDescriptionInStorageTask() {
        initWithManyTasks();

        LocalDate newDate = LocalDate.of(2024, 11, 12);
        Task toUpdate = new Task(includedTask.name(), newDate, null, "new Description");
        Task expectedUpdated = new Task(includedTask.name(), newDate,
                includedTask.dueDate(), "new Description");

        assertDoesNotThrow(() -> storage.updateTask(toUpdate),
                "Did not expect to throw an exception when updating 2 fields of" +
                        "a registered task, but threw.");
        initWithManyTasks();

        assertNotNull(storage.updateTask(toUpdate),
                "Did not expect returned task to be null when updating 2 fields of" +
                        "a registered task, but was null.");

        Task actual = storage.updateTask(toUpdate);
        assertTasksAreTheSame(expectedUpdated, actual);
    }

    @Test
    public void testUpdateTaskDueDateAndDescriptionInStorageTask() {
        initWithManyTasks();

        LocalDate newDueDate = LocalDate.of(2024, 11, 12);
        Task toUpdate = new Task(includedTask.name(), includedTask.date(), newDueDate, "new Description");
        Task expectedUpdated = new Task(includedTask.name(), includedTask.date(),
                newDueDate, "new Description");

        assertDoesNotThrow(() -> storage.updateTask(toUpdate),
                "Did not expect to throw an exception when updating 2 fields of" +
                        "a registered task, but threw.");
        initWithManyTasks();

        assertNotNull(storage.updateTask(toUpdate),
                "Did not expect returned task to be null when updating 2 fields of" +
                        "a registered task, but was null.");

        Task actual = storage.updateTask(toUpdate);
        assertTasksAreTheSame(expectedUpdated, actual);
    }

    @Test
    public void testUpdateTaskAllFieldsInStorageTask() {
        initWithManyTasks();

        LocalDate newDate = LocalDate.of(2024, 11, 12);
        LocalDate newDueDate = LocalDate.of(2024, 12, 12);
        Task toUpdate = new Task(includedTask.name(), newDate, newDueDate, "new Description");
        Task expectedUpdated = new Task(includedTask.name(), newDate,
                newDueDate, "new Description");

        assertDoesNotThrow(() -> storage.updateTask(toUpdate),
                "Did not expect to throw an exception when updating all fields of" +
                        "a registered task, but threw.");
        initWithManyTasks();

        assertNotNull(storage.updateTask(toUpdate),
                "Did not expect returned task to be null when updating all fields of" +
                        "a registered task, but was null.");

        Task actual = storage.updateTask(toUpdate);
        assertTasksAreTheSame(expectedUpdated, actual);
    }

    @Test
    void testDeleteTaskNullTask() {
        assertThrows(IllegalArgumentException.class,
                () -> storage.deleteTask(null),
                "Expected to throw IllegalArgumentException when deleting task with" +
                        "null argument, but was not thrown.");
    }

    @Test
    void testDeleteTaskEmptyStorage() {
        assertDoesNotThrow(() -> storage.deleteTask(datedTask),
                "Did not expect to throw an exception when deleting a " +
                        "task from empty storage, but was thrown.");
        assertFalse(storage.deleteTask(datedTask),
                "Expected to return false when deleting task from empty storage, but was true.");
    }

    @Test
    void testDeleteTaskNotInStorageHasManyTasks() {
        initWithManyTasks();
        assertDoesNotThrow(() -> storage.deleteTask(datedTask),
                "Did not expect to throw an exception when attempting to delete a not-registered " +
                        "dated task, but was thrown.");

        initWithManyTasks();
        assertFalse(storage.deleteTask(datedTask),
                "Expected to return false when deleting not-registered task, but was true.");
    }

    @Test
    public void testDeleteTaskNullDateInStorageHasManyTasks() {
        initWithManyTasks();
        assertDoesNotThrow(() -> storage.deleteTask(includedTask),
                "Did not expect to throw an exception when deleting a registered " +
                        "undated task, but was thrown.");

        initWithManyTasks();
        assertTrue(storage.deleteTask(includedTask),
                "Expected to return true when deleting undated task, but was false.");
    }

    @Test
    public void testDeleteTaskWithDateInStorageHasManyTasks() {
        initWithManyTasks();
        assertDoesNotThrow(() -> storage.deleteTask(includedDatedTask),
                "Did not expect to throw an exception when deleting a registered " +
                        "dated task, but was thrown.");

        initWithManyTasks();
        assertTrue(storage.deleteTask(includedDatedTask),
                "Expected to return true when deleting dated task, but was false.");
    }

    @Test
    public void testDeleteTaskNullDateActualHasDateWithManyTasks() {
        initWithManyTasks();
        Task withoutDate = new Task(includedDatedTask.name(), null, null, null);

        assertDoesNotThrow(() -> storage.deleteTask(withoutDate),
                "Did not expect to throw an exception when deleting a registered " +
                        "dated task without date argument, but was thrown.");

        initWithManyTasks();
        assertTrue(storage.deleteTask(withoutDate),
                "Expected to return true when deleting dated task" +
                        "without date argument, but was false.");
    }

    @Test
    public void testDeleteTaskCompletedWithManyTasks() {
        initWithManyTasksAndCompleted();
        assertDoesNotThrow(() -> storage.deleteTask(completedTask),
                "Did not expect to throw an exception when deleting a registered " +
                        "completed task, but was thrown.");

        initWithManyTasksAndCompleted();
        assertTrue(storage.deleteTask(completedTask),
                "Expected to return true when deleting completed task, but was false.");
    }

    @Test
    public void testGetTaskNullTask() {
        assertThrows(IllegalArgumentException.class,
                () -> storage.getTask(null),
                "Expected to throw IllegalArgumentException when getting task" +
                        "with null task argument, but was not thrown.");
    }

    @Test
    public void testGetTaskWithDateEmptyStorage() {
        assertDoesNotThrow(() -> storage.getTask(datedTask),
                "Did not expect to throw an exception when getting task " +
                        "from empty storage, storage, but threw.");
        assertNull(storage.getTask(datedTask),
                "Expected returned task to be null when getting task " +
                        "from empty storage, but was not null.");
    }

    @Test
    public void testGetTaskNullDateEmptyStorage() {
        assertDoesNotThrow(() -> storage.getTask(nullDateTask),
                "Did not expect to throw an exception when getting task " +
                        "from empty storage, storage, but threw.");
        assertNull(storage.getTask(nullDateTask),
                "Expected returned task to be null when getting task " +
                        "from empty storage, but was not null.");
    }

    @Test
    public void testGetTaskCompletedEmptyStorage() {
        assertDoesNotThrow(() -> storage.getTask(completedTask),
                "Did not expect to throw an exception when getting completed task " +
                        "from empty storage, but threw.");
        assertNull(storage.getTask(completedTask),
                "Expected returned task to be null when getting task " +
                        "from empty storage, but was not null.");
    }

    @Test
    public void testGetTaskNullDateNotInStorageHasManyTasks() {
        initWithManyTasks();
        assertDoesNotThrow(() -> storage.getTask(nullDateTask),
                "Did not expect to throw an exception when getting task " +
                        "with not-registered task name, but threw.");
        assertNull(storage.getTask(nullDateTask),
                "Expected returned task to be null when getting task " +
                        "with not-registered task name, but was not null.");
    }

    @Test
    public void testGetTaskNullDateInStorageHasManyTasks() {
        initWithManyTasks();
        assertDoesNotThrow(() -> storage.getTask(includedTask),
                "Did not expect to throw an exception when getting task " +
                        "with registered task name, but threw.");
        assertNotNull(storage.getTask(includedTask),
                "Dis not expect returned task to be null when getting task " +
                        "with registered task name, but was null.");
        assertTasksAreTheSame(includedTask, storage.getTask(includedTask));
    }

    @Test
    public void testGetTaskWithDateInStorageHasManyTasks() {
        initWithManyTasks();
        assertDoesNotThrow(() -> storage.getTask(includedDatedTask),
                "Did not expect to throw an exception when getting task " +
                        "with registered task name, but threw.");
        assertNotNull(storage.getTask(includedDatedTask),
                "Dis not expect returned task to be null when getting task " +
                        "with registered task name, but was null.");
        assertTasksAreTheSame(includedDatedTask, storage.getTask(includedDatedTask));
    }

    @Test
    public void testGetTaskCompletedNotInStorageHasManyTasks() {
        initWithManyTasks();
        assertDoesNotThrow(() -> storage.getTask(completedTask),
                "Did not expect to throw an exception when getting completed task " +
                        "with not-registered task name, but threw.");
        assertNull(storage.getTask(completedTask),
                "Expected returned task to be null when getting completed task " +
                        "with not-registered task name, but was not null.");
    }

    @Test
    public void testGetTaskCompletedInStorageHasManyTasks() {
        initWithManyTasksAndCompleted();

        assertDoesNotThrow(() -> storage.getTask(completedTask),
                "Did not expect to throw an exception when getting completed task " +
                        "with registered task name.");
        assertNotNull(storage.getTask(completedTask),
                "Dis not expect returned task to be null when getting completed task " +
                        "with registered task name, but was null.");
        assertTasksAreTheSame(completedTask, storage.getTask(completedTask));
    }

    @Test
    public void testGetTaskWithDateNotInStorageHasThisDateAndHasManyTasks() {
        initWithManyTasks();
        Task notIncludedDated = new Task("not included task", includedDatedTask.date(),
                null, null);

        assertDoesNotThrow(() -> storage.getTask(notIncludedDated),
                "Did not expect to throw an exception when getting tasks on" +
                        "not-registered date, but threw.");
        assertNull(storage.getTask(notIncludedDated),
                "Expected to return null when getting tasks with not-registered date," +
                        "but was not null.");
    }

    @Test
    public void testGetTaskWithDateInStorageButActualHasDateHasManyTasks() {
        initWithManyTasks();
        Task includedDated = new Task(includedDatedTask.name(), null, null, null);

        assertDoesNotThrow(() -> storage.getTask(includedDated),
                "Did not expect to throw an exception when getting tasks with s date" +
                        "that has tasks in the storage, but threw.");
        assertNotNull(storage.getTask(includedDated),
                "Returned collection was not expected to be null when " +
                        "storage has tasks with this date, but was null.");
        assertTasksAreTheSame(includedDatedTask, storage.getTask(includedDated));
    }

    @Test
    public void testGetInboxEmptyStorage() {
        assertDoesNotThrow(() -> storage.getInbox(),
                "Did not expect to throw an exception when getting inbox from empty storage" +
                        ", but threw.");

        assertNotNull(storage.getInbox(),
                "Returned collection was not expected to be null when " +
                        "storage is empty, but was null.");

        Set<Task> actual = storage.getInbox();
        assertEquals(0, actual.size(),
                String.format("Actual set size was %d, but expected set size was 0", actual.size()));
    }

    @Test
    public void testGetCompletedEmptyStorage() {
        assertDoesNotThrow(() -> storage.getCompleted(),
                "Did not expect to throw an exception when getting completed tasks from " +
                        "empty storage, but threw.");
        assertNotNull(storage.getCompleted(),
                "Returned collection was not expected to be null when storage is empty" +
                        ", but was null.");

        Set<Task> actual = storage.getCompleted();
        assertEquals(0, actual.size(),
                String.format("Actual set size was %d, but expected set size was 0", actual.size()));
    }

    private void assertSetsAreTheSame(Set<Task> expected, Set<Task> actual) {
        assertEquals(expected.size(), actual.size(),
                String.format("Actual set size was %d, but expected set size was %d", expected.size(), actual.size()));
        assertTrue(actual.containsAll(expected),
                "Actual set does not contain all elements of expected.");
    }

    @Test
    public void testGetInboxHasManyTasks() {
        Set<Task> manyTasks = initManyTasksSet();
        Set<Task> expectedInbox = new HashSet<>();

        for (Task t : manyTasks) {
            if (t.date() == null) {
                expectedInbox.add(t);
            }
        }
        initWithManyTasks();

        assertDoesNotThrow(() -> storage.getInbox(),
                "Did not expect to throw an exception when getting inbox from storage" +
                        "with not-empty inbox, but threw.");
        assertNotNull(storage.getInbox(),
                "Returned collection was not expected to be null when inbox is not empty" +
                        ", but was null.");

        Set<Task> actual = storage.getInbox();

        assertThrows(UnsupportedOperationException.class,
                () -> actual.add(nullDateTask),
                "Returned collection was expected to be unmodifiable and throw" +
                        "UnsupportedOperationException on attempt to add, but did not throw.");

        assertSetsAreTheSame(expectedInbox, actual);
    }

    @Test
    public void testGetCompletedHasManyCompleted() {
        Set<Task> manyTasks = initManyTasksSet();
        Set<Task> manyCompletedTasks = new HashSet<>();
        Set<Task> expectedCompleted = new HashSet<>();

        for (Task t : manyTasks) {
            if (t.date() == null) {
                Task compTask = new Task("COMPLETED " + t.name() + " but different",
                        null, t.dueDate(), t.description());
                expectedCompleted.add(compTask);
                manyCompletedTasks.add(compTask);
            }
        }
        manyTasks.addAll(manyCompletedTasks);
        storage = new UserSpecificStorage(manyTasks);

        assertDoesNotThrow(() -> storage.getCompleted(),
                "Did not expect to throw an exception when getting completed tasks from storage" +
                        "with many completed tasks, but threw.");
        assertNotNull(storage.getCompleted(),
                "Returned collection was not expected to be null when there are completed" +
                        "tasks in the storage, but was null.");

        Set<Task> actual = storage.getCompleted();

        assertThrows(UnsupportedOperationException.class,
                () -> actual.add(datedTask),
                "Returned collection was expected to be unmodifiable and throw" +
                        "UnsupportedOperationException on attempt to add, but did not throw.");

        assertSetsAreTheSame(expectedCompleted, actual);
    }

    @Test
    public void testFinishTaskNullTask() {
        assertThrows(IllegalArgumentException.class,
                () -> storage.finishTask(null),
                "Expected to throw IllegalArgumentException when finishing a task when argument" +
                        " task is null, but was not thrown.");
    }

    @Test
    public void testFinishTaskWithDateEmptyStorage() {
        assertFalse(storage.finishTask(datedTask),
                "Expected to return false when finishing a task when there are no tasks in storage, " +
                        "but was true instead.");
    }

    @Test
    public void testFinishTaskNullDateEmptyStorage() {
        assertFalse(storage.finishTask(nullDateTask),
                "Expected to return false when finishing a task when there are no tasks in storage, " +
                        "but was true instead.");
    }

    @Test
    public void testFinishTaskCompletedTask() {
        assertFalse(storage.finishTask(completedTask),
                "Expected to return false when finishing an already completed task, " +
                        "but was true instead.");
    }

    @Test
    public void testFinishTaskWithDateNotInStorageButHasThisDate() {
        initWithManyTasks();
        Task notIncludedDated = new Task(includedDatedTask.name() + " but different",
                includedDatedTask.date(), null, null);
        assertFalse(storage.finishTask(notIncludedDated),
                "Expected to return false when finishing a task not included in the storage, " +
                        "but was true instead.");
    }

    @Test
    public void testFinishTaskNullDateNotInStorage() {
        initWithManyTasks();
        Task notIncludedNotDated = new Task(includedTask.name() + " but different",
                null, null, null);
        assertFalse(storage.finishTask(notIncludedNotDated),
                "Expected to return false when finishing a task not included in the storage, " +
                        "but was true instead.");
    }

    @Test
    public void testFinishTaskWithDateInStorage() {
        initWithManyTasks();
        assertTrue(storage.finishTask(includedDatedTask),
                "Expected to return true when finishing task that is in the storage, but was false.");
    }

    @Test
    public void testFinishTaskNullDateInStorage() {
        initWithManyTasks();
        assertTrue(storage.finishTask(includedTask),
                "Expected to return true when finishing task that is in the storage, but was false.");
    }

    @Test
    public void testGetTasksOnDateNullDate() {
        assertNull(storage.getTasksOnDate(null),
                "Expected to return null when getting tasks with null date, but was not null.");
    }

    @Test
    public void testGetTasksOnDateNoTasksOnDateHasManyTasks() {
        initWithManyTasks();
        LocalDate notPresentDate = LocalDate.of(2026, 6, 6);
        assertNull(storage.getTasksOnDate(notPresentDate));
    }

    @Test
    public void testGetTasksOnDateHasNoTasks() {
        assertNull(storage.getTasksOnDate(includedDatedTask.date()));
    }

    @Test
    public void testGetTasksOnDateHasDateButHasNoTasks() {
        initWithManyTasks();
        storage.deleteTask(includedDatedTask);

        assertDoesNotThrow(() -> storage.getTasksOnDate(includedDatedTask.date()));

        assertNotNull(storage.getTasksOnDate(includedDatedTask.date()),
                "Returned collection was not expected to be null, but was null.");

        Set<Task> emptySet = new HashSet<>();
        Set<Task> actual = storage.getTasksOnDate(includedDatedTask.date());

        assertSetsAreTheSame(emptySet, actual);
        assertThrows(UnsupportedOperationException.class,
                () -> actual.add(nullDateTask),
                "Returned collection was expected to be unmodifiable and throw" +
                        "UnsupportedOperationException on attempt to add, but did not throw.");
    }
}

package bg.sofia.uni.fmi.mjt.todoist.command;

import bg.sofia.uni.fmi.mjt.todoist.storage.CollabRepository;
import bg.sofia.uni.fmi.mjt.todoist.storage.TaskRepository;
import bg.sofia.uni.fmi.mjt.todoist.storage.UsersRepository;
import bg.sofia.uni.fmi.mjt.todoist.task.Task;
import bg.sofia.uni.fmi.mjt.todoist.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CommandExecutorTest {
    private UsersRepository usersRepository;
    private TaskRepository taskRepository;
    private CollabRepository collabRepository;
    private CommandExecutor cmdExecutor;
    private final UserId workingUser = new UserId("user");
    private final String TASK_NAME_EMPTY_MESSAGE = "Task name cannot be empty!" + System.lineSeparator();

    private final Task task = new Task("task",
            null,
            LocalDate.of(2020, 1, 1),
            "some desc");

    private final Task datedTask = new Task("dated task",
            LocalDate.of(2001, 1, 1),
            LocalDate.of(2020, 1, 1),
            "some desc");

    private final String taskStr = "Task name: \"task\"" + System.lineSeparator() +
            "Date: none" + System.lineSeparator() +
            "Due date: 01 януари 2020" + System.lineSeparator() +
            "Description \"some desc\"" + System.lineSeparator() +
            "--------" + System.lineSeparator();

    private final String datedTaskStr = "Task name: \"dated task\"" + System.lineSeparator() +
            "Date: 01 януари 2001" + System.lineSeparator() +
            "Due date: 01 януари 2020" + System.lineSeparator() +
            "Description \"some desc\"" + System.lineSeparator() +
            "--------" + System.lineSeparator();
    private final Command login = new Command(workingUser.username(),
            "login",
            new String[] {"user", "password"});

    private final Command register = new Command(workingUser.username(),
            "register",
            new String[] {"user", "password"});
    private final Command addTask = new Command(workingUser.username(),
            "add-task",
            new String[] {"--name=\"Some task\""});

    private final Command update = new Command(workingUser.username(),
            "update-task",
            new String[] {"--name=\"Some task\""});

    private final Command get = new Command(workingUser.username(),
            "get-task",
            new String[] {"--name=\"task\""});

    private final Command getWithDate = new Command(workingUser.username(),
            "get-task",
            new String[] {"--name=\"dated task\"", "--date=1/1/2001"});

    private final Command list = new Command(workingUser.username(),
            "list-tasks",
            new String[0]);

    private final Command listCompleted = new Command(workingUser.username(),
            "list-tasks",
            new String[] {"--completed"});

    private final Command listForDate = new Command(workingUser.username(),
            "list-tasks",
            new String[] {"--date=01/01/2001"});

    @BeforeEach
    public void setup() {
        usersRepository = mock(UsersRepository.class);
        taskRepository = mock(TaskRepository.class);
        collabRepository = mock(CollabRepository.class);
        cmdExecutor = new CommandExecutor(usersRepository, taskRepository, collabRepository);
    }

    @Test
    public void testLoginValid() {
        when(usersRepository.hasUserWith("user", "password")).thenReturn(true);

        String expected = "Login successful. Welcome to Todoist!";
        String actual = cmdExecutor.execute(login);

        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testLoginInvalid() {
        when(usersRepository.hasUserWith("user", "password")).thenReturn(false);

        String expected = "401 User does not exist!";
        String actual = cmdExecutor.execute(login);

        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testLoginWithLessThan2Arguments() {
        when(taskRepository.addTask(any(), any())).thenReturn(true);

        String expected = "Invalid count of arguments: \"login\" expects 2 arguments. Example: \"login johnny johnny1\"";
        Command invalidLogin = new Command(workingUser.username(),
                "login",
                new String[] {"user"});

        String actual = cmdExecutor.execute(invalidLogin);

        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testRegisterValid() {
        when(usersRepository.userExists("user")).thenReturn(false, true);
        doNothing().when(usersRepository).addUser(any());

        String expected = "Registration successful. Welcome to Todoist!";
        String actual = cmdExecutor.execute(register);

        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testRegisterValidFail() {
        when(usersRepository.userExists("user")).thenReturn(false, false);
        doNothing().when(usersRepository).addUser(any());

        String expected = "Something went wrong." + System.lineSeparator();
        String actual = cmdExecutor.execute(register);

        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testRegisterInvalid() {
        when(usersRepository.userExists("user")).thenReturn(true);

        String expected = "401 User already exists!";
        String actual = cmdExecutor.execute(register);

        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testRegisterWithLessThan2Arguments() {
        when(taskRepository.addTask(any(), any())).thenReturn(true);

        String expected = "Invalid count of arguments: \"register\" expects 2 arguments. Example: \"register johnny johnny1\"";
        Command invalidRegister = new Command(workingUser.username(),
                "register",
                new String[] {"user"});

        String actual = cmdExecutor.execute(invalidRegister);

        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testAddTaskSuccess() {
        when(taskRepository.addTask(any(), any())).thenReturn(true);

        String expected = "Task added successfully." + System.lineSeparator();
        String actual = cmdExecutor.execute(addTask);

        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testAddTaskFail() {
        when(taskRepository.updateTask(any(), any())).thenReturn(false);

        String expected = "Failed to add task." + System.lineSeparator();
        String actual = cmdExecutor.execute(addTask);

        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testAddTaskNoName() {
        Command addTaskInvalid = new Command(workingUser.username(),
                "add-task",
                new String[0]);
        String expected = TASK_NAME_EMPTY_MESSAGE;
        String actual = cmdExecutor.execute(addTaskInvalid);

        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testUpdateTaskSuccess() {
        when(taskRepository.updateTask(any(), any())).thenReturn(true);

        String expected = "Updated task successfully." + System.lineSeparator();
        String actual = cmdExecutor.execute(update);

        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testUpdateTaskFail() {
        when(taskRepository.updateTask(any(), any())).thenReturn(false);

        String expected = "Failed to update task." + System.lineSeparator();
        String actual = cmdExecutor.execute(update);

        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testUpdateTaskNoName() {
        Command updTaskInvalid = new Command(workingUser.username(),
                "update-task",
                new String[0]);
        String expected = TASK_NAME_EMPTY_MESSAGE;
        String actual = cmdExecutor.execute(updTaskInvalid);

        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testGetUndatedTaskSuccess() {
        when(taskRepository.getTask(any(), any())).thenReturn(task);

        String expected = taskStr;
        String actual = cmdExecutor.execute(get);

        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testGetDatedTaskSuccess() {
        when(taskRepository.getTask(any(), any())).thenReturn(datedTask);

        String expected = datedTaskStr;
        String actual = cmdExecutor.execute(getWithDate);

        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testGetUndatedTaskFail() {
        when(taskRepository.getTask(any(), any())).thenReturn(null);

        String expected = "Failed to get task." + System.lineSeparator();
        String actual = cmdExecutor.execute(get);

        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testGetDatedTaskFail() {
        when(taskRepository.getTask(any(), any())).thenReturn(null);

        String expected = "Failed to get task." + System.lineSeparator();
        String actual = cmdExecutor.execute(getWithDate);

        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testGetTaskNoNameArg() {
        Command getTaskInvalid = new Command(workingUser.username(),
                "get-task",
                new String[0]);
        String expected = TASK_NAME_EMPTY_MESSAGE;
        String actual = cmdExecutor.execute(getTaskInvalid);

        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    private String parseDate(LocalDate date) {
        if (date == null) {
            return "none";
        }
        return date.format(DateTimeFormatter.ofPattern("dd LLLL yyyy"));
    }
    private String getTaskString(Task task) {
        String dateStr = parseDate(task.date());
        String dueDateStr = parseDate(task.dueDate());
        String description = task.description() == null || task.description().isEmpty()
                ? "none"
                : task.description();
        return String.format("Task name: \"%s\"" + System.lineSeparator() +
                        "Date: %s" + System.lineSeparator() +
                        "Due date: %s" + System.lineSeparator() +
                        "Description \"%s\"" + System.lineSeparator(),
                task.name(), dateStr, dueDateStr, description);
    }

    @Test
    public void testListTasksNoArgsSuccess() {
        Task task2 = new Task("task2", null, null, null);
        Set<Task> inbox = Set.of(task, task2);
        String task2Str = getTaskString(task2);

        when(taskRepository.getUserInbox(any())).thenReturn(inbox);

        String[] expected = new String[] {task2Str,
                taskStr.replace("--------" + System.lineSeparator(), "")};
        String actual = cmdExecutor.execute(list);
        String[] listedTasks = actual.split("--------" + System.lineSeparator());

        assertEquals(2, listedTasks.length,
                String.format("Expected to list 2 tasks, but listed %d.", listedTasks.length));

        assertTrue(List.of(listedTasks).containsAll(List.of(expected)),
                "Expected listed task strings do not match actual.");
    }

    @Test
    public void testListTasksNoArgsFail() {
        when(taskRepository.getUserInbox(any())).thenReturn(new HashSet<>());

        String expected = "No tasks to list." + System.lineSeparator();
        String actual = cmdExecutor.execute(list);

        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testListTasksCompletedSuccess() {
        Task task1 = new Task("COMPLETED task1", null, null, null);
        Task task2 = new Task("COMPLETED task2", null, null, null);
        Set<Task> completed = Set.of(task1, task2);
        String task1Str = getTaskString(task1);
        String task2Str = getTaskString(task2);

        when(taskRepository.getUserCompleted(any())).thenReturn(completed);

        String[] expected = new String[] {task2Str, task1Str};
        String actual = cmdExecutor.execute(listCompleted);
        String[] listedTasks = actual.split("--------" + System.lineSeparator());

        assertEquals(2, listedTasks.length,
                String.format("Expected to list 2 tasks, but listed %d.", listedTasks.length));

        assertTrue(List.of(listedTasks).containsAll(List.of(expected)),
                "Expected listed task strings do not match actual.");
    }

    @Test
    public void testListTasksCompletedFail() {
        when(taskRepository.getUserCompleted(any())).thenReturn(new HashSet<>());

        String expected = "No tasks to list." + System.lineSeparator();
        String actual = cmdExecutor.execute(listCompleted);

        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testListTasksMoreThanOneArg() {
        when(taskRepository.getUserCompleted(any())).thenReturn(new HashSet<>());

        String expected = "Unknown Command." + System.lineSeparator();
        Command invCmd = new Command(workingUser.username(),
                "list-tasks",
                new String[] {"some arg1", "some arg2", "some arg2"});
        String actual = cmdExecutor.execute(invCmd);

        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testListTasksShortArgument() {
        when(taskRepository.getUserCompleted(any())).thenReturn(new HashSet<>());

        String expected = "Unknown Command." + System.lineSeparator();
        Command invCmd = new Command(workingUser.username(),
                "list-tasks",
                new String[] {"argu1"});
        String actual = cmdExecutor.execute(invCmd);

        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testListTasksWithDateSuccess() {
        LocalDate date = LocalDate.of(2001, 1, 1);
        Task task1 = new Task("task1", date, null, null);
        Task task2 = new Task("task2", date, null, null);
        Set<Task> tasks = Set.of(task1, task2);
        String task1Str = getTaskString(task1);
        String task2Str = getTaskString(task2);

        when(taskRepository.getUserTasksOnDate(any(), any())).thenReturn(tasks);

        String[] expected = new String[] {task2Str, task1Str};
        String actual = cmdExecutor.execute(listForDate);
        String[] listedTasks = actual.split("--------" + System.lineSeparator());

        assertEquals(2, listedTasks.length,
                String.format("Expected to list 2 tasks, but listed %d.", listedTasks.length));

        assertTrue(List.of(listedTasks).containsAll(List.of(expected)),
                "Expected listed task strings do not match actual.");
    }

    @Test
    public void testDeleteTaskNoArgs() {
        Command invDel = new Command(workingUser.username(),
                "delete-task",
                new String[0]);
        String expected = TASK_NAME_EMPTY_MESSAGE;
        String actual = cmdExecutor.execute(invDel);
        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testDeleteTaskNoDateSuccess() {
        when(taskRepository.deleteTask(any(), any())).thenReturn(true);
        Command del = new Command(workingUser.username(),
                "delete-task",
                new String[] {"-name=\"task\""});
        String expected = "Task deleted successfully." + System.lineSeparator();
        String actual = cmdExecutor.execute(del);
        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testDeleteTaskWithDateSuccess() {
        when(taskRepository.deleteTask(any(), any())).thenReturn(true);
        Command del = new Command(workingUser.username(),
                "delete-task",
                new String[] {"-name=\"task\"", "--date=01/01/2001"});
        String expected = "Task deleted successfully." + System.lineSeparator();
        String actual = cmdExecutor.execute(del);
        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testDeleteTaskNoDateFail() {
        when(taskRepository.deleteTask(any(), any())).thenReturn(false);
        Command del = new Command(workingUser.username(),
                "delete-task",
                new String[] {"-name=\"task\""});
        String expected = "Failed to delete this task." + System.lineSeparator();
        String actual = cmdExecutor.execute(del);
        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testDeleteTaskWithDateFail() {
        when(taskRepository.deleteTask(any(), any())).thenReturn(false);
        Command del = new Command(workingUser.username(),
                "delete-task",
                new String[] {"-name=\"task\"", "--date=01/01/2001"});
        String expected = "Failed to delete this task." + System.lineSeparator();
        String actual = cmdExecutor.execute(del);
        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testFinishTaskNoArgs() {
        Command finish = new Command(workingUser.username(),
                "finish-task",
                new String[0]);
        String expected = TASK_NAME_EMPTY_MESSAGE;
        String actual = cmdExecutor.execute(finish);
        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testFinishTaskWithDateSuccess() {
        when(taskRepository.finishTask(any(), any())).thenReturn(true);
        Command finish = new Command(workingUser.username(),
                "finish-task",
                new String[] {"--name=\"dated task\"", "--date=1/1/2001"});
        String expected = "Task completed successfully." + System.lineSeparator();
        String actual = cmdExecutor.execute(finish);
        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testFinishTaskWithDateFail() {
        when(taskRepository.finishTask(any(), any())).thenReturn(false);
        Command finish = new Command(workingUser.username(),
                "finish-task",
                new String[] {"--name=\"dated task\"", "--date=1/1/2001"});
        String expected = "Failed to complete task." + System.lineSeparator();
        String actual = cmdExecutor.execute(finish);
        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testFinishTaskNoDateSuccess() {
        when(taskRepository.finishTask(any(), any())).thenReturn(true);
        Command finish = new Command(workingUser.username(),
                "finish-task",
                new String[] {"--name=\"dated task\""});
        String expected = "Task completed successfully." + System.lineSeparator();
        String actual = cmdExecutor.execute(finish);
        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }

    @Test
    public void testFinishTaskNoDateFail() {
        when(taskRepository.finishTask(any(), any())).thenReturn(false);
        Command finish = new Command(workingUser.username(),
                "finish-task",
                new String[] {"--name=\"dated task\""});
        String expected = "Failed to complete task." + System.lineSeparator();
        String actual = cmdExecutor.execute(finish);
        assertEquals(expected, actual,
                String.format("Expected command result was %s, but actual was %s", expected, actual));
    }
}

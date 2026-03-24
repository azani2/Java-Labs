package bg.sofia.uni.fmi.mjt.todoist.command;

import bg.sofia.uni.fmi.mjt.todoist.storage.CollabRepository;
import bg.sofia.uni.fmi.mjt.todoist.storage.TaskRepository;
import bg.sofia.uni.fmi.mjt.todoist.storage.UsersRepository;
import bg.sofia.uni.fmi.mjt.todoist.task.Collaboration;
import bg.sofia.uni.fmi.mjt.todoist.task.Task;
import bg.sofia.uni.fmi.mjt.todoist.user.User;
import bg.sofia.uni.fmi.mjt.todoist.user.UserId;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Set;

public class CommandExecutor {
    private static final String INVALID_COMMAND_MESSAGE = "Unknown Command." + System.lineSeparator();
    private static final String INVALID_LOGIN_MESSAGE = "401 User does not exist!";
    private static final String SUCCESSFUL_LOGIN_MESSAGE = "Login successful. Welcome to Todoist!";
    private static final String CREDENTIALS_EXAMPLE = " johnny johnny1";
    private static final String USER_ALREADY_EXISTS_MESSAGE = "401 User already exists!";
    private static final String SUCCESSFUL_REGISTER_MESSAGE = "Registration successful. Welcome to Todoist!";
    private static final String TASK_NAME_MISSING_MESSAGE = "Task name cannot be empty!" + System.lineSeparator();
    private static final String SOMETHING_WENT_WRONG_MESSAGE = "Something went wrong." + System.lineSeparator();
    private static final String UPDATE_TASK_SUCCESS_MESSAGE = "Updated task successfully." + System.lineSeparator();
    private static final String ADD_TASK_SUCCESS_MESSAGE = "Task added successfully." + System.lineSeparator();
    private static final String UPDATE_TASK_FAILURE_MESSAGE = "Failed to update task." + System.lineSeparator();
    private static final String GET_TASK_FAILURE_MESSAGE = "Failed to get task." + System.lineSeparator();
    private static final String DELETE_TASK_FAILURE_MESSAGE = "Failed to delete this task." + System.lineSeparator();
    private static final String FINISH_TASK_SUCCESS_MESSAGE = "Task completed successfully." + System.lineSeparator();
    private static final String FINISH_TASK_FAILURE_MESSAGE = "Failed to complete task." + System.lineSeparator();
    private static final String DELETE_TASK_SUCCESS_MESSAGE = "Task deleted successfully." + System.lineSeparator();
    private static final String ADD_TASK_FAILURE_MESSAGE = "Failed to add task." + System.lineSeparator();
    private static final String NO_TASKS_MESSAGE = "No tasks to list." + System.lineSeparator();
    private static final String COLLAB_NAME_MISSING_MESSAGE = "Collab name cannot be empty!" + System.lineSeparator();
    private static final String ADD_COLLAB_FAIL_MESSAGE = "Failed to add collaboration." + System.lineSeparator();
    private static final String ADD_COLLAB_SUCCESS_MESSAGE = "Successfully added collaboration."
            + System.lineSeparator();
    private static final String LOGIN = "login";
    private static final String REGISTER = "register";
    private static final String ADD_TASK = "add-task";
    private static final String UPDATE_TASK = "update-task";
    private static final String FINISH_TASK = "finish-task";
    private static final String DELETE_TASK = "delete-task";
    private static final String GET_TASK = "get-task";
    private static final String LIST_TASKS = "list-tasks";
    private static final String DASHBOARD = "list-dashboard";
    private static final String ADD_COLLAB = "add-collab";
    private static final String INVALID_ARGS_COUNT_MESSAGE_FORMAT =
            "Invalid count of arguments: \"%s\" expects %d arguments. Example: \"%s\"";
    private static final String TASK_FORMAT =
            "Task name: \"%s\"" + System.lineSeparator() +
                    "Date: %s" + System.lineSeparator() +
                    "Due date: %s" + System.lineSeparator() +
                    "Description \"%s\"" + System.lineSeparator() +
                    "--------" + System.lineSeparator();
    private static final String DATE_PATTERN_OUT = "dd LLLL yyyy";
    private static final String DATE_SEPARATOR = "/";
    private static final String NONE_PLACEHOLDER = "none";
    private static final String COMP_PREFIX = "--comp";
    private static final int COMP_PREFIX_LEN = 11;
    private static final String DATE_PREFIX = "--date";
    private static final int DATE_PREFIX_LEN = 6;
    private static final String USER_INFO = "Exception was thrown after request by user: ";
    private static final String COMMAND_INFO = "With command: ";
    private static final String ARGS_INFO = "With arguments: ";
    private UsersRepository usersRepository;
    private TaskRepository taskRepository;
    private CollabRepository collabRepository;
    private UserId requestingUser;
    private Command currentCommand;

    public CommandExecutor(UsersRepository userRepository, TaskRepository taskStorage,
                           CollabRepository collabRepository) {
        this.usersRepository = userRepository;
        this.taskRepository = taskStorage;
        this.collabRepository = collabRepository;
    }

    private void printAdditInfo(Exception e) {
        System.err.println(USER_INFO);
        System.err.println(requestingUser);
        System.err.println(COMMAND_INFO);
        System.err.println(currentCommand.command());
        System.err.println(ARGS_INFO);
        System.err.println(Arrays.toString(currentCommand.args()));
        e.printStackTrace();
    }

    public String execute(Command cmd) {
        this.requestingUser = new UserId(cmd.username());
        this.currentCommand = cmd;
        try {
            return switch (cmd.command()) {
                case LOGIN -> login(cmd.args());
                case REGISTER -> register(cmd.args());
                case ADD_TASK -> addTask(cmd.args());
                case UPDATE_TASK -> updateTask(cmd.args());
                case GET_TASK -> getTask(cmd.args());
                case DELETE_TASK -> deleteTask(cmd.args());
                case LIST_TASKS -> listTasks(cmd.args());
                case DASHBOARD -> listDashboard(cmd.args());
                case FINISH_TASK -> finishTask(cmd.args());
                case ADD_COLLAB -> addCollab(cmd.args());
                default -> INVALID_COMMAND_MESSAGE;
            };
        } catch (Exception e) {
            printAdditInfo(e);
            return SOMETHING_WENT_WRONG_MESSAGE;
        }
    }

    private String removeLineBreaks(String s) {
        return s.replace(System.lineSeparator(), "");
    }

    private String login(String[] args) {
        if (args.length != 2) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, LOGIN, 2, LOGIN + CREDENTIALS_EXAMPLE);
        }

        String pass = removeLineBreaks(args[1]);
        if (usersRepository.hasUserWith(args[0], pass)) {
            return SUCCESSFUL_LOGIN_MESSAGE;
        }
        return  INVALID_LOGIN_MESSAGE;
    }

    private String register(String[] args) {
        if (args.length != 2) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, REGISTER, 2, REGISTER + CREDENTIALS_EXAMPLE);
        }
        if (usersRepository.userExists(args[0])) {
            return USER_ALREADY_EXISTS_MESSAGE;
        }

        String pass = removeLineBreaks(args[1]);
        try {
            usersRepository.addUser(new User(new UserId(args[0]), pass));
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
        if (usersRepository.userExists(args[0])) {
            return SUCCESSFUL_REGISTER_MESSAGE;
        }
        return SOMETHING_WENT_WRONG_MESSAGE;
    }

    private Task tryToParseFromArgs(String[] args) {
        try {
            return Task.fromCommandLineArguments(args);
        } catch (IllegalArgumentException e) {
            printAdditInfo(e);
            return null;
        }
    }

    private String addTask(String[] args) {
        if (args.length < 1) {
            return TASK_NAME_MISSING_MESSAGE;
        }

        Task toAdd = tryToParseFromArgs(args);

        if (!taskRepository.addTask(requestingUser, toAdd)) {
            return ADD_TASK_FAILURE_MESSAGE;
        }

        return ADD_TASK_SUCCESS_MESSAGE;
    }

    private String updateTask(String[] args) {
        if (args.length < 1) {
            return TASK_NAME_MISSING_MESSAGE;
        }

        Task toUpdate = tryToParseFromArgs(args);

        if (!taskRepository.updateTask(requestingUser, toUpdate)) {
            return UPDATE_TASK_FAILURE_MESSAGE;
        }

        return UPDATE_TASK_SUCCESS_MESSAGE;
    }

    private String deleteTask(String[] args) {
        if (args.length < 1) {
            return TASK_NAME_MISSING_MESSAGE;
        }

        Task toDelete = tryToParseFromArgs(args);

        if (!taskRepository.deleteTask(requestingUser, toDelete)) {
            return DELETE_TASK_FAILURE_MESSAGE;
        }
        return DELETE_TASK_SUCCESS_MESSAGE;
    }

    private String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN_OUT);
        String parsedDate = NONE_PLACEHOLDER;
        if (date != null) {
            parsedDate = date.format(formatter);
        }
        return parsedDate;
    }

    private String toHumanReadable(Task task) {
        String descrption = task.description() == null || task.description().isEmpty()
                ? NONE_PLACEHOLDER
                : task.description();
        return String.format(TASK_FORMAT, task.name(), formatDate(task.date()),
                formatDate(task.dueDate()), descrption);
    }

    private String toHumanReadable(Set<Task> set) {
        if (set == null || set.isEmpty()) {
            return NO_TASKS_MESSAGE;
        }
        StringBuilder b = new StringBuilder();
        for (Task t : set) {
            b.append(toHumanReadable(t));
        }
        return b.toString();
    }

    private String getTask(String[] args) {
        if (args.length < 1) {
            return TASK_NAME_MISSING_MESSAGE;
        }

        Task toGet = tryToParseFromArgs(args);

        Task actual = taskRepository.getTask(requestingUser, toGet);
        if (actual == null) {
            return GET_TASK_FAILURE_MESSAGE;
        }
        return toHumanReadable(actual);
    }

    private LocalDate parseDate(String s) {
        String[] tokens = s.replace(System.lineSeparator(), "").split(DATE_SEPARATOR);
        int idx = 0;
        int day = Integer.parseInt(tokens[idx++]);
        int month = Integer.parseInt(tokens[idx++]);
        int year = Integer.parseInt(tokens[idx++]);
        return LocalDate.of(year, month, day);
    }

    private String listTasks(String[] args) {
        if (args.length == 0) {
            Set<Task> tasks = taskRepository.getUserInbox(requestingUser);
            return toHumanReadable(tasks);
        }
        if (args[0].length() < DATE_PREFIX_LEN) {
            return INVALID_COMMAND_MESSAGE;
        }

        String argPrefix = args[0].substring(0, DATE_PREFIX_LEN).replace(System.lineSeparator(), "");
        if (argPrefix.equals(COMP_PREFIX)) {
            return toHumanReadable(taskRepository.getUserCompleted(requestingUser));
        }

        if (argPrefix.equals(DATE_PREFIX)) {
            String dateParam = args[0].substring(DATE_PREFIX_LEN + 1);
            LocalDate date = parseDate(dateParam);
            return toHumanReadable(taskRepository.getUserTasksOnDate(requestingUser, date));
        }
        return INVALID_COMMAND_MESSAGE;
    }
    
    private String listDashboard(String[] args) {
        return toHumanReadable(taskRepository.getUserTasksOnDate(requestingUser, LocalDate.now()));
    }
    
    private String finishTask(String[] args) {
        if (args.length < 1) {
            return TASK_NAME_MISSING_MESSAGE;
        }

        Task task = tryToParseFromArgs(args);

        if (taskRepository.finishTask(requestingUser, task)) {
            return FINISH_TASK_SUCCESS_MESSAGE;
        }
        return FINISH_TASK_FAILURE_MESSAGE;
    }

    private String addCollab(String[] args) {
        if (args.length < 1) {
            return COLLAB_NAME_MISSING_MESSAGE;
        }

        Collaboration collab = Collaboration.fromCommandArgs(args, requestingUser);

        if (collabRepository.addCollab(collab)) {
            return ADD_COLLAB_SUCCESS_MESSAGE;
        }
        return ADD_COLLAB_FAIL_MESSAGE;
    }
}

package bg.sofia.uni.fmi.mjt.todoist.task;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {

    private void assertTasksAreTheSame(Task expected, Task actual) {
        assertEquals(expected.name(), actual.name(),
                String.format("Expected task name was %s, but actual was %s instead.",
                        expected.name(), actual.name()));

        assertEquals(expected.date(), actual.date(),
                String.format("Expected task date was %s, but actual was %s instead.",
                        expected.date(), actual.date()));

        assertEquals(expected.dueDate(), actual.dueDate(),
                String.format("Expected task due date was %s, but actual was %s instead.",
                        expected.dueDate(), actual.dueDate()));

        assertEquals(expected.description(), actual.description(),
                String.format("Expected task description was %s, but actual was %s instead.",
                        expected.description(), actual.description()));

    }

    @Test
    public void testTaskCreationFromCSVLineNameOnly() {
        Task expected = new Task("name",
                null,
                null,
                "");
        String line = "name,,,";
        Task actual = Task.fromCSVLine(line);
        assertTasksAreTheSame(expected, actual);
    }

    @Test
    public void testTaskCreationFromCSVLineNameAndDate() {
        Task expected = new Task("name",
                LocalDate.of(2002,2,2),
                null,
                "");
        String line = "name,2/2/2002,,";
        Task actual = Task.fromCSVLine(line);
        assertTasksAreTheSame(expected, actual);
    }

    @Test
    public void testTaskCreationFromCSVLineNameAndDateAndDueDate() {
        Task expected = new Task("name",
                LocalDate.of(2002,2,2),
                LocalDate.of(2002,3,3),
                "");
        String line = "name,2/2/2002,3/3/2002,";
        Task actual = Task.fromCSVLine(line);
        assertTasksAreTheSame(expected, actual);
    }

    @Test
    public void testTaskCreationFromCSVLineAllFields() {
        Task expected = new Task("name",
                LocalDate.of(2002,2,2),
                LocalDate.of(2002, 3, 3),
                "feed the cat");
        String line = "name,2/2/2002,3/3/2002,feed the cat";
        Task actual = Task.fromCSVLine(line);
        assertTasksAreTheSame(expected, actual);
    }

    @Test
    public void testTaskCreationFromCSVLineNameAndDateAndDescription() {
        Task expected = new Task("name",
                LocalDate.of(2002,2,2),
                null,
                "feed the cat");
        String line = "name,2/2/2002,,feed the cat";
        Task actual = Task.fromCSVLine(line);
        assertTasksAreTheSame(expected, actual);
    }

    @Test
    public void testTaskCreationFromCSVLineNameAndDescriptionAndDueDate() {
        Task expected = new Task("name",
                null,
                LocalDate.of(2002, 3, 3),
                "feed the cat!");
        String line = "name,,3/3/2002,feed the cat!";
        Task actual = Task.fromCSVLine(line);
        assertTasksAreTheSame(expected, actual);
    }

    @Test
    public void testTaskCreationFromCSVLineNameAndDescriptionHasCommas() {
        Task expected = new Task("name",
                null,
                LocalDate.of(2002, 3, 3),
                "feed, the, cat!");
        String line = "name,,3/3/2002,feed, the, cat!";
        Task actual = Task.fromCSVLine(line);
        assertTasksAreTheSame(expected, actual);
    }

    @Test
    public void testTaskCreationFromCSVLineNameAndDescriptionHasConsecutiveCommas() {
        Task expected = new Task("name",
                null,
                LocalDate.of(2002, 3, 3),
                "feed, the,, cat!,,,");
        String line = "name,,3/3/2002,feed, the,, cat!,,,";
        Task actual = Task.fromCSVLine(line);
        assertTasksAreTheSame(expected, actual);
    }

    @Test
    public void testTaskCreationFromCommandArgsNameOnly() {
        Task expected = new Task("\"abc\"",
                null,
                null,
                null);
        String[] args = new String[] {"--name=\"abc\""};
        Task actual = Task.fromCommandLineArguments(args);
        assertTasksAreTheSame(expected, actual);
    }

    @Test
    public void testTaskCreationFromCommandArgsNameAndDate() {
        Task expected = new Task("\"abc\"",
                LocalDate.of(2002,2,2),
                null,
                null);
        String[] args = new String[] {"--name=\"abc\"", "--date=2/2/2002"};
        Task actual = Task.fromCommandLineArguments(args);
        assertTasksAreTheSame(expected, actual);
    }

    @Test
    public void testTaskCreationFromCommandArgsNameDateAndDueDate() {
        Task expected = new Task("\"abc\"",
                LocalDate.of(2002,1,2),
                LocalDate.of(2002,2,2),
                null);
        String[] args = new String[] {"--name=\"abc\"","--date=2/1/2002", "--due-date=2/2/2002"};
        Task actual = Task.fromCommandLineArguments(args);
        assertTasksAreTheSame(expected, actual);
    }

    @Test
    public void testTaskCreationFromCommandArgsNameAndDueDate() {
        Task expected = new Task("\"abc\"",
                null,
                LocalDate.of(2002,2,2),
                null);
        String[] args = new String[] {"--name=\"abc\"", "--due-date=2/2/2002"};
        Task actual = Task.fromCommandLineArguments(args);
        assertTasksAreTheSame(expected, actual);
    }

    @Test
    public void testTaskCreationFromCommandArgsNameAndDescription() {
        Task expected = new Task("\"abc\"",
                null,
                null,
                "\"feed, the,, cat,,,\"");
        String[] args = new String[] {"--name=\"abc\"", "--description=\"feed, the,, cat,,,\""};
        Task actual = Task.fromCommandLineArguments(args);
        assertTasksAreTheSame(expected, actual);
    }

    @Test
    public void testTaskCreationFromCommandArgsNameDateAndDescription() {
        Task expected = new Task("\"abc\"",
                LocalDate.of(2002,2,2),
                null,
                "\"some description\"");
        String[] args = new String[] {"--name=\"abc\"", "--description=\"some description\"", "--date=2/2/2002"};
        Task actual = Task.fromCommandLineArguments(args);
        assertTasksAreTheSame(expected, actual);
    }

    @Test
    public void testTaskCreationFromCommandArgsNameDueDateAndDescription() {
        Task expected = new Task("\"abc\"",
                null,
                LocalDate.of(2002,2,2),
                "\"some description\"");
        String[] args = new String[] {"--name=\"abc\"", "--description=\"some description\"", "--due-date=2/2/2002"};
        Task actual = Task.fromCommandLineArguments(args);
        assertTasksAreTheSame(expected, actual);
    }

    @Test
    public void testTaskCreationFromCommandArgsAllFields() {
        Task expected = new Task("\"abc\"",
                LocalDate.of(2002,1,2),
                LocalDate.of(2002,2,2),
                "\"abcd\"");
        String[] args = new String[] {"--name=\"abc\"", "--description=\"abcd\"",
                "--due-date=2/2/2002", "--date=2/1/2002"};
        Task actual = Task.fromCommandLineArguments(args);
        assertTasksAreTheSame(expected, actual);
    }
}

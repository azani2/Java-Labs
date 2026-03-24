package bg.sofia.uni.fmi.mjt.todoist.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CommandCreatorTest {
    @Test
    public void testCommandCreateNoArguments() {
        String command = "test-command";
        String commandLine = "test-user " + command;
        Command cmd = CommandCreator.newCommand(commandLine);

        assertEquals(command, cmd.command(),
                String.format("Expected command to be %s, but was %s instead",
                        command, cmd.command()));
        assertNotNull(cmd.args(),
                "Command arguments were not expected to be null, but was null.");
        assertEquals(0, cmd.args().length,
                String.format("Expected arguments length was 0, but was %d instead.", cmd.args().length));
    }

    @Test
    public void testCommandCreateOneArguments() {
        String command = "test-command abc";
        String commandLine = "test-user " + command;
        Command cmd = CommandCreator.newCommand(commandLine);

        assertEquals(command.split(" ")[0], cmd.command(),
                String.format("Expected command to be %s, but was %s instead",
                        command.split(" ")[0], cmd.command()));
        assertNotNull(cmd.args(),
                "Command arguments were not expected to be null, but was null.");
        assertEquals(1, cmd.args().length,
                String.format("Expected arguments length was 1, but was %d instead.", cmd.args().length));
        assertEquals(command.split(" ")[1], cmd.args()[0],
                String.format("Expected command argument was \"abc\", but actual was %s", cmd.args()[0]));
    }

    @Test
    public void testCommandCreationWithArgumentInQuotes() {
        String command = "test-command \"abc 123\"";
        String commandLine = "test-user " + command;
        Command cmd = CommandCreator.newCommand(commandLine);

        assertEquals(command.split(" ")[0], cmd.command(),
                String.format("Expected command to be %s, but was %s instead",
                        command.split(" ")[0], cmd.command()));
        assertNotNull(cmd.args(),
                "Command arguments were not expected to be null, but was null.");
        assertEquals(1, cmd.args().length,
                String.format("Expected arguments length was 1, but was %d instead.", cmd.args().length));
        assertEquals("abc 123", cmd.args()[0],
                String.format("Expected command argument was \"abc 123\", but actual was %s", cmd.args()[0]));
    }
}

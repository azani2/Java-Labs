package bg.sofia.uni.fmi.mjt.todoist.command;

import java.util.ArrayList;
import java.util.List;

public class CommandCreator {
    private static final char DEFAULT_SEPARATOR = ' ';
    private static final String QUOTE = "\"";
    private static final char QUOTE_CHAR = '"';
    private static List<String> getCommandArguments(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder builder = new StringBuilder();

        boolean insideQuote = false;

        for (char c : input.toCharArray()) {
            if (c == QUOTE_CHAR && insideQuote) {
                insideQuote = false;
            } else if ( c == QUOTE_CHAR) {
                insideQuote = true;
            }
            if (c == DEFAULT_SEPARATOR && !insideQuote) {
                tokens.add(builder.toString().replace(QUOTE, ""));
                builder.delete(0, builder.length());
            } else {
                builder.append(c);
            }
        }
        tokens.add(builder.toString().replace(QUOTE, ""));
        return tokens;
    }

    public static Command newCommand(String clientInput) {
        List<String> tokens = CommandCreator.getCommandArguments(clientInput);
        String[] args = tokens.subList(2, tokens.size()).toArray(new String[0]);

        return new Command(tokens.getFirst().replace(System.lineSeparator(), ""),
                tokens.get(1).replace(System.lineSeparator(), ""),
                args);
    }
}

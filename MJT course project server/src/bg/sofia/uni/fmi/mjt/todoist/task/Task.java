package bg.sofia.uni.fmi.mjt.todoist.task;

import bg.sofia.uni.fmi.mjt.todoist.validation.StringValidator;

import java.time.LocalDate;

public record Task(String name, LocalDate date, LocalDate dueDate, String description) {
    private static final String DATE_SEPARATOR = "/";
    private static final int DATE_PARTS_COUNT = 3;
    private static final int DATE_DAY_IDX = 0;
    private static final int DATE_MONTH_IDX = 1;
    private static final int DATE_YEAR_IDX = 2;
    private static final String ARG_TASK_NAME = "Task name";
    private static final int ARG_TASK_NAME_IDX = 7;
    private static final String ARG_DATE = "--date=";
    private static final String ARG_DUE_DATE = "--due-d";
    private static final String ARG_DESC = "--descr";
    private static final int ARG_PREF_LEN = 7;
    private static final int ARG_DATE_LEN = 7;
    private static final int ARG_DUE_DATE_LEN = 11;
    private static final int ARG_DESC_LEN = 14;
    private static final String CSV_SEPARATOR = ",";
    private static final int DATE_TOKEN_IDX =  1;
    private static final int DUE_DATE_TOKEN_IDX =  2;
    private static final int DESC_TOKEN_IDX =  3;
    private static final int DESCRIPTION_TOKEN_BRAKE = 2;
    private static final String LINE_ARG = "Line";

    public Task {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    private static LocalDate makeDateOf(String arg, int startIdx) {
        if (arg.length() < startIdx + 1) {
            return null;
        }
        String[] dateTokens = arg.substring(startIdx).split(DATE_SEPARATOR);
        if (dateTokens.length != DATE_PARTS_COUNT) {
            return null;
        }

        try {
            return LocalDate.of(Integer.parseInt(dateTokens[DATE_YEAR_IDX]),
                    Integer.parseInt(dateTokens[DATE_MONTH_IDX]),
                    Integer.parseInt(dateTokens[DATE_DAY_IDX]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Task fromCommandLineArguments(String[] args) {
        args[args.length - 1] = args[args.length - 1].replace(System.lineSeparator(), "");
        String taskName = args[0].substring(ARG_TASK_NAME_IDX);
        StringValidator.assertNotNullOrEmpty(taskName, ARG_TASK_NAME);
        LocalDate date = null;
        LocalDate dueDate = null;
        String desc = null;

        for (String arg : args) {
            String pref = arg.substring(0, ARG_PREF_LEN);

            switch (pref) {
                case ARG_DATE -> date = makeDateOf(arg, ARG_DATE_LEN);
                case ARG_DUE_DATE -> dueDate = makeDateOf(arg, ARG_DUE_DATE_LEN);
                case ARG_DESC ->  {
                    String subs = arg.substring(ARG_DESC_LEN);
                    if (!subs.isEmpty()) {
                        desc = subs;
                    }
                }
                default -> { }
            }
        }
        return new Task(taskName, date, dueDate, desc);
    }

    public static Task fromCSVLine(String line) {
        StringValidator.assertNotNullOrEmpty(line, LINE_ARG);
        String[] tokens = line.split(CSV_SEPARATOR, -1);

        int tokensCount = tokens.length;
        String taskName = tokens[0];
        LocalDate taskDate = makeDateOf(tokens[DATE_TOKEN_IDX], 0);
        LocalDate taskDueDate = makeDateOf(tokens[DUE_DATE_TOKEN_IDX], 0);
        String desc = null;
        StringBuilder descriptionBuilder = new StringBuilder();

        int lastDescIdx = tokensCount - 1;
        for (int i = DESC_TOKEN_IDX; i < lastDescIdx; i++) {
            if (tokens[i] != null) {
                descriptionBuilder.append(tokens[i]).append(CSV_SEPARATOR);
            } else {
                descriptionBuilder.append(CSV_SEPARATOR);
            }
        }
        descriptionBuilder.append(tokens[lastDescIdx]);

        desc = descriptionBuilder.toString();

        return new Task(taskName, taskDate, taskDueDate, desc);
    }

    @Override
    public int hashCode() {
        return  name.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Task)) {
            return false;
        }

        Task that = (Task) other;
        return this.name.equals(that.name);
    }

    private String formatDateCSV(LocalDate d) {
        String str = "";
        if (d != null) {
            str = d.getDayOfMonth() + DATE_SEPARATOR +
                    d.getMonthValue() + DATE_SEPARATOR +
                    d.getYear();
        }
        return str;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(name).append(CSV_SEPARATOR);
        String dateStr = formatDateCSV(date());
        String dueDateStr = formatDateCSV(dueDate());

        String descStr = "";
        if (description != null) {
            descStr = description;
        }

        builder.append(dateStr).append(CSV_SEPARATOR)
                .append(dueDateStr).append(CSV_SEPARATOR)
                .append(descStr);
        return builder.toString();
    }
}

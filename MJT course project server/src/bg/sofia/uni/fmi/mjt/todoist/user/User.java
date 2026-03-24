package bg.sofia.uni.fmi.mjt.todoist.user;

import bg.sofia.uni.fmi.mjt.todoist.validation.StringValidator;

public record User(UserId userId, String password) {
    private static final String[] PASSWORD_INVALID_STRINGS = {" ", "\\", ",", "/", "?"};
    private static final String PASSWORD_ARG = "Password";

    private static final String SEPARATOR = ",";

    public static User fromLine(String line) {
        String[] tokens = line.split(SEPARATOR);
        return new User(new UserId(tokens[0]), tokens[1]);
    }

    public static User create(String username, String password) {
        StringValidator.assertDoesNotInclude(password, PASSWORD_INVALID_STRINGS, PASSWORD_ARG);
        return new User(new UserId(username), password);
    }

    @Override
    public String toString() {
        return userId.username() + SEPARATOR + password;
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof User)) {
            return false;
        }

        User that = (User) other;
        return this.userId.equals(that.userId);
    }
}

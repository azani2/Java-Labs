package bg.sofia.uni.fmi.mjt.todoist.user;

import bg.sofia.uni.fmi.mjt.todoist.validation.StringValidator;

public record UserId(String username) {
    private static final String[] USERNAME_INVALID_STRINGS = {" ", "\\", "!", "/", "?"};
    private static final String USERNAME_ARG = "Username";

    public UserId {
        StringValidator.assertDoesNotInclude(username, USERNAME_INVALID_STRINGS, USERNAME_ARG);
    }

    @Override
    public String toString() {
        return username;
    }
}

package bg.sofia.uni.fmi.mjt.todoist.storage;

import bg.sofia.uni.fmi.mjt.todoist.user.User;
import bg.sofia.uni.fmi.mjt.todoist.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UsersRepositoryTest {
    private UsersRepository repository;
    private User user;
    private final String testSaveFile = "testSaveFile.txt";

    private final Path filePath = Path.of(testSaveFile);

    @BeforeEach
    public void setup() throws IOException {
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        repository = new UsersRepository(testSaveFile);
        user = new User(new UserId("user1"), "password1");
    }

    @Test
    public void testUserExistsEmptyUsername() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.userExists(""),
                "Expected to throw IllegalArgumentException when " +
                        "calling userExists with an empty username.");
    }

    @Test
    public void testUserExistsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> repository.userExists(null),
                "Expected to throw IllegalArgumentException when " +
                        "calling userExists with a null username.");
    }

    @Test
    public void testUserExistsDoesNotExist() {
        assertFalse(repository.userExists("not-existing"),
                "Expected to return false when checking if an user " +
                        "exists from an empty repository, but was true.");
    }

    @Test
    public void testUserExistsHasUser() {
        repository = new UsersRepository(List.of(user), testSaveFile);

        assertTrue(repository.userExists("user1"),
                "Expected true when checking if an already added username exists," +
                        "but was false.");
    }

    private void addManyUsers() {
        for (int i = 2; i <= 10; i++) {
            User userI = new User(new UserId("user" + i), "password");
            repository.addUser(userI);
        }
    }

    @Test
    public void testUserExistsHasManyUsers() {
        addManyUsers();
        assertTrue(repository.userExists("user5"),
                "Expected true when checking if an already added username exists," +
                        "but was false.");
    }


    @Test
    public void testLoadUsersFromSaveFileNotEmpty() {
        repository = new UsersRepository("testLoadUser.csv");

        assertTrue(repository.userExists("user1"),
                "Expected to return true when checking if username from initialization save file" +
                        "exists in the repository, but was false.");

        assertTrue(repository.hasUserWith("user1", "password1"),
                "Expected to return true when cheking if a user with username and password pair " +
                        "from initialization save file exists in the repository, but was false.");
    }

    @Test
    public void testAddUserWithAlreadyExisting() {
        repository.addUser(user);

        assertThrows(IllegalArgumentException.class, () -> repository.addUser(user),
                "Expected to throw IllegalArgumentException when attempting to add a user" +
                        "that is already in the repository, but was not thrown.");
    }

    @Test
    public void testAddUserHasManyUsersOK() {
        addManyUsers();
        assertDoesNotThrow(() -> repository.addUser(user),
                "Expected to not throw exceptions after adding a user that was not " +
                        "previously in the repository.");
    }

    @Test
    public void testAddUserHasNoUsers() {
        assertDoesNotThrow(() -> repository.addUser(user),
                "Expected to not throw exceptions after adding a user that was not " +
                        "previously in the repository, because it was empty.");
    }

    @Test
    public void testGetUserIdsWithNoUsers() {
        assertNull(repository.getUserIds());
    }

    @Test
    public void testGetUsersDataWithNoUsers() {
        assertNull(repository.getUsersData());
    }

    @Test
    public void testGetUserIdsOneUser() {
        repository = new UsersRepository(List.of(user), testSaveFile);

        assertDoesNotThrow(() -> repository.getUserIds(),
                "Expected to not throw when getting userIds from not-empty repository.");
        assertNotNull(repository.getUserIds(),
                "Collection of userIds is not expected to be null when repository is not empty.");

        Set<UserId> actual = repository.getUserIds();

        assertEquals(1, actual.size(),
                String.format("Size of actual and expected collections differ. Expected %d, but was %d.",
                        1, actual.size()));

        assertTrue(actual.contains(user.userId()),
                "Collection was expected to contain userId \"user1\", but did not.");
    }

    @Test
    public void testGetUserIdsManyUsers() {
        addManyUsers();

        assertDoesNotThrow(() -> repository.getUserIds(),
                "Expected to not throw when getting userIds from not-empty repository.");
        assertNotNull(repository.getUserIds(),
                "Collection of userIds is not expected to be null when repository is not empty.");

        Set<UserId> actual = repository.getUserIds();

        Set<UserId> expected = new HashSet<>();
        for (int i = 2; i <= 10; i++) {
            expected.add(new UserId("user" + i));
        }

        assertEquals(expected.size(), actual.size(),
                String.format("Size of actual and expected collections differ. Expected %d, but was %d.",
                        1, actual.size()));

        assertTrue(actual.containsAll(expected),
                String.format("Collection was expected to contain all userIds from %s," +
                        " but did not.", Arrays.toString(expected.toArray())));
    }

    @Test
    public void testGetUsersDataOneUser() {
        repository = new UsersRepository(List.of(user), testSaveFile);

        assertDoesNotThrow(() -> repository.getUsersData(),
                "Expected to not throw when getting users from not-empty repository.");
        assertNotNull(repository.getUsersData(),
                "Collection of users is not expected to be null when repository is not empty.");

        Set<User> actual = repository.getUsersData();

        assertEquals(1, actual.size(),
                String.format("Size of actual and expected collections differ. Expected %d, but was %d.",
                        1, actual.size()));

        assertTrue(actual.contains(user),
                "Collection was expected to contain user [\"user1\",\"password1\"], but did not.");
    }

    @Test
    public void testGetUsersDataManyUsers() {
        addManyUsers();

        assertDoesNotThrow(() -> repository.getUsersData(),
                "Expected to not throw when getting users from not-empty repository.");
        assertNotNull(repository.getUsersData(),
                "Collection of users is not expected to be null when repository is not empty.");

        Set<User> actual = repository.getUsersData();
        Set<User> expected = new HashSet<>();
        for (int i = 2; i <= 10; i++) {
            expected.add(new User(new UserId("user" + i), "password"));
        }

        assertEquals(expected.size(), actual.size(),
                String.format("Size of actual and expected collections differ. Expected %d, but was %d.",
                        1, actual.size()));

        for (User u : expected) {
            assertTrue(actual.contains(u),
                    String.format("Collection was expected to contain user [\"%s\",\"%s\"], but did not.",
                            user.userId().username(), user.password()));
        }
    }
}

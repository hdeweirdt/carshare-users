package be.harm.carshare.users.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    private User user;

    private final String validPassword = "Password123";
    private final String validUserName= "Username";

    @AfterEach
    private void cleanUp() {
        user = null;
    }


    @Test
    void shouldHaveNoRolesByDefault() {
        user = new User(validUserName, validPassword);

        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void shouldAcceptValidUserNameAndPassword() {
       user = new User(validUserName, validPassword);
    }

    @Test
    void shouldNotAllowPasswordWithoutNumber() {
        String password = "nonumbershere";

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            user = new User(validUserName, password);
        });
    }

    @Test
    void shouldNotAllowOnlyUppercasePassword() {
        String password = "LONGENOUGH1";

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            user = new User(validUserName, password);
        });
    }

    @Test
    void shouldNotAllowOnlyLowercasePassword() {
        String password = "longenough1";

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            user = new User(validUserName, password);
        });
    }

    @Test
    void shouldNotAllowTooShortPassword() {
        String password = "short1";

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            user = new User(validUserName, password);
        });
    }

    @Test
    void shouldNotAllowNullPassword() {
        String password = null;

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            user = new User(validUserName, password);
        });
    }


    @Test
    void shouldNotAllowNullUsername() {
        String username = null;

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            user = new User(username, validPassword);
        });
    }

}
package be.harm.carshare.users.user;

import be.harm.carshare.users.user.security.ApplicationRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserJPAServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserJPAService userJPAService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        // Thoughts: change to dummy implementations?
        when(passwordEncoder.encode(anyString())).then(returnsFirstArg());
        when(userRepository.save(any(User.class))).then(returnsFirstArg());
    }

    @Test
    void shouldGiveNewAdminAdminRights() {
        // Given
        User user = new User("Username", "Wachtwoord1");

        // When
        User resultingUser = userJPAService.saveAdmin(user);

        // Then
        assertEquals(1, resultingUser.getRoles().size());
        assertTrue(resultingUser.getRoles().stream()
                .anyMatch((role -> role == ApplicationRole.ADMIN)));
    }

    @Test
    void shouldGiveNewUserOnlyPlayerRights() {
        // Given
        User user = new User("Username", "Wachtwoord1");

        // When
        User resultingUser = userJPAService.saveUser(user);

        // Then
        assertEquals(1, resultingUser.getRoles().size());
        assertTrue(resultingUser.getRoles().stream()
                .anyMatch((role -> role == ApplicationRole.USER)));
    }
}
package be.harm.carshare.users;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("local")
@TestPropertySource(properties = {"carshare.users.jwt.secret=secret"})
class CarshareUsersApplicationTests {

    @Test
    void contextLoads() {
    }

}

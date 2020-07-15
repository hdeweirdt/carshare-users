package be.harm.carshare.users.bootstrap;

import be.harm.carshare.users.user.User;
import be.harm.carshare.users.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private final UserService userService;

    @Value("${application.adminUserName}")
    private String adminUserName;

    @Value("${application.adminUserPassword}")
    private String adminPassword;

    public DataLoader(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        if(userService.findAll().isEmpty()) {
            saveAdmin();
        }
    }


    private void saveAdmin() {
        userService.saveAdmin(new User(adminUserName, adminPassword));
    }
}

package be.harm.carshare.users.user;

import java.util.Optional;
import java.util.Set;

public interface UserService {
    Set<User> findAll();

    Optional<User> findById(Long id);

    User saveUser(User user);

    User saveAdmin(User user);

    User updateUser(User user);
}

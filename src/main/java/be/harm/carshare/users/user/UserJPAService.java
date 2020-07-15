package be.harm.carshare.users.user;

import be.harm.carshare.users.user.security.ApplicationRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
class UserJPAService implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserJPAService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public Set<User> findAll() {
        Set<User> users = new HashSet<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User saveAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(getDefaultAdminRoles());
        return userRepository.save(user);
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(getDefaultUserRoles());
        return userRepository.save(user);
    }

    private Set<ApplicationRole> getDefaultAdminRoles() {
        return Collections.singleton(ApplicationRole.ADMIN);
    }

    private Set<ApplicationRole> getDefaultUserRoles() {
        return Collections.singleton(ApplicationRole.USER);
    }

}

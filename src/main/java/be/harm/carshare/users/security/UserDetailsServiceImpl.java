package be.harm.carshare.users.security;

import be.harm.carshare.users.user.User;
import be.harm.carshare.users.user.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + userName + " not found.");
        }
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .flatMap(role -> role.getGrantedAuthorities().stream())
                .collect(Collectors.toList());

        return new AuthenticatedUser(user.getUserName(), user.getPassword(), user.getId(), authorities);
    }
}

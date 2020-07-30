package be.harm.carshare.users.security.authentication;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Collections;

/**
 * Wrapper around the [org.springframework.security.userdetails.User], expanding it with the user's Id.
 * Used so the ID can be used for authorization checks.
 */
public class AuthenticatedUser extends User {

    @Getter
    private Long id;

    public AuthenticatedUser(String username, String password, Long id, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
    }

    public AuthenticatedUser(String username, String password) {
        super(username, password, Collections.emptyList());
    }

    public AuthenticatedUser(String username, String password, Long id) {
        this(username, password, id, Collections.emptyList());
    }
}



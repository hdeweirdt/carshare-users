package be.harm.carshare.users.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Wrapper around the [org.springframework.security.userdetails.User], expanding it with the user's Id.
 * Used so the ID can be used for authorization checks.
 */
public class AuthenticatedUser implements UserDetails {

    private final User user;

    @Getter
    private final Long id;

    public AuthenticatedUser(String username, String password, Long id) {
        this(username, password, id, Collections.emptyList());
    }

    public AuthenticatedUser(String username, String password, Long id, Collection<? extends GrantedAuthority> authorities) {
        this.user = new User(username, password, true, true, true, true, authorities);
        this.id = id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}

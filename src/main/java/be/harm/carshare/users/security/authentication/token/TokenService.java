package be.harm.carshare.users.security.authentication.token;

import be.harm.carshare.users.security.authentication.AuthenticatedUser;

import java.util.Optional;

/**
 * Creates and validates credentials.
 */
public interface TokenService {

    /**
     * Checks the validity of the given credentials.
     *
     * @return attributes if verified
     */
    Optional<String> verify(String token);

    String createToken(AuthenticatedUser user);
}


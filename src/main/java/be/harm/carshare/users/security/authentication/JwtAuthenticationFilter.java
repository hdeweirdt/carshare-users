package be.harm.carshare.users.security.authentication;

import be.harm.carshare.users.security.authentication.token.JwtTokenService;
import be.harm.carshare.users.security.authentication.token.TokenService;
import be.harm.carshare.users.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static be.harm.carshare.users.security.authentication.AuthenticationConstants.AUTHORIZATION;
import static be.harm.carshare.users.security.authentication.AuthenticationConstants.AUTH_TOKEN_PREFIX;


public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService = new JwtTokenService();

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
        try {
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUserName(),
                            user.getPassword(),
                            Collections.emptyList()
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) {
        AuthenticatedUser user = (AuthenticatedUser) authResult.getPrincipal();
        String token = tokenService.createToken(user);

        response.addHeader(AUTHORIZATION, AUTH_TOKEN_PREFIX + token);

    }
}

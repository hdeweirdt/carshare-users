package be.harm.carshare.users.security.authentication;

import be.harm.carshare.users.security.authentication.token.TokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import static be.harm.carshare.users.security.authentication.AuthenticationConstants.AUTHORIZATION;
import static be.harm.carshare.users.security.authentication.AuthenticationConstants.AUTH_TOKEN_PREFIX;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, TokenService tokenService, UserDetailsService userDetailsService) {
        super(authenticationManager);
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String authHeader = request.getHeader(AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(AUTH_TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION);
        Optional<String> userName = tokenService.verify(header.replace(AUTH_TOKEN_PREFIX, ""));
        UserDetails principal = userDetailsService.loadUserByUsername(userName.orElseThrow(() -> new UsernameNotFoundException("No username found in JWT")));
        return userName.map(value -> new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()))
                .orElse(null);
    }
}

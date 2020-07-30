package be.harm.carshare.users.security.authentication;

import be.harm.carshare.users.security.authentication.token.JwtTokenService;
import be.harm.carshare.users.security.authentication.token.TokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

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

    private final TokenService tokenService = new JwtTokenService();

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
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
        return userName.map(value -> new UsernamePasswordAuthenticationToken(value, null, Collections.emptyList()))
                .orElse(null);
    }
}

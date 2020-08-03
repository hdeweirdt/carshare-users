package be.harm.carshare.users.security;

import be.harm.carshare.users.security.authentication.JwtAuthenticationFilter;
import be.harm.carshare.users.security.authentication.JwtAuthorizationFilter;
import be.harm.carshare.users.security.authentication.token.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

class SecurityConfig {
    @Configuration
    @EnableGlobalMethodSecurity(
            prePostEnabled = true,
            securedEnabled = true,
            jsr250Enabled = true)
    public static class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
    }

    @Configuration
    @Order(2)
    static class ApiSecurityConfig extends WebSecurityConfigurerAdapter {
        private static final String H2CONSOLE_LOCATION = "/h2-console/**";
        private static final String LOGIN_URL = "/login";
        private static final String REGISTER_URL = "/users";

        private final UserDetailsService userDetailsService;
        private final TokenService tokenService;

        ApiSecurityConfig(UserDetailsService userDetailsService, TokenService tokenService) {
            this.userDetailsService = userDetailsService;
            this.tokenService = tokenService;
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService);
        }

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            httpSecurity.cors().and().csrf().disable()
                    .authorizeRequests()
                    .antMatchers(H2CONSOLE_LOCATION).permitAll()
                    .antMatchers(HttpMethod.POST, REGISTER_URL).permitAll()
                    .antMatchers(HttpMethod.POST, LOGIN_URL).permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .addFilter(new JwtAuthenticationFilter(authenticationManager(), tokenService))
                    .addFilter(new JwtAuthorizationFilter(authenticationManager(), tokenService))
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
            final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
            return source;
        }
    }
}

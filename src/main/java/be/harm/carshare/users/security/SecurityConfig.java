package be.harm.carshare.users.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

class SecurityConfig {
    @Configuration
    @EnableGlobalMethodSecurity(
            prePostEnabled = true,
            securedEnabled = true,
            jsr250Enabled = true)
    public static class MethodSecurityConfig extends GlobalMethodSecurityConfiguration { }

    @Configuration
    @Order(1)
    static class ApiSecurityConfig extends WebSecurityConfigurerAdapter {
        private static final String H2CONSOLE_LOCATION = "/h2-console/**";

        private final UserDetailsService userDetailsService;

        ApiSecurityConfig(UserDetailsService userDetailsService) {
            this.userDetailsService = userDetailsService;
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService);
        }

        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            httpSecurity.authorizeRequests()
                    .antMatchers(H2CONSOLE_LOCATION).permitAll()
                    // TEMPORARILY PERMIT EVERYTHING, ADD SECURITY LATER
                    .antMatchers("/**").permitAll();
            httpSecurity.csrf().ignoringAntMatchers(H2CONSOLE_LOCATION);
            httpSecurity.headers().frameOptions().sameOrigin();
        }
    }
}

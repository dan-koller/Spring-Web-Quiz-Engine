package io.dankoller.github.webquizengine.auth;

import io.dankoller.github.webquizengine.entity.user.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@SuppressWarnings({"unused", "deprecation"})
public class WebSecurityConfigurerImpl extends WebSecurityConfigurerAdapter {
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final UserDetailsServiceImpl userDetailsService;

    public WebSecurityConfigurerImpl(RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                                     UserDetailsServiceImpl userDetailsService) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.userDetailsService = userDetailsService;
    }

    /**
     * This method is used to configure the {@link AuthenticationManagerBuilder} to specify which UserDetailsService and
     * {@link PasswordEncoder} to use.
     *
     * @param auth The {@link AuthenticationManagerBuilder} to use
     * @throws Exception If an error occurs
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(getEncoder());
    }

    /**
     * This method is used to configure the security of the web application by restricting access based on the
     * HttpServletRequest.
     *
     * @param http the {@link HttpSecurity} to modify
     * @throws Exception if an error occurs
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic()
                .authenticationEntryPoint(restAuthenticationEntryPoint) // handles 401 auth error
                .and()
                .csrf().disable().headers().frameOptions().disable() // for Postman, H2 console
                .and()
                .authorizeRequests()
                // Api endpoints
                .mvcMatchers("/api/register").permitAll()
                .mvcMatchers("/api/quizzes").hasRole("USER")
                .mvcMatchers("/api/quizzes/**").hasRole("USER");
    }

    /**
     * This method is used to encrypt the password of the user using the BCryptPasswordEncoder.
     *
     * @return the {@link PasswordEncoder} to use
     */
    @Bean
    public static PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}
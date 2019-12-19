package org.luncert.facedetect.config;

import org.luncert.facedetect.component.SecurityAuthFailureHandler;
import org.luncert.facedetect.component.SecurityAuthSuccessHandler;
import org.luncert.facedetect.component.SecurityUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityUserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity hs) throws Exception {
        hs.csrf().disable();
        hs.cors();
        hs.authorizeRequests()
            .antMatchers(HttpMethod.POST, "/user/student", "/user/teacher").permitAll()
            .antMatchers(HttpMethod.GET, "/user/unauthorized").permitAll();
        hs.authorizeRequests()
            .anyRequest().authenticated();
        hs.formLogin()
            .loginPage("/user/unauthorized")
            .loginProcessingUrl("/user/signIn")
            .usernameParameter("account")
            .passwordParameter("password")
            .successHandler(new SecurityAuthSuccessHandler())
            .failureHandler(new SecurityAuthFailureHandler());
//                .defaultSuccessUrl("/user", true);
    }
}
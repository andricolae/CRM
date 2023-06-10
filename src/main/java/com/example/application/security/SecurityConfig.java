package com.example.application.security;

import com.example.application.views.list.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests().requestMatchers("/images/*.png").permitAll();
        super.configure(http);
        setLoginView(http, LoginView.class); 
    }

    @Bean
    public UserDetailsService users() {
        UserDetails user = User.builder()
                .username("user")
                //pass
                .password("{bcrypt}$2a$12$xHmsa9J8jnKcai0o2dOgReULYSut7Kmaol5b3oh1sheDqOOzG7.s6")
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username("admin")
                //apass
                .password("{bcrypt}$2a$12$e3TwLRsmBec3eSqLz8on.exBHTvlsTBvt0tHyMTVgOl5fvmlfTPra")
                .roles("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin); // <5>
    }
}

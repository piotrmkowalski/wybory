package com.example.wybory1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class Autoryzacja extends WebSecurityConfigurerAdapter {

    @Bean
    public UserDetailsService userDetailsService() {

        UserDetails wyb1 = User.withDefaultPasswordEncoder()
                .username("wyb1")
                .password("123")
                .roles("USER")
                .build();
        UserDetails wyb2 = User.withDefaultPasswordEncoder()
                .username("wyb2")
                .password("123")
                .roles("ADMIN")
                .build();
        UserDetails wyb3 = User.withDefaultPasswordEncoder()
                .username("wyb3")
                .password("123")
                .roles("ADMIN", "USER")
                .build();
        return new InMemoryUserDetailsManager(wyb1, wyb2, wyb3);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/glosowanie", "/glosuj", "/dolicz", "/wyniki", "/start2").permitAll()
                .antMatchers("/lista", "/dodaj", "/kasuj", "/wylon").hasAnyRole("ADMIN")
                .anyRequest().hasRole("USER")
                .and().logout().logoutSuccessUrl("/")
                .and().formLogin().permitAll()
                .defaultSuccessUrl("/home");
    }
}
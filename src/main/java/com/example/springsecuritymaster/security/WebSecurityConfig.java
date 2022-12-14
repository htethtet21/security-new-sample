package com.example.springsecuritymaster.security;

import org.aspectj.weaver.ast.Not;
import org.hibernate.bytecode.enhance.internal.tracker.NoopCollectionTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

import javax.validation.constraints.NotBlank;

import static com.example.springsecuritymaster.security.SecurityRoles.*;

@Configuration
public class WebSecurityConfig {

    @Autowired
    private RoleHierarchy roleHierarchy;

    @Bean
    public UserDetailsService userDetailsService() {
       /* PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();*/

        var uds = new InMemoryUserDetailsManager();
        var john = User.withUsername("john")
                .password("john")
                .roles(SUPER_ADMIN)
                .build();
        var emma = User.withUsername("emma")
                .password("emma")
                .roles(EMPLOYEES_ADMIN)
                .build();
        var william = User.withUsername("william")
                .password("william")
                .roles(DEPARTMENT_CREATE,DEPARTMENT_PAG_VIEW, DEPARTMENTS_READ)
                .build();

        var lucas = User.withUsername("lucas")
                .password("lucas")
                .roles(CUSTOMERS_READ, CUSTOMERS_PAG_VIEW)
                .build();

        var tom = User.withUsername("tom")
                .password("tom")
                .roles()
                .build();

        uds.createUser(john);
        uds.createUser(emma);
        uds.createUser(william);
        uds.createUser(lucas);
        uds.createUser(tom);


        return uds;
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeRequests()
                .expressionHandler(expressionHandler())
                .mvcMatchers("/", "/home", "/customers","/create-customer").permitAll()
                .mvcMatchers("/bootstrap/**").permitAll()
                .mvcMatchers("/customers").hasRole(CUSTOMERS_PAG_VIEW)
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .failureUrl("/login-error")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .permitAll();
        httpSecurity.csrf().disable();
        return httpSecurity.build();
    }


    private DefaultWebSecurityExpressionHandler expressionHandler() {
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }


}

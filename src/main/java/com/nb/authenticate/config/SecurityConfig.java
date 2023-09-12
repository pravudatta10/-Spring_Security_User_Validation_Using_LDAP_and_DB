package com.nb.authenticate.config;

import com.nb.authenticate.filter.AuthenticationFilter;
import com.nb.authenticate.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    AuthenticationFilter authenticationFilter;
    @Autowired
    AuthenticationService authenticationService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        try {
            if (auth != null) {
                auth.userDetailsService(authenticationService);
            }
            auth.ldapAuthentication().userSearchFilter("(uid={0})").
                    userSearchBase("dc=nichebit,dc=com").
                    groupSearchFilter("uniqueMember={0}").
                    groupSearchBase("ou=People,dc=nichebit,dc=com").
                    userDnPatterns("uid={0}").
                    contextSource().
                    url("ldap://192.168.2.111:389").
                    managerDn("cn=admin,dc=nichebit,dc=com").
                    managerPassword("nichebit");
        } catch (Exception e) {
            System.out.println("User not Exist");
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests().
                antMatchers("/login", "/refreshtoken").
                permitAll().anyRequest().authenticated().
                and().exceptionHandling().
                and().sessionManagement().
                sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}

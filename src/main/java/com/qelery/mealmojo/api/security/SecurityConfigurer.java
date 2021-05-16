package com.qelery.mealmojo.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.context.WebApplicationContext;

@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

    private JwtRequestFilter jwtRequestFilter;
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    public void setJwtRequestFilter(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Autowired
    public void setUserDetailsService(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/auth/users/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/restaurants").permitAll()
                .antMatchers(HttpMethod.GET, "/api/restaurants/{[0-9]+}").permitAll()
                .antMatchers(HttpMethod.GET, "/api/restaurants/{[0-9]+}/menuitems").permitAll()
                .antMatchers(HttpMethod.GET, "/api/restaurants/{[0-9]+}/menuitems/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/restaurants/{[0-9]+}/menuitems").hasAnyAuthority("MERCHANT")
                .antMatchers(HttpMethod.PUT, "/api/restaurants/{[0-9]+}/menuitems/**").hasAnyAuthority("MERCHANT", "ADMIN")
                .antMatchers(HttpMethod.PATCH, "/api/restaurants/{[0-9]+}/menuitems/**").hasAnyAuthority("MERCHANT", "ADMIN")
                .antMatchers(HttpMethod.GET, "/api/orders").hasAnyAuthority("CUSTOMER", "MERCHANT", "ADMIN")
                .antMatchers(HttpMethod.GET, "/api/restaurants/{[0-9]+}/orders").hasAnyAuthority("CUSTOMER", "MERCHANT", "ADMIN")
                .antMatchers(HttpMethod.POST, "/api/restaurants/{[0-9]+}/orders").hasAnyAuthority("CUSTOMER", "MERCHANT", "ADMIN")
                .antMatchers(HttpMethod.GET, "/api/restaurants/{[0-9]+}/orders/{[0-9]+}").hasAnyAuthority("CUSTOMER", "MERCHANT", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/restaurants/{[0-9]+}/orders/{[0-9]+}").hasAnyAuthority("CUSTOMER", "MERCHANT", "ADMIN")
                .antMatchers(HttpMethod.PATCH, "/api/restaurants/{[0-9]+}/orders/{[0-9]+}").hasAnyAuthority("MERCHANT", "ADMIN")
                .antMatchers(HttpMethod.POST, "/api/restaurants/**").hasAnyAuthority("MERCHANT")
                .antMatchers(HttpMethod.PATCH, "/api/restaurants/**").hasAnyAuthority("MERCHANT", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/restaurants/**").hasAnyAuthority("MERCHANT", "ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable();

//        http.authorizeRequests().antMatchers("/auth/users/**").permitAll().anyRequest()
//                .authenticated()
//                .and()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .csrf().disable();

//        http.authorizeRequests().antMatchers(HttpMethod.GET, "/apid/restaurants").hasRole("USER")
//                .antMatchers("/auth/users", "/auth/users/login", "/auth/users/register", "/api/restaurants").permitAll().anyRequest()
//                .authenticated()
//                .and()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .csrf().disable();

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    @Scope(value= WebApplicationContext.SCOPE_REQUEST, proxyMode=ScopedProxyMode.TARGET_CLASS)
    public UserDetailsImpl userDetailsImpl() {
        return (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}

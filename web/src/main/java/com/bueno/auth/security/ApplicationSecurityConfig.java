/*
 *  Copyright (C) 2022 Lucas B. R. de Oliveira - IFSP/SCL
 *  Contact: lucas <dot> oliveira <at> ifsp <dot> edu <dot> br
 *
 *  This file is part of CTruco (Truco game for didactic purpose).
 *
 *  CTruco is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CTruco is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CTruco.  If not, see <https://www.gnu.org/licenses/>
 */

package com.bueno.auth.security;

import com.bueno.auth.jwt.JwtProperties;
import com.bueno.auth.jwt.JwtTokenHelper;
import com.bueno.auth.jwt.JwtTokenVerifier;
import com.bueno.auth.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import com.bueno.domain.usecases.session.usecase.CreateSessionUseCase;
import com.bueno.domain.usecases.session.usecase.FindSessionUseCase;
import com.bueno.domain.usecases.session.usecase.RefreshSessionUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder encoder;
    private final ApplicationUserService applicationUserService;
    private final SecretKey secretKey;
    private final JwtProperties jwtProperties;
    private final JwtTokenHelper jwtTokenHelper;
    private final CreateSessionUseCase createSessionUseCase;
    private final FindSessionUseCase findSessionUseCase;
    private final RefreshSessionUseCase refreshSessionUseCase;

    public ApplicationSecurityConfig(PasswordEncoder encoder,
                                     ApplicationUserService applicationUserService,
                                     SecretKey secretKey,
                                     JwtProperties jwtProperties, JwtTokenHelper jwtTokenHelper, CreateSessionUseCase createSessionUseCase, FindSessionUseCase findSessionUseCase, RefreshSessionUseCase refreshSessionUseCase) {
        this.encoder = encoder;
        this.applicationUserService = applicationUserService;
        this.secretKey = secretKey;
        this.jwtProperties = jwtProperties;
        this.jwtTokenHelper = jwtTokenHelper;
        this.createSessionUseCase = createSessionUseCase;
        this.findSessionUseCase = findSessionUseCase;
        this.refreshSessionUseCase = refreshSessionUseCase;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtProperties, jwtTokenHelper, createSessionUseCase, findSessionUseCase, refreshSessionUseCase))
                .addFilterAfter(new JwtTokenVerifier(jwtProperties, jwtTokenHelper), JwtUsernameAndPasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/refresh-token").permitAll()
                .antMatchers("/api/v2/**").permitAll()
                .antMatchers("/ws-handshake/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/register").permitAll()
                .antMatchers("/api/v1/**").authenticated()
                .anyRequest()
                .authenticated();

        http.exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        // add this line to use H2 web console
        http.headers().frameOptions().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth){
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(encoder);
        provider.setUserDetailsService(applicationUserService);
        return provider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "https://ctruco-front.onrender.com"
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

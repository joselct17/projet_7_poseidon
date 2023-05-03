package com.nnk.springboot.config;

import com.nnk.springboot.config.oauth.CustomAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration  {

    @Autowired
   private CustomAuth2UserService customAuth2UserService;
    /**
     * Spring Security needs to have a PasswordEncoder defined.
     * @return PasswordEncoder that uses the BCrypt
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests()
                .requestMatchers("/admin").hasRole("ADMIN")
                .requestMatchers("/user/**").hasRole("ADMIN")
                .requestMatchers("/bidList/**", "/curvePoint/**", "/rating/**", "/ruleName/**", "/trade/**", "/app/secure/**").authenticated()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .oauth2Login()
                .userInfoEndpoint().userService(customAuth2UserService)
                .and()
                .defaultSuccessUrl("/")
                .and()
                .httpBasic()
                .and()
                .csrf().disable()
                .build();
    }


}

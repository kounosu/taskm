package com.example.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .oauth2Login()
            .loginPage("/login")//ログイン画面のURL
            .permitAll()
            .defaultSuccessUrl("/app/top", true)//認証が成功した時に遷移するURL
            .and()
            .logout().permitAll();//ログアウト機能
    }
}
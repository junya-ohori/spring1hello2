package com.udemy.spring1hello2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ActuatorSecurity {
	//　mvcMatchers,antMatchersが使えなくなった
	// https://qiita.com/suke_masa/items/908805dd45df08ba28d8#spring-security-5860-antmatchers--mvcmatchers-%E3%81%8C%E9%9D%9E%E6%8E%A8%E5%A5%A8%E5%89%8A%E9%99%A4%E3%81%AB%E3%81%AA%E3%81%A3%E3%81%9F
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/", "/welcome", "/hello").permitAll() // API認証不要
				.requestMatchers("/actuator/**").hasRole("ADMIN") // actuatorはADMINロールユーザのみ
				.anyRequest().denyAll() // 上記以外はアクセスさせない
				)
			.formLogin(); // ログイン認証を実行させる
		return http.build();
	}

	//APIアクセス：actuator/helloは認証不要
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers("/actuator/health");
	}
	
	//actuatorの認証ユーザ、InMemoryUserとはメモリ上＝ハードコード設定ユーザ
	@Bean
	public InMemoryUserDetailsManager userDetailsService() {
		UserDetails user = User.withUsername("admin")
				.password("{noop}admin") //{noop}をつけることで平文を暗号化できる
				.roles("ADMIN")
				.build();
		return new InMemoryUserDetailsManager(user);
	}
}

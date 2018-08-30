package com.cloud.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@SpringBootApplication
@EnableAuthorizationServer
@EnableDiscoveryClient
@EnableResourceServer
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}
	
	@Bean
	public TokenStore tokenStore() {
	    return new InMemoryTokenStore();
	}

	@Bean
	public DefaultTokenServices tokenServices() {
	    final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
	    defaultTokenServices.setAccessTokenValiditySeconds(-1);

	    defaultTokenServices.setTokenStore(tokenStore());
	    return defaultTokenServices;
	}
}

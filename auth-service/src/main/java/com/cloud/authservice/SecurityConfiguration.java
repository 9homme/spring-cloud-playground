package com.cloud.authservice;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@EnableAuthorizationServer
@EnableResourceServer
@EnableWebSecurity(debug = true)
public class SecurityConfiguration {

	@Configuration
	protected static class WebMvcConfiguration implements WebMvcConfigurer {

		@Override
		public void addViewControllers(final ViewControllerRegistry registry) {
			registry.addViewController("/login").setViewName("login");
			registry.addViewController("/").setViewName("main");
			registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		}

	}

	@Configuration
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

		private static final String RESOURCE_ID = "client";

		@Autowired
		private TokenStore tokenStore;

		@Override
		public void configure(final ResourceServerSecurityConfigurer resources) throws Exception {
			resources.tokenStore(tokenStore);
			resources.resourceId(RESOURCE_ID);
		}

		@Override
		public void configure(final HttpSecurity http) throws Exception {
			http.cors().and().
			requestMatchers().antMatchers("/resource/**")
			.and().authorizeRequests().antMatchers("/resource/**")
					.access("hasRole('ADMIN') or hasRole('USER')").and().exceptionHandling()
					.accessDeniedHandler(new OAuth2AccessDeniedHandler());
			;
		}

	}

	@Configuration
	protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

		@Autowired
		private AuthenticationManager authenticationManager;

		@Autowired
		private UserApprovalHandler userApprovalHandler;

		@Autowired
		private TokenStore tokenStore;

		@Override
		public void configure(final AuthorizationServerSecurityConfigurer security) throws Exception {
			security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
		}

		@Override
		public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
			clients.inMemory()
			.withClient("client")
			.secret("{noop}secret")
					.autoApprove(true)
					.authorizedGrantTypes("authorization_code", "client_credentials", "password", "refresh_token","implicit")
					.scopes("read", "write");
		}

		@Override
		public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler)
					.authenticationManager(authenticationManager);
		}

	}

	@Configuration
	protected static class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

		@Autowired
		private ClientDetailsService clientDetailsService;

		@Override
		protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
			auth.inMemoryAuthentication()
			.withUser("user")
			.password("{noop}password")
			.roles("USER").and()
			.withUser("admin")
			.password("{noop}password")
			.roles("ADMIN");
		}

		@Override
		@Bean
		public AuthenticationManager authenticationManagerBean() throws Exception {
			return super.authenticationManagerBean();
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

		@Bean
		@Autowired
		public TokenStoreUserApprovalHandler userApprovalHandler(TokenStore tokenStore) {
			TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();
			handler.setTokenStore(tokenStore);
			handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
			handler.setClientDetailsService(clientDetailsService);
			return handler;
		}

		@Bean
		@Autowired
		public ApprovalStore approvalStore(TokenStore tokenStore) throws Exception {
			TokenApprovalStore store = new TokenApprovalStore();
			store.setTokenStore(tokenStore);
			return store;
		}

		@Bean
		CorsConfigurationSource corsConfigurationSource() {
			CorsConfiguration configuration = new CorsConfiguration();
			configuration.setAllowedHeaders(Arrays.asList("*"));
			configuration.setAllowedOrigins(Arrays.asList("*"));
			configuration.setAllowedMethods(Arrays.asList("*"));
			UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
			source.registerCorsConfiguration("/**", configuration);
			return source;
		}
		

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable().cors().and()
			.requestMatchers().antMatchers("/login", "/logout", "/oauth/**", "/")
			.and().authorizeRequests().antMatchers("/login", "/logout", "/oauth/token").permitAll()
			.anyRequest().authenticated()
			.and().formLogin()
			.loginPage("/login").permitAll()
			.and().logout().logoutUrl("/logout").permitAll();
		}
	}
}
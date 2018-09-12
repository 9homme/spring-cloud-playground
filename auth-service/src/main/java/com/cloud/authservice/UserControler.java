package com.cloud.authservice;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserControler {

	@Autowired
    private DefaultTokenServices tokenServices;

    @Autowired
    private TokenStore tokenStore;
    	
	@RequestMapping("/resource/user")
    public Principal user(Principal user) {
        return user;
    }
	
	@RequestMapping("/resource/user/logout")
    public void revokeToken() {
        final OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder
                .getContext().getAuthentication();
        final String token = tokenStore.getAccessToken(auth).getValue();
        System.out.println("#############################################=>Logout Token:"+token);
        tokenServices.revokeToken(token);   
    }
}

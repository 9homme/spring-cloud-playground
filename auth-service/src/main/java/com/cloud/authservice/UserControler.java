package com.cloud.authservice;

import java.security.Principal;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserControler {

	@RequestMapping("/user")
    public Principal user(Principal user) {
		System.out.println("user ==================>"+user.getName());
        return user;
    }
}

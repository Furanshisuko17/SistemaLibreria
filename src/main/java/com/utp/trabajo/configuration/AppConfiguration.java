
package com.utp.trabajo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfiguration {
	
	@Bean
	public BCryptPasswordEncoder BCryptpasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

}

package com.app.caresync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.app.caresync.model.UserRole;

@org.springframework.cloud.openfeign.EnableFeignClients
@SpringBootApplication
public class AuthServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

	//@org.springframework.context.annotation.Bean
//	org.springframework.boot.CommandLineRunner initAdmin(com.app.caresync.repository.UserRepository repository) {
//		return args -> {
//			String specificEmail = "manish@gmail.com"; // REPLACE THIS WITH YOUR EMAIL
//			repository.findByEmail(specificEmail).ifPresent(user -> {
//				user.setRole(com.app.caresync.model.UserRole.DOCTOR);
//				repository.save(user);
//				System.out.println("ROLE ELEVATED: " + specificEmail + " is now an ADMIN!");
//			});
//		};
	//}
}

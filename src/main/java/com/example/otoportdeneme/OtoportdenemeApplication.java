package com.example.otoportdeneme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OtoportdenemeApplication {

    public static void main(String[] args) {
        SpringApplication.run(OtoportdenemeApplication.class, args);
        System.out.println(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("Store123!"));
    }

}

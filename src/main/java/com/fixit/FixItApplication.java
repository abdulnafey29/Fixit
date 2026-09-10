package com.fixit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FixItApplication {

    public static void main(String[] args) {
        SpringApplication.run(FixItApplication.class, args);
        System.out.println("\n========================================================");
        System.out.println("🚀 FixIt: Campus Issue Management System is running!");
        System.out.println("🌐 Web Application: http://localhost:8080");
        System.out.println("📊 H2 Console (if dev profile): http://localhost:8080/h2-console");
        System.out.println("========================================================\n");
    }
}

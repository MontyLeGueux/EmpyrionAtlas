package com.empyrionatlas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // ✅ Enables scheduled tasks
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
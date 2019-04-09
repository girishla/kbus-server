package com.bigmantra.kbus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class KbusApplication {

    public static void main(String[] args) {
        SpringApplication.run(KbusApplication.class, args);
    }

}

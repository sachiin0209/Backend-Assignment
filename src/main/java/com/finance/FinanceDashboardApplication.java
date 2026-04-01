package com.finance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class FinanceDashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceDashboardApplication.class, args);
    }
}

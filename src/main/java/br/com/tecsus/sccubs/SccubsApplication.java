package br.com.tecsus.sccubs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class SccubsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SccubsApplication.class, args);
    }
}

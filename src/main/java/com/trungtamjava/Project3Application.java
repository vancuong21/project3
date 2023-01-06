package com.trungtamjava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // gen createdAt
@EnableCaching // enable caching
public class Project3Application {

    public static void main(String[] args) {
        SpringApplication.run(Project3Application.class, args);

    }

}

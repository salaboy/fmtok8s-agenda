package com.salaboy.conferences.agenda;

import io.zeebe.spring.client.EnableZeebeClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
public class AgendaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgendaServiceApplication.class, args);
    }
}

package com.salaboy.conferences.agenda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salaboy.conferences.agenda.model.AgendaItem;
import com.salaboy.conferences.agenda.repository.AgendaItemRepository;
import com.salaboy.conferences.agenda.service.AgendaItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping
public class AgendaController {

    private final AgendaItemRepository agendaItemRepository;
    private final AgendaItemService agendaItemService;
    private final ObjectMapper objectMapper;

    public AgendaController(
            final AgendaItemRepository agendaItemRepository,
            final ObjectMapper objectMapper,
            final AgendaItemService agendaItemService) {

        this.agendaItemRepository = agendaItemRepository;
        this.objectMapper = objectMapper;
        this.agendaItemService = agendaItemService;
    }

    @PostMapping
    public Mono<String> newAgendaItem(@RequestBody AgendaItem agendaItem) {

        log.info("> New Agenda Item Received: " + agendaItem);

        return agendaItemService.createAgenda(agendaItem);
    }

    @GetMapping
    public Flux<AgendaItem> getAll() {
        return agendaItemRepository.findAll();
    }

    @GetMapping("/day/{day}")
    public Mono<Set<AgendaItem>> getAllByDay(@PathVariable(value = "day", required = true) final String day) {
        return agendaItemRepository.findAllByDay(day).collect(Collectors.toSet());
    }

    @GetMapping("/{id}")
    public Mono<AgendaItem> getById(@PathVariable("id") String id) {

        return agendaItemRepository.findById(id);
    }

    @DeleteMapping("/")
    public Mono<Void> clearAgendaItems() {

        log.info(">>> Deleting all");
        return agendaItemRepository.deleteAll();
    }
}

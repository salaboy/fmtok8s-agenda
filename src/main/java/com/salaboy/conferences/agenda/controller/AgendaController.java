package com.salaboy.conferences.agenda.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salaboy.cloudevents.helper.CloudEventsHelper;
import com.salaboy.conferences.agenda.model.AgendaItem;
import com.salaboy.conferences.agenda.repository.AgendaItemRepository;
import com.salaboy.conferences.agenda.service.AgendaItemService;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping
public class AgendaController {

    private final AgendaItemRepository agendaItemRepository;
    private final AgendaItemService agendaItemService;

    public AgendaController(
            final AgendaItemRepository agendaItemRepository,
            final AgendaItemService agendaItemService) {

        this.agendaItemRepository = agendaItemRepository;
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

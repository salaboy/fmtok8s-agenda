package com.salaboy.conferences.agenda.controller;

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

    @Value("${EVENTS_ENABLED:true}")
    private Boolean eventsEnabled;

    @Value("${K_SINK:http://broker-ingress.knative-eventing.svc.cluster.local/default/default}")
    private String K_SINK;

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

        if(eventsEnabled) {
            CloudEventBuilder cloudEventBuilder = CloudEventBuilder.v1()
                    .withId(UUID.randomUUID().toString())
                    .withTime(OffsetDateTime.now().toZonedDateTime()) // bug-> https://github.com/cloudevents/sdk-java/issues/200
                    .withType("Agenda.ItemCreated")
                    .withSource(URI.create("agenda-service.default.svc.cluster.local"))
                    .withData(agendaItem.toString().getBytes())
                    .withDataContentType("application/json")
                    .withSubject(agendaItem.getId());

            CloudEvent cloudEvent = cloudEventBuilder.build();


            logCloudEvent(cloudEvent);
            WebClient webClient = WebClient.builder().baseUrl(K_SINK).filter(logRequest()).build();

            WebClient.ResponseSpec postCloudEvent = CloudEventsHelper.createPostCloudEvent(webClient, cloudEvent);

            postCloudEvent.bodyToMono(String.class)
                    .doOnError(t -> t.printStackTrace())
                    .doOnSuccess(s -> System.out.println("Result -> " + s))
                    .subscribe();
        }

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

    private void logCloudEvent(CloudEvent cloudEvent) {
        EventFormat format = EventFormatProvider
                .getInstance()
                .resolveFormat(JsonFormat.CONTENT_TYPE);

        log.info("Cloud Event: " + new String(format.serialize(cloudEvent)));

    }

    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: " + clientRequest.method() + " - " + clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info(name + "=" + value)));
            return Mono.just(clientRequest);
        });
    }
}

package com.salaboy.conferences.agenda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.salaboy.conferences.agenda.model.AgendaItem;
import com.salaboy.conferences.agenda.model.Proposal;
import com.salaboy.conferences.agenda.repository.AgendaItemRepository;
import io.zeebe.client.api.response.ActivatedJob;
import io.zeebe.client.api.worker.JobClient;
import io.zeebe.spring.client.annotation.ZeebeWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping
public class AgendaController {

    private final AgendaItemRepository agendaItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public AgendaController(AgendaItemRepository agendaItemRepository) {
        this.agendaItemRepository = agendaItemRepository;
    }

    @PostMapping
    public Mono<String> newAgendaItem(@RequestBody AgendaItem agendaItem) {

        log.info("> New Agenda Item Received: " + agendaItem);

        if(agendaItem.getTitle().contains("fail")){
            throw new IllegalStateException("Something went wrong with adding the Agenda Item: " + agendaItem);
        }

        return agendaItemRepository.save(agendaItem)
                .map(item -> {

                    if (!Strings.isNullOrEmpty(item.getId())) {
                        log.info("> Agenda Item Added to Agenda: " + agendaItem);
                        return "Agenda Item Added to Agenda";
                    } else {
                        log.info("> Agenda Item NOT Added to Agenda: " + agendaItem);
                        return "Agenda Item Not Added";
                    }
                });
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
    public Mono<Void> clearAgendaItems(){

        log.info(">>> Deleting all");
        return agendaItemRepository.deleteAll();
    }

    @ZeebeWorker(name = "agenda-worker", type = "agenda-publish")
    public void newAgendaItemJob(final JobClient client, final ActivatedJob job) {
        Proposal proposal = objectMapper.convertValue(job.getVariablesAsMap().get("proposal"), Proposal.class);
        String[] days = {"Monday", "Tuesday"};
        String[] times = {"9:00 am", "10:00 am", "11:00 am", "1:00 pm", "2:00 pm", "3:00 pm", "4:00 pm", "5:00 pm"};
        Random random = new Random();
        int day = random.nextInt(2);
        int time = random.nextInt(8);
        newAgendaItem(new AgendaItem(proposal.getTitle(), proposal.getAuthor(), days[day], times[time]));
        client.newCompleteCommand(job.getKey()).send();
    }
}

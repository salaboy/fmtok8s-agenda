package com.salaboy.conferences.agenda.service;

import com.google.common.base.Strings;
import com.salaboy.conferences.agenda.model.AgendaItem;
import com.salaboy.conferences.agenda.repository.AgendaItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

@Service
@Slf4j
public class AgendaItemService {

    private final AgendaItemRepository agendaItemRepository;

    public AgendaItemService(AgendaItemRepository agendaItemRepository) {
        this.agendaItemRepository = agendaItemRepository;
    }

    public Mono<String> createAgenda(AgendaItem agendaItem) {
        if (Pattern.compile(Pattern.quote("fail"), Pattern.CASE_INSENSITIVE).matcher(agendaItem.getTitle()).find()) {
            throw new IllegalStateException("Something went wrong with adding the Agenda Item: " + agendaItem);
        }
        return agendaItemRepository.save(agendaItem)
                .doOnSuccess(i -> log.info("> Agenda Item Added to Agenda: {}", i))
                .doOnError(i -> log.info("> Agenda Item NOT Added to Agenda: {}", i))
                .map(i -> !Strings.isNullOrEmpty(i.getId()) ? "Agenda Item Added to Agenda" : "Agenda Item Not Added");
    }
}
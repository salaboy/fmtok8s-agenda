package com.salaboy.conferences.agenda.repository;

import com.salaboy.conferences.agenda.model.AgendaItem;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AgendaItemRepository extends ReactiveMongoRepository<AgendaItem, String> {

    Flux<AgendaItem> findAllByDay(final String day);
}

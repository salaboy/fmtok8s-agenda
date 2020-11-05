package com.salaboy.conferences.agenda.zeebe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salaboy.conferences.agenda.model.AgendaItem;
import com.salaboy.conferences.agenda.model.Proposal;
import com.salaboy.conferences.agenda.service.AgendaItemService;
import com.salaboy.conferences.agenda.service.TimeDayGenerator;
import io.zeebe.client.api.response.ActivatedJob;
import io.zeebe.client.api.worker.JobClient;
import io.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.stereotype.Component;

@Component
public class ZeebeWorkers {

    private final ObjectMapper objectMapper;
    private final AgendaItemService agendaItemService;
    private final TimeDayGenerator timeDayGenerator;

    public ZeebeWorkers(
            final ObjectMapper objectMapper,
            final AgendaItemService agendaItemService,
            final TimeDayGenerator timeDayGenerator) {

        this.objectMapper = objectMapper;
        this.agendaItemService =  agendaItemService;
        this.timeDayGenerator = timeDayGenerator;
    }

    @ZeebeWorker(name = "agenda-worker", type = "agenda-publish")
    public void newAgendaItemJob(final JobClient client, final ActivatedJob job) throws JsonProcessingException {

        Proposal proposal = objectMapper.convertValue(job.getVariablesAsMap().get("proposal"), Proposal.class);

        var time = timeDayGenerator.time();
        var day = timeDayGenerator.day();

        agendaItemService.createAgenda(new AgendaItem(proposal.getTitle(), proposal.getAuthor(), day, time))
                .subscribe(i -> {
                    client.newCompleteCommand(job.getKey()).send();
                });
    }
}

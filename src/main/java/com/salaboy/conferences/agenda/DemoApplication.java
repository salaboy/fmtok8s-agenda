package com.salaboy.conferences.agenda;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salaboy.conferences.agenda.model.AgendaItem;
import com.salaboy.conferences.agenda.model.Proposal;
//import io.zeebe.client.api.response.ActivatedJob;
//import io.zeebe.client.api.worker.JobClient;
//import io.zeebe.spring.client.EnableZeebeClient;
//import io.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
@RestController
//@EnableZeebeClient
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Value("${version:0.0.0}")
    private String version;

    private ObjectMapper objectMapper = new ObjectMapper();
    private Set<AgendaItem> agendaItems = new TreeSet<>(new Comparator<AgendaItem>() {
        @Override
        public int compare(AgendaItem t, AgendaItem t1) {
            return t.getTime().compareTo(t1.getTime());
        }
    });


    @GetMapping("/info")
    public String infoWithVersion() {
        return "Agenda v" + version;
    }

    @PostMapping()
    public String newAgendaItem(@RequestBody AgendaItem agendaItem) {
        System.out.println("> New Agenda Received: " + agendaItem);
        agendaItems.add(agendaItem);
        return "Agenda Item Added to Agenda";
    }

    @GetMapping()
    public Set<AgendaItem> getAll() {
        return agendaItems;
    }

    @GetMapping("/{day}")
    public Set<AgendaItem> getAllByDay(@PathVariable(value = "day", required = true) String day) {
        return agendaItems.stream().filter(a -> a.getDay().equals(day)).collect(Collectors.toSet());
    }

    @GetMapping("/{id}")
    public Optional<AgendaItem> getById(@PathVariable("id") String id) {
        return agendaItems.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

//    @ZeebeWorker(name = "agenda-worker", type = "agenda-publish")
//    public void newAgendaItemJob(final JobClient client, final ActivatedJob job) {
//        Proposal proposal = objectMapper.convertValue(job.getVariablesAsMap().get("proposal"), Proposal.class);
//        newAgendaItem(new AgendaItem(proposal.getTitle(), proposal.getAuthor(), new Date()));
//        client.newCompleteCommand(job.getKey()).send();
//    }


}

package com.salaboy.conferences.agenda;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salaboy.conferences.agenda.model.AgendaItem;
import com.salaboy.conferences.agenda.model.Proposal;
import io.zeebe.client.api.response.ActivatedJob;
import io.zeebe.client.api.worker.JobClient;
import io.zeebe.spring.client.EnableZeebeClient;
import io.zeebe.spring.client.annotation.ZeebeWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
@RestController
@EnableZeebeClient
@Slf4j
public class AgendaService {

    public static void main(String[] args) {
        SpringApplication.run(AgendaService.class, args);
    }

    @Value("${version:0.0.0}")
    private String version;

    private ObjectMapper objectMapper = new ObjectMapper();
    private Set<AgendaItem> agendaItems = new TreeSet<>(Comparator.comparing(AgendaItem::getTime).thenComparing(AgendaItem::getId));


    @GetMapping("/info")
    public String getInfo() {
        return "{ \"name\" : \"Agenda Service\", \"version\" : \"v" + version + "\", \"source\": \"https://github.com/salaboy/fmtok8s-agenda/releases/tag/v" + version + "\" }";
    }

    @PostMapping()
    public String newAgendaItem(@RequestBody AgendaItem agendaItem) {
        log.info("> New Agenda Item Received: " + agendaItem);
        if(agendaItem.getTitle().contains("fail")){
            throw new IllegalStateException("Something went wrong with adding the Agenda Item: " + agendaItem);
        }
        boolean added = agendaItems.add(agendaItem);
        if(added) {
            log.info("> Agenda Item Added to Agenda: " + agendaItem);
            return "Agenda Item Added to Agenda";
        }else{
            log.info("> Agenda Item NOT added to Agenda: "+ agendaItem);
            return "Agenda Item Not added";
        }
    }

    @GetMapping()
    public Set<AgendaItem> getAll() {
        return agendaItems;
    }

    @GetMapping("/day/{day}")
    public Set<AgendaItem> getAllByDay(@PathVariable(value = "day", required = true) String day) {
        return agendaItems.stream().filter(a -> a.getDay().equals(day)).collect(Collectors.toSet());
    }

    @GetMapping("/{id}")
    public Optional<AgendaItem> getById(@PathVariable("id") String id) {
        return agendaItems.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    @DeleteMapping("/")
    public void clearAgendaItems(){
        agendaItems.clear();
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

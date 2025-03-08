package br.com.tecsus.sccubs.controllers;

import br.com.tecsus.sccubs.jobs.ContemplationSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScheduleController {

    private final ContemplationSchedule contemplationSchedule;

    @Autowired
    public ScheduleController(ContemplationSchedule contemplationSchedule) {
        this.contemplationSchedule = contemplationSchedule;
    }

    @GetMapping("/contemplation-management/test/schedule")
    public ResponseEntity<String> start() {
        contemplationSchedule.processContemplationTask();
        return ResponseEntity.ok("executado");
    }
}

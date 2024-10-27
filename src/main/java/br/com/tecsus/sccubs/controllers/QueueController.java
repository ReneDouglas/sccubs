package br.com.tecsus.sccubs.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.annotation.SessionScope;

@Slf4j
@Controller
@SessionScope
public class QueueController {

    public QueueController() {

    }

    @GetMapping
    public String getQueuePage() {
        return "queueManagement/queue-management";
    }

}

package br.com.tecsus.sccubs.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.annotation.SessionScope;

@Slf4j
@Controller
@SessionScope
public class ContemplationController {

    @GetMapping("/contemplation-management")
    public String getContemplationPage(Model model) {
        return "contemplationManagement/contemplation-management";
    }

}

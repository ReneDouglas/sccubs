package br.com.tecsus.sccubs.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String getHomePage(){
        return "home";
    }

    @GetMapping("/error")
    public String getError() {
        return "error";
    }

    @GetMapping("/expired")
    public String getExpired() {
        return "expired";
    }

    @GetMapping("/invalid")
    public String getInvalid() {
        return "invalid";
    }
}

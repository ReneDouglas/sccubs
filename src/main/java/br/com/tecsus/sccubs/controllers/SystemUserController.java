package br.com.tecsus.sccubs.controllers;

import br.com.tecsus.sccubs.entities.SystemUser;
import br.com.tecsus.sccubs.services.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SystemUserController {

    @Autowired
    private SystemUserService systemUserService;

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }

    @GetMapping("/registration")
    public String getRegistrationPage(Model model) {
        model.addAttribute("user", new SystemUser());
        return "registration";

    }

    @PostMapping("/registration")
    public String registerSystemUser(@ModelAttribute SystemUser user) {

        systemUserService.register(user);
        return "redirect:/login?sucess";
    }

    @GetMapping("/admin")
    public String getAdminPage() {
        return "admin";
    }

    @GetMapping("/user")
    public String getUserPage() {
        return "user";
    }
}

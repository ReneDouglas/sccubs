package br.com.tecsus.sccubs.controllers;

import br.com.tecsus.sccubs.entities.SystemUser;
import br.com.tecsus.sccubs.enums.SystemMessages;
import br.com.tecsus.sccubs.services.SystemUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@Controller
public class SessionController {

    @Autowired
    private SystemUserService systemUserService;

    @GetMapping("/login")
    public String getLoginPage() {
        return "sessionManagement/login";
    }

    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "sessionManagement/login";
    }

    @GetMapping("/")
    public String getHomePage(){
        return "home";
    }

    @GetMapping("/error")
    public String getError() {
        return "error2";
    }

    @GetMapping("/expired")
    public String getExpired() {
        return "sessionManagement/expired";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/user")
    public String getRegistrationPage(Model model, @RequestParam(value = "msg", required = false) String msg) {

        if (msg != null) {
            model.addAttribute("message", SystemMessages.getDescription(msg));
        }
        model.addAttribute("systemUser", new SystemUser());
        model.addAttribute("rolesList", systemUserService.getRolesNotAdmin());
        return "sessionManagement/registration";

    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/user/update")
    public String updateSystemUser() {
        return null;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/user/create")
    public String registerSystemUser(@ModelAttribute SystemUser user) {

        try {
            String msg = systemUserService.registerNotAdminUser(user);
            return "redirect:/user?msg=%s".formatted(msg);
        } catch (Exception e) {
            return "redirect:/user?msg=%s".formatted(SystemMessages.ERROR_01.getCode());
        }
        //systemUserService.register(user);
        //System.out.println(user.toString());
        //return "redirect:/login?sucess";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin")
    public String getAdminPage() {
        return "admin";
    }

    /*@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/user")
    public String getUserPage() {
        return "user";
    }*/
}

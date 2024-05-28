package br.com.tecsus.sccubs.controllers;

import br.com.tecsus.sccubs.entities.SystemUser;
import br.com.tecsus.sccubs.enums.SystemMessages;
import br.com.tecsus.sccubs.services.SystemUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;


@Slf4j
@Controller
public class SessionController {

    @Autowired
    private SystemUserService systemUserService;

    @GetMapping("/login")
    public String getLoginPage() {
        return "sessionManagement/login";
    }

    @GetMapping("/logout")
    public String logout() {
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
    public String getRegistrationPage(Model model) {

        model.addAttribute("systemUser", new SystemUser());
        model.addAttribute("rolesList", systemUserService.getRolesNotAdminAndNotGestao());
        return "sessionManagement/systemUsers-insert";

    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/user/create")
    public String registerSystemUser(@ModelAttribute SystemUser user, RedirectAttributes redirectAttributes) {

        try {
            systemUserService.registerNotAdminUser(user);
            redirectAttributes.addFlashAttribute("message", "Usuário cadastrado com sucesso.");
            log.info("Cadastro de usuário realizado com sucesso.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("message", "Usuário já cadastrado no sistema.");
            log.error("Usuário já cadastrado: {}", e.getMessage());
        }catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Erro ao cadastrar usuário.");
            log.error("Erro ao cadastrar usuário: {}", e.getMessage());
        }
        //return new RedirectView("/user");
        return "redirect:/user";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/users")
    public String listSystemUsers(Model model) {

        model.addAttribute("systemUsers", systemUserService.findAllUsersByCreationUser());
        return "sessionManagement/systemUsers-list";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/user/to-edit")
    public String requestUpdateSystemUser(@RequestParam("id") Long id, Model model) {

        model.addAttribute("systemUser", systemUserService.findSystemUserById(id));
        model.addAttribute("rolesList", systemUserService.getRolesNotAdminAndNotGestao());
        return "sessionManagement/systemUsers-insert";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/user/update")
    public String updateSystemUser(@ModelAttribute SystemUser user, RedirectAttributes redirectAttributes) {
        return null;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/user/delete")
    public String deleteSystemUser(@RequestParam("id") Long id) {
        return null;
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

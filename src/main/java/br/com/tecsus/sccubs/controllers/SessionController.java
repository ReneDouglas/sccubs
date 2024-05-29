package br.com.tecsus.sccubs.controllers;

import br.com.tecsus.sccubs.entities.SystemUser;
import br.com.tecsus.sccubs.services.SystemUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Slf4j
@Controller
public class SessionController {

    private final SystemUserService systemUserService;

    @Autowired
    public SessionController(SystemUserService systemUserService) {
        this.systemUserService = systemUserService;
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "sessionManagement/login";
    }

    @GetMapping("/logout")
    public String getLogoutPage() {
        return "sessionManagement/login";
    }

    @GetMapping("/login-error")
    public String getLoginErrorPage(Model model) {
        model.addAttribute("loginError", true);
        return "sessionManagement/login";
    }

    @GetMapping("/")
    public String getHomePage(){
        return "home";
    }

    @GetMapping("/error")
    public String getErrorPage() {
        return "error2";
    }

    @GetMapping("/expired")
    public String getExpiredPage() {
        return "sessionManagement/expired";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/systemUser-insert")
    public String getSystemUserInsertPage(Model model) {

        model.addAttribute("systemUser", new SystemUser());
        model.addAttribute("rolesList", systemUserService.getRolesNotAdminAndNotGestao());
        return "sessionManagement/systemUser-insert";

    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/systemUser-insert/create")
    public String registerSystemUser(@ModelAttribute SystemUser systemUser, RedirectAttributes redirectAttributes) {

        try {
            systemUserService.registerNotAdminSystemUser(systemUser);
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
        return "redirect:/systemUser-insert";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/systemUser-list")
    public String getSystemUserListPage(Model model) {

        model.addAttribute("systemUsers", systemUserService.findAllUsersByCreationUser());
        return "sessionManagement/systemUser-list";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/systemUser-insert")
    public String getSystemUserInsertPageToUpdate(@RequestParam("id") Long id, Model model) {

        model.addAttribute("systemUser", systemUserService.findSystemUserById(id));
        model.addAttribute("rolesList", systemUserService.getRolesNotAdminAndNotGestao());
        return "sessionManagement/systemUser-insert";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/systemUser-insert/update")
    public String updateSystemUser(@ModelAttribute SystemUser systemUser, RedirectAttributes redirectAttributes) {

        try {
            systemUserService.updateNotAdminSystemUser(systemUser);
            redirectAttributes.addFlashAttribute("message", "Usuário atualizado com sucesso.");
            log.info("Usuário atualizado com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Erro ao atualizar usuário.");
            log.error("Erro ao atualizar usuário: {}", e.getMessage());
        }
        //return new RedirectView("/user");
        return "redirect:/systemUser-insert";
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/systemUser-list/delete")
    public String deleteSystemUser(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {

        try {
            systemUserService.deleteNotAdminSystemUser(id);
            redirectAttributes.addFlashAttribute("message", "Usuário deletado com sucesso.");
            log.info("Usuário deletado com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Erro ao deletar usuário.");
            log.error("Erro ao deletar usuário: {}", e.getMessage());
        }

        return "redirect:/systemUser-list";
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

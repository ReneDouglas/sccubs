package br.com.tecsus.sccubs.controllers;

import br.com.tecsus.sccubs.entities.SystemUser;
import br.com.tecsus.sccubs.services.BasicHealthUnitService;
import br.com.tecsus.sccubs.services.SystemUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Slf4j
@Controller
public class SessionController {

    private final SystemUserService systemUserService;
    private final BasicHealthUnitService basicHealthUnitService;

    @Autowired
    public SessionController(SystemUserService systemUserService, BasicHealthUnitService basicHealthUnitService) {
        this.systemUserService = systemUserService;
        this.basicHealthUnitService = basicHealthUnitService;
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
    public String getHomePage() {
        return "home";
    }

    @GetMapping("/error")
    public String getErrorPage() {
        return "error";
    }

    @GetMapping("/expired")
    public String getExpiredPage() {
        return "sessionManagement/expired";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SMS')")
    @GetMapping("/systemUser-insert")
    public String getSystemUserInsertPage(Model model) {

        model.addAttribute("systemUser", new SystemUser());
        model.addAttribute("rolesList", systemUserService.getRolesNotAdminAndNotManagement());
        model.addAttribute("basicHealthUnits", basicHealthUnitService
                .findBasicHealthUnitsByCityHallOfLoggedSystemUser());
        return "sessionManagement/systemUser-insert";

    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SMS')")
    @PostMapping("/systemUser-insert/create")
    public String registerSystemUser(@ModelAttribute SystemUser systemUser,
                                     RedirectAttributes redirectAttributes) {

        try {
            systemUserService.registerNotAdminSystemUser(systemUser);
            redirectAttributes.addFlashAttribute("message", "Usuário cadastrado com sucesso.");
            log.info("Cadastro de usuário realizado com sucesso.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("message", "Usuário já cadastrado no sistema.");
            log.error("Usuário já cadastrado: {}", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Erro ao cadastrar usuário.");
            log.error("Erro ao cadastrar usuário: {}", e.getMessage());
        }
        //return new RedirectView("/user");
        return "redirect:/systemUser-insert";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SMS')")
    @GetMapping("/systemUser-list")
    public String getSystemUserListPage(Model model,
                                        @ModelAttribute SystemUser systemUser,
                                        @RequestParam(value = "page", defaultValue = "0",required = false) int currentPage,
                                        @RequestParam(value = "size", defaultValue = "5",required = false) int pageSize,
                                        HttpServletRequest request) {

        Page<SystemUser> systemUsersPage;
        int totalPages;

        model.addAttribute("systemUser", new SystemUser());
        model.addAttribute("rolesList", systemUserService.getRolesNotAdminAndNotManagement());
        model.addAttribute("basicHealthUnits", basicHealthUnitService
                .findBasicHealthUnitsByCityHallOfLoggedSystemUser());

        systemUser.setCreationUser(SecurityContextHolder.getContext().getAuthentication().getName());
        systemUser.setName(systemUser.getName() == null || systemUser.getName().isEmpty() ? null : systemUser.getName());
        systemUser.setUsername(systemUser.getUsername() == null || systemUser.getUsername().isEmpty() ? null : systemUser.getUsername());

        systemUsersPage = systemUserService
                .findAllUsersByCreationUserPaginated(systemUser, PageRequest.of(currentPage, pageSize, Sort.Direction.valueOf("DESC"), "creationDate"));
        model.addAttribute("systemUsersPage", systemUsersPage);

        totalPages = systemUsersPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        if("searchRequest".equals(request.getHeader("X-Requested-With"))) {
            return "sessionManagement/sessionFragments/systemUserList-datatable :: systemUser-datatable";
        } else {
            return "sessionManagement/systemUser-list";
        }

    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SMS')")
    @GetMapping("/tutorials")
    public String getSystemUserListPageTest(Model model, @RequestParam(required = false) String keyword,
                                        @ModelAttribute SystemUser systemUser,
                                        @RequestParam(value = "page", defaultValue = "0",required = false) int page,
                                        @RequestParam(value = "size", defaultValue = "5",required = false) int size,
                                        HttpServletRequest request) {

        Page<SystemUser> systemUsersPage;
        List<SystemUser> suList;

        int totalPages;

        model.addAttribute("systemUser", new SystemUser());
        model.addAttribute("rolesList", systemUserService.getRolesNotAdminAndNotManagement());
        model.addAttribute("basicHealthUnits", basicHealthUnitService
                .findBasicHealthUnitsByCityHallOfLoggedSystemUser());

        systemUser.setCreationUser(SecurityContextHolder.getContext().getAuthentication().getName());
        systemUser.setName(systemUser.getName());
        systemUser.setUsername(systemUser.getUsername());

        systemUsersPage = systemUserService
                .findAllUsersByCreationUserPaginated(systemUser, PageRequest.of(page, size, Sort.Direction.valueOf("DESC"), "creationDate"));

        suList = systemUsersPage.getContent();

        model.addAttribute("systemUsersPage", suList);
        model.addAttribute("currentPage", systemUsersPage.getNumber() + 1);
        model.addAttribute("totalItems", systemUsersPage.getTotalElements());
        model.addAttribute("totalPages", systemUsersPage.getTotalPages());
        model.addAttribute("pageSize", size);

        return "sessionManagement/systemUser-listV2";


    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SMS')")
    @PostMapping("/systemUser-insert")
    public String getSystemUserInsertPageToUpdate(@RequestParam("id") Long id, Model model) {

        model.addAttribute("systemUser", systemUserService.findSystemUserById(id));
        model.addAttribute("rolesList", systemUserService.getRolesNotAdminAndNotManagement());
        model.addAttribute("basicHealthUnits", basicHealthUnitService
                .findBasicHealthUnitsByCityHallOfLoggedSystemUser());

        return "sessionManagement/systemUser-insert";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SMS')")
    @PostMapping("/systemUser-insert/update")
    public String updateSystemUser(@ModelAttribute SystemUser systemUser,
                                   RedirectAttributes redirectAttributes) {

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

    @PreAuthorize("hasAnyRole('ADMIN', 'SMS')")
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

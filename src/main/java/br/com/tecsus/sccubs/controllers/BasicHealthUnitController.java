package br.com.tecsus.sccubs.controllers;

import br.com.tecsus.sccubs.dtos.UBSsystemUserDTO;
import br.com.tecsus.sccubs.entities.BasicHealthUnit;
import br.com.tecsus.sccubs.security.SystemUserDetails;
import br.com.tecsus.sccubs.services.BasicHealthUnitService;
import br.com.tecsus.sccubs.services.CityHallService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Slf4j
@Controller
public class BasicHealthUnitController {

    private final BasicHealthUnitService basicHealthUnitService;
    private final CityHallService cityHallService;

    @Autowired
    public BasicHealthUnitController(BasicHealthUnitService basicHealthUnitService, CityHallService cityHallService) {
        this.basicHealthUnitService = basicHealthUnitService;
        this.cityHallService = cityHallService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SMS')")
    @GetMapping("/basicHealthUnit-management")
    public String getBasicHealthUnitPage(Model model, @AuthenticationPrincipal SystemUserDetails loggedUser) {

        BasicHealthUnit ubs = new BasicHealthUnit();
        ubs.setCityHall(cityHallService.findById(loggedUser.getCityHallId()));

        model.addAttribute("basicHealthUnit", ubs);
        model.addAttribute("basicHealthUnits", basicHealthUnitService
                .findBasicHealthUnitsByCityHallOfLoggedSystemUser());
        model.addAttribute("ubsUsers", List.of());

        return "basicHealthUnitManagement/basicHealthUnit-management";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SMS')")
    @PostMapping("/basicHealthUnit-management/create")
    public String registerBasicHealthUnit(@ModelAttribute BasicHealthUnit basicHealthUnit,
                                          @AuthenticationPrincipal SystemUserDetails loggedUser,
                                          RedirectAttributes redirectAttributes) {

        try {
            basicHealthUnitService.registerBasicHealthUnit(basicHealthUnit, loggedUser);
            redirectAttributes.addFlashAttribute("message", "UBS Cadastrada com sucesso.");
            log.info("UBS Cadastrada com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Erro ao cadastrar UBS.");
            log.error("Erro ao cadastrar UBS: {}", e.getMessage());
        }

        return "redirect:/basicHealthUnit-management";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SMS')")
    @PostMapping("/basicHealthUnit-management")
    public String getBasicHealthUnitPageToUpdate(@RequestParam("basicHealthUnit") Long basicHealthUnit,
                                                 Model model,
                                                 HttpServletRequest request) {

        model.addAttribute("basicHealthUnit", basicHealthUnitService.findById(basicHealthUnit));
        model.addAttribute("basicHealthUnits", basicHealthUnitService
                .findBasicHealthUnitsByCityHallOfLoggedSystemUser());
        model.addAttribute("ubsUsers", List.of());

        return "basicHealthUnitManagement/basicHealthUnit-management";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SMS')")
    @PostMapping("/basicHealthUnit-management/update")
    public String updateSystemUser(@ModelAttribute BasicHealthUnit basicHealthUnit,
                                   RedirectAttributes redirectAttributes) {
        return null;
    }

    @GetMapping(value = "/basicHealthUnit-management/users", produces = MediaType.TEXT_HTML_VALUE)
    public String getUBSsystemUsers(@RequestParam("basicHealthUnit") String basicHealthUnit,
                                    Model model) {

        List<UBSsystemUserDTO> ubsUsers = List.of();

        if (!basicHealthUnit.isEmpty()) {
            ubsUsers = basicHealthUnitService.findUBSsystemUsersByUBSid(Long.valueOf(basicHealthUnit));
        }

        model.addAttribute("ubsUsers", ubsUsers);

        if (ubsUsers.isEmpty()) {
            return "basicHealthUnitManagement/ubsFragments/systemUsersUBSTable :: emptySystemUsersUBStable";
        }

        return "basicHealthUnitManagement/ubsFragments/systemUsersUBSTable :: systemUsersUBStable";
    }



}

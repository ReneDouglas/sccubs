package br.com.tecsus.sccubs.controllers;

import br.com.tecsus.sccubs.dtos.AvailableMedicalSlotsFormDTO;
import br.com.tecsus.sccubs.entities.MedicalSlot;
import br.com.tecsus.sccubs.security.SystemUserDetails;
import br.com.tecsus.sccubs.services.BasicHealthUnitService;
import br.com.tecsus.sccubs.services.MedicalSlotService;
import br.com.tecsus.sccubs.services.SpecialtyService;
import br.com.tecsus.sccubs.services.exceptions.DistinctAvailableMedicalSlotException;
import br.com.tecsus.sccubs.utils.DefaultValues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@SessionScope
public class MedicalSlotController {

    private final BasicHealthUnitService basicHealthUnitService;
    private final SpecialtyService specialtyService;
    private final MedicalSlotService medicalSlotService;
    private AvailableMedicalSlotsFormDTO availableMedicalSlotsFormDTO;

    public MedicalSlotController(BasicHealthUnitService basicHealthUnitService, SpecialtyService specialtyService, MedicalSlotService medicalSlotService) {
        this.basicHealthUnitService = basicHealthUnitService;
        this.specialtyService = specialtyService;
        this.medicalSlotService = medicalSlotService;
    }

    @GetMapping("/medicalSlot-management")
    public String getMedicalSlotPage(Model model) {

        this.availableMedicalSlotsFormDTO = null;

        model.addAttribute("basicHealthUnits", basicHealthUnitService.findAllUBS());
        model.addAttribute("specialties", specialtyService.findSpecialties());
        model.addAttribute("medicalSlotsPage", medicalSlotService.findMedicalSlotsPaginated(PageRequest.of(0, DefaultValues.PAGE_SIZE)));

        return "medicalSlotManagement/medicalSlot-management";
    }

    @PostMapping("/medicalSlot-management/slots/add")
    public String addAvailableMedicalSlotsRow(@ModelAttribute MedicalSlot availableMedicalSlot,
                                              Model model) {

        if (availableMedicalSlotsFormDTO == null) {
            this.availableMedicalSlotsFormDTO = new AvailableMedicalSlotsFormDTO();
        }
        this.availableMedicalSlotsFormDTO.addRow(availableMedicalSlot);
        model.addAttribute("availableMedicalSlotsForm", availableMedicalSlotsFormDTO);

        return "medicalSlotManagement/medicalSlotFragments/availableMedicalSlotsForm :: availableMedicalSlotsFormTable";

    }

    @PostMapping("/medicalSlot-management/slots/create")
    public String registerAvailableMedicalSlots(@ModelAttribute AvailableMedicalSlotsFormDTO availableMedicalSlotsFormDTO,
                                                @AuthenticationPrincipal SystemUserDetails loggedUser,
                                                RedirectAttributes redirectAttributes) {

        try {
            medicalSlotService.registerAvailableMedicalSlotsBatch(availableMedicalSlotsFormDTO, loggedUser);
            redirectAttributes.addFlashAttribute("message", "Vagas registradas com sucesso.");
            redirectAttributes.addFlashAttribute("error", false);
            log.info("Vagas registradas com sucesso.");
        } catch (DistinctAvailableMedicalSlotException e) {
            log.error("Erro ao registrar vagas: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("message", "Erro ao registrar vagas: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", true);
        }

        this.availableMedicalSlotsFormDTO = null;
        return "redirect:/medicalSlot-management";
    }

    @GetMapping("/medicalSlot-management/slots/{index}/remove")
    public String removeRowtByIndex(@PathVariable int index,
                                    Model model) {
        this.availableMedicalSlotsFormDTO.removeRow(index);
        model.addAttribute("availableMedicalSlotsForm", this.availableMedicalSlotsFormDTO);
        return "medicalSlotManagement/medicalSlotFragments/availableMedicalSlotsForm :: availableMedicalSlotsFormTable";
    }

    @GetMapping("/medicalSlot-management/paginated")
    public String getMedicalSlotsPaginated(Model model,
                                           @RequestParam(value = "page", defaultValue = "0", required = false) int currentPage,
                                           @RequestParam(value = "pageSize", defaultValue = "" + DefaultValues.PAGE_SIZE, required = false) int pageSize) {

        model.addAttribute("medicalSlotsPage", medicalSlotService.findMedicalSlotsPaginated(PageRequest.of(currentPage, pageSize)));
        return "medicalSlotManagement/medicalSlotFragments/medicalSlot-datatable :: medicalSlotsDatatable";
    }
}

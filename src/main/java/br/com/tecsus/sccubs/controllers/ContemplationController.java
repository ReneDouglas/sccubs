package br.com.tecsus.sccubs.controllers;

import br.com.tecsus.sccubs.entities.BasicHealthUnit;
import br.com.tecsus.sccubs.entities.Contemplation;
import br.com.tecsus.sccubs.entities.Specialty;
import br.com.tecsus.sccubs.enums.ProcedureType;
import br.com.tecsus.sccubs.security.SystemUserDetails;
import br.com.tecsus.sccubs.services.BasicHealthUnitService;
import br.com.tecsus.sccubs.services.ContemplationService;
import br.com.tecsus.sccubs.services.SpecialtyService;
import br.com.tecsus.sccubs.services.exceptions.CancelContemplationException;
import br.com.tecsus.sccubs.services.exceptions.ConfirmContemplationException;
import br.com.tecsus.sccubs.utils.DefaultValues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Slf4j
@Controller
@SessionScope
public class ContemplationController {

    private final ContemplationService contemplationService;
    private final List<BasicHealthUnit> basicHealthUnits;
    private final List<Specialty> specialties;

    @Autowired
    public ContemplationController(BasicHealthUnitService basicHealthUnitService, SpecialtyService specialtyService, ContemplationService contemplationService) {
        this.contemplationService = contemplationService;
        this.basicHealthUnits = basicHealthUnitService.findAllUBS();
        this.specialties = specialtyService.findSpecialties();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SMS')")
    @GetMapping("/contemplation-management")
    public String getContemplationPage(Model model) {

        model.addAttribute("basicHealthUnits", this.basicHealthUnits);
        model.addAttribute("specialties", this.specialties);
        model.addAttribute("consultasPage", new PageImpl<Contemplation>(List.of(), PageRequest.of(0, DefaultValues.PAGE_SIZE), 0));
        model.addAttribute("examesPage", new PageImpl<Contemplation>(List.of(), PageRequest.of(0, DefaultValues.PAGE_SIZE), 0));
        model.addAttribute("cirurgiasPage", new PageImpl<Contemplation>(List.of(), PageRequest.of(0, DefaultValues.PAGE_SIZE), 0));
        model.addAttribute("hide", "hidden");
        return "contemplationManagement/contemplation-management";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SMS')")
    @GetMapping("/contemplation-management/search")
    public String loadSearchContemplations(@RequestParam Long basicHealthUnit,
                                           @RequestParam Long specialty,
                                           @RequestParam String referenceMonth,
                                           @RequestParam(required = false) String contemplationStatus,
                                           Model model) {


        var consultas = contemplationService
                .findContemplationsByUBSAndSpecialty(
                        ProcedureType.CONSULTA,
                        basicHealthUnit,
                        specialty,
                        referenceMonth,
                        contemplationStatus,
                        PageRequest.of(0, DefaultValues.PAGE_SIZE));
        var exames = contemplationService
                .findContemplationsByUBSAndSpecialty(
                        ProcedureType.EXAME,
                        basicHealthUnit,
                        specialty,
                        referenceMonth,
                        contemplationStatus,
                        PageRequest.of(0, DefaultValues.PAGE_SIZE));
        var cirurgias = contemplationService
                .findContemplationsByUBSAndSpecialty(
                        ProcedureType.CIRURGIA,
                        basicHealthUnit,
                        specialty,
                        referenceMonth,
                        contemplationStatus,
                        PageRequest.of(0, DefaultValues.PAGE_SIZE));

        model.addAttribute("selectedUBS", basicHealthUnit);
        model.addAttribute("basicHealthUnits", this.basicHealthUnits);
        model.addAttribute("selectedSpecialty", specialty);
        model.addAttribute("selectedMonth", referenceMonth);
        model.addAttribute("selectedStatus", contemplationStatus);
        model.addAttribute("specialties", this.specialties);
        model.addAttribute("consultasPage", consultas);
        model.addAttribute("examesPage", exames);
        model.addAttribute("cirurgiasPage", cirurgias);
        model.addAttribute("hide", "hidden");

        return "contemplationManagement/contemplation-management";
        /*return List.of(
                new ModelAndView("contemplationManagement/contemplationFragments/contemplation-tabs :: consultas-datatable",
                        Map.of("consultasPage", consultas)),
                new ModelAndView("contemplationManagement/contemplationFragments/contemplation-tabs :: exames-datatable",
                        Map.of("examesPage", exames)),
                new ModelAndView("contemplationManagement/contemplationFragments/contemplation-tabs :: cirurgias-datatable",
                        Map.of("cirurgiasPage", cirurgias))
        );*/
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SMS')")
    @GetMapping("/contemplation-management/paginated")
    public String getMedicalSlotsPaginated(Model model,
                                           @RequestParam(value = "page", defaultValue = "0", required = false) int currentPage,
                                           @RequestParam(value = "consultasPageSize", defaultValue = "" + DefaultValues.PAGE_SIZE, required = false) int consultasPageSize,
                                           @RequestParam(value = "examesPageSize", defaultValue = "" + DefaultValues.PAGE_SIZE, required = false) int examesPageSize,
                                           @RequestParam(value = "cirurgiasPageSize", defaultValue = "" + DefaultValues.PAGE_SIZE, required = false) int cirurgiasPageSize,
                                           @RequestParam(value = "ubs") Long ubs,
                                           @RequestParam(value = "specialty") Long specialty,
                                           @RequestParam(value = "month") String referenceMonth,
                                           @RequestParam(value = "type") String procedureType,
                                           @RequestParam(value = "contemplationStatus") String contemplationStatus) {

        model.addAttribute("selectedUBS", ubs);
        model.addAttribute("selectedSpecialty", specialty);
        model.addAttribute("selectedMonth", referenceMonth);
        model.addAttribute("selectedStatus", contemplationStatus);

        if (procedureType.equals(ProcedureType.CONSULTA.toString())) {
            model.addAttribute("consultasPage", contemplationService
                    .findContemplationsByUBSAndSpecialty(
                            ProcedureType.CONSULTA,
                            ubs,
                            specialty,
                            referenceMonth,
                            contemplationStatus,
                            PageRequest.of(currentPage, consultasPageSize)));
            return "contemplationManagement/contemplationFragments/contemplation-tabs :: consultas-datatable";
        } else if (procedureType.equals(ProcedureType.EXAME.toString())) {
            model.addAttribute("examesPage", contemplationService
                    .findContemplationsByUBSAndSpecialty(
                            ProcedureType.EXAME,
                            ubs,
                            specialty,
                            referenceMonth,
                            contemplationStatus,
                            PageRequest.of(currentPage, examesPageSize)));
            return "contemplationManagement/contemplationFragments/contemplation-tabs :: exames-datatable";
        } else if (procedureType.equals(ProcedureType.CIRURGIA.toString())) {
            model.addAttribute("cirurgiasPage", contemplationService
                    .findContemplationsByUBSAndSpecialty(
                            ProcedureType.CIRURGIA,
                            ubs,
                            specialty,
                            referenceMonth,
                            contemplationStatus,
                            PageRequest.of(currentPage, cirurgiasPageSize)));
            return "contemplationManagement/contemplationFragments/contemplation-tabs :: cirurgias-datatable";

        }
        return "contemplationManagement/contemplation-management";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SMS')")
    @GetMapping("/contemplation-management/{id}/load")
    public String loadContemplated(@PathVariable long id,
                                   @RequestParam Long ubs,
                                   @RequestParam Long specialty,
                                   @RequestParam String month,
                                   Model model) {

        model.addAttribute("selectedUBS", ubs);
        model.addAttribute("selectedSpecialty", specialty);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("contemplated", contemplationService.loadContemplatedById(id));
        model.addAttribute("isContemplated", true);

        //return "contemplationManagement/contemplationFragments/contemplation-info :: contemplationInfo";
        return "queueManagement/queueFragments/patientAppointment-info :: patientAppointmentInfo";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SMS')")
    @PostMapping(value = "/contemplation-management/cancel")
    public String cancelContemplation(@RequestParam("reason") String reason,
                                      @RequestParam Long ubs,
                                      @RequestParam Long specialty,
                                      @RequestParam String month,
                                      @RequestParam Long contemplationId,
                                      @AuthenticationPrincipal SystemUserDetails loggedUser,
                                      RedirectAttributes redirectAttributes) {


        try {
            log.info("Cancelando contemplação[id={}] pelo usuário[nome={}].", contemplationId, loggedUser.getName());
            contemplationService.cancelContemplationByAdmin(contemplationId, reason, loggedUser);
            redirectAttributes.addFlashAttribute("error", false);
            redirectAttributes.addFlashAttribute("message", "Contemplação cancelada com sucesso.");
            log.info("Contemplação[id={}] cancelada com sucesso pelo usuário[nome={}].", contemplationId, loggedUser.getName());
        } catch (CancelContemplationException e) {
            redirectAttributes.addFlashAttribute("error", true);
            redirectAttributes.addFlashAttribute("message", "Erro ao cancelar contemplação.");
            log.error("Erro ao cancelar contemplação: {}", e.getMessage());
        }

        return "redirect:/contemplation-management/search?basicHealthUnit=" + ubs + "&specialty=" + specialty + "&referenceMonth=" + month;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SMS')")
    @PostMapping(value = "/contemplation-management/confirm")
    public String confirmContemplation(@RequestParam Long ubs,
                                       @RequestParam Long specialty,
                                       @RequestParam String month,
                                       @RequestParam Long contemplationId,
                                       @AuthenticationPrincipal SystemUserDetails loggedUser,
                                       RedirectAttributes redirectAttributes) {

        try {
            contemplationService.confirmContemplationByAdmin(contemplationId, loggedUser);
            redirectAttributes.addFlashAttribute("error", false);
            redirectAttributes.addFlashAttribute("message", "Contemplação confirmada com sucesso.");
            log.info("Contemplação[id={}] confirmada com sucesso pelo usuário[nome={}].", contemplationId, loggedUser.getName());
        } catch (ConfirmContemplationException e) {
            redirectAttributes.addFlashAttribute("error", true);
            redirectAttributes.addFlashAttribute("message", "Erro ao confirmar contemplação.");
            log.error("Erro ao confirmar contemplação: {}", e.getMessage());
        }

        return "redirect:/contemplation-management/search?basicHealthUnit=" + ubs + "&specialty=" + specialty + "&referenceMonth=" + month;
    }


}

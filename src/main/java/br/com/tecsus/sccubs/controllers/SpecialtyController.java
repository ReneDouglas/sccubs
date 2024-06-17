package br.com.tecsus.sccubs.controllers;

import br.com.tecsus.sccubs.entities.Specialty;
import br.com.tecsus.sccubs.services.SpecialtyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Slf4j
@Controller
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SMS')")
    @GetMapping("/specialty-management")
    public String getEspecialtiesPage(Model model) {

        model.addAttribute("specialties", specialtyService.findSpecialties());
        model.addAttribute("exams", List.of());
        model.addAttribute("surgeries", List.of());

        return "specialtyManagement/specialty-management";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SMS')")
    @GetMapping(value = "/specialty-management/load", produces = MediaType.TEXT_HTML_VALUE)
    public String loadExamsAndSurgeries(@RequestParam("specialty") long id,
                                        Model model) {

        Specialty fetchedSpecialty = specialtyService.findFetchedSpecialties(id);

        model.addAttribute("exams", fetchedSpecialty.getExams());
        model.addAttribute("surgeries", fetchedSpecialty.getSurgeries());

        return "specialtyManagement/specialtyFragments/specialty-info :: examsAndSurgeriesInfo";
    }

}

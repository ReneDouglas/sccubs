package br.com.tecsus.sccubs.controllers;

import br.com.tecsus.sccubs.entities.Patient;
import br.com.tecsus.sccubs.enums.SocialSituationRating;
import br.com.tecsus.sccubs.security.SystemUserDetails;
import br.com.tecsus.sccubs.services.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
public class PatientSchedulingController {

    private final PatientService patientService;

    public PatientSchedulingController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/patient-scheduling")
    public String getPatientInsertPage(Model model, @AuthenticationPrincipal SystemUserDetails loggedUser) {

        model.addAttribute("patients", List.of());
        model.addAttribute("patient", new Patient());
        //model.addAttribute("socialSituations", SocialSituationRating.getDescriptionSortedByRating());

        return "patientSchedulingManagement/patientScheduling-management";
    }

    @GetMapping(value = "/patient-scheduling/search", produces = MediaType.TEXT_HTML_VALUE)
    public String searchPatient(@RequestParam("patientSearch") String patient,
                                @AuthenticationPrincipal SystemUserDetails loggedUser,
                                Model model) {

        if (patient.isEmpty()) {
            model.addAttribute("patients", List.of());
            return "patientSchedulingManagement/patientSchedulingFragments/patientSearch-dropdown :: dropdownPatient";
        }

        final int THRESHOLD = 4;
        if (patient.length() < THRESHOLD) {
            model.addAttribute("patients", List.of());
            return "patientSchedulingManagement/patientSchedulingFragments/patientSearch-dropdown :: dropdownPatient";
        }

        model.addAttribute("patients", patientService.searchNativePatients(patient, loggedUser.getBasicHealthUnitId()));
        return "patientSchedulingManagement/patientSchedulingFragments/patientSearch-dropdown :: dropdownPatient";
    }

    @GetMapping(value = "/patient-scheduling/load", produces = MediaType.TEXT_HTML_VALUE)
    public String loadPatient(@RequestParam("id") Long idPatient,
                              Model model,
                              @AuthenticationPrincipal SystemUserDetails loggedUser) {

        model.addAttribute("patients", List.of());
        model.addAttribute("patient", patientService.findByIdAndUBS(idPatient, loggedUser.getBasicHealthUnitId()));
        return "patientSchedulingManagement/patientScheduling-management";
    }
}

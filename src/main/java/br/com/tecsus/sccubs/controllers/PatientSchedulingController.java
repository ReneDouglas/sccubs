package br.com.tecsus.sccubs.controllers;

import br.com.tecsus.sccubs.entities.MedicalProcedure;
import br.com.tecsus.sccubs.entities.Patient;
import br.com.tecsus.sccubs.entities.PatientScheduling;
import br.com.tecsus.sccubs.enums.Priorities;
import br.com.tecsus.sccubs.enums.ProcedureType;
import br.com.tecsus.sccubs.enums.SocialSituationRating;
import br.com.tecsus.sccubs.security.SystemUserDetails;
import br.com.tecsus.sccubs.services.PatientSchedulingService;
import br.com.tecsus.sccubs.services.PatientService;
import br.com.tecsus.sccubs.services.SpecialtyService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Slf4j
@Controller
public class PatientSchedulingController {

    private final PatientService patientService;
    private final SpecialtyService specialtyService;
    private final PatientSchedulingService patientSchedulingService;

    public PatientSchedulingController(PatientService patientService, SpecialtyService specialtyService, PatientSchedulingService patientSchedulingService) {
        this.patientService = patientService;
        this.specialtyService = specialtyService;
        this.patientSchedulingService = patientSchedulingService;
    }

    @GetMapping("/patient-scheduling")
    public String getPatientInsertPage(Model model,
                                       @AuthenticationPrincipal SystemUserDetails loggedUser) {

        PatientScheduling patientScheduling = new PatientScheduling();
        patientScheduling.setPatient(new Patient());
        patientScheduling.setMedicalProcedure(new MedicalProcedure());

        model.addAttribute("patients", List.of());
        model.addAttribute("patientScheduling", patientScheduling);
        model.addAttribute("specialties", specialtyService.findSpecialties());

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

        PatientScheduling patientScheduling = new PatientScheduling();
        patientScheduling.setPatient(patientService.findByIdAndUBS(idPatient, loggedUser.getBasicHealthUnitId()));

        model.addAttribute("patients", List.of());
        model.addAttribute("patientScheduling", patientScheduling);
        model.addAttribute("specialties", specialtyService.findSpecialties());
        return "patientSchedulingManagement/patientScheduling-management";
    }

    @GetMapping(value = "/patient-scheduling/procedures", produces = MediaType.TEXT_HTML_VALUE)
    public String loadPrioritiesByProcedure(@RequestParam("procedureType") String procedureType,
                                            @RequestParam("specialty") Long specialtyId,
                                            Model model) {

        List<MedicalProcedure> procedures;
        List<Priorities> priorities = List.of(Priorities.ELETIVO, Priorities.URGENCIA);

        if (procedureType.equals(ProcedureType.CONSULTA.toString())) {
            procedures = patientSchedulingService.findBySpecialtyIdAndProcedureType(specialtyId, ProcedureType.CONSULTA);
            priorities = List.of(Priorities.ELETIVO, Priorities.URGENCIA, Priorities.RETORNO);
        } else if (procedureType.equals(ProcedureType.EXAME.toString())){
            procedures = patientSchedulingService.findBySpecialtyIdAndProcedureType(specialtyId, ProcedureType.EXAME);
        } else {
            procedures = patientSchedulingService.findBySpecialtyIdAndProcedureType(specialtyId, ProcedureType.CIRURGIA);
        }

        model.addAttribute("procedures", procedures);
        model.addAttribute("priorities", priorities);
        return "patientSchedulingManagement/patientSchedulingFragments/patientScheduling-procedureAndPriority :: proceduresAndPriorities";
    }

    @PostMapping("/patient-scheduling/create")
    public String registerPatientScheduling(@ModelAttribute PatientScheduling patientScheduling,
                                            @AuthenticationPrincipal SystemUserDetails loggedUser,
                                            Model model) {
        return null;
    }
}

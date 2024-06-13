package br.com.tecsus.sccubs.controllers;

import br.com.tecsus.sccubs.entities.Patient;
import br.com.tecsus.sccubs.enums.SocialSituationRating;
import br.com.tecsus.sccubs.security.SystemUserDetails;
import br.com.tecsus.sccubs.services.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
public class PatientController {

    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/patient-insert")
    public String getPatientInsertPage(Model model, @AuthenticationPrincipal SystemUserDetails loggedUser) {

        model.addAttribute("patient", new Patient());
        model.addAttribute("socialSituations", SocialSituationRating.getDescriptionSortedByRating());


        return "patientManagement/patient-insert";
    }

    @PostMapping("/patient-insert/create")
    public String registerPatient(@ModelAttribute Patient patient,
                                  @AuthenticationPrincipal SystemUserDetails loggedUser,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        try {
            Patient newPatient = patientService.registerPatient(patient, loggedUser);
            model.addAttribute("patient", newPatient);
            model.addAttribute("message", "Paciente cadastrado com sucesso.");
        } catch (Exception e) {
            log.error("Erro ao cadastrar paciente: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("message", "Erro ao cadastrar paciente.");
            return "redirect:patient-insert";
        }
        return "patientManagement/patientFragments/patient-info :: patientToEdit";
    }

    @PostMapping("/patient-insert/edit")
    public String patientToEdit(@ModelAttribute Patient patient,
                                Model model) {

        model.addAttribute("patient", patient);
        return "patientManagement/patientFragments/patient-form :: patientForm";
    }

    @PostMapping("/patient-insert/update")
    public String updatePatient(@ModelAttribute Patient patient,
                                @AuthenticationPrincipal SystemUserDetails loggedUser,
                                Model model) {

        try {
            Patient updatedPatient = patientService.updatePatient(patient, loggedUser);
            model.addAttribute("patient", updatedPatient);
            model.addAttribute("message", "Paciente atualizado com sucesso.");
        } catch (Exception e) {
            log.error("Erro ao atualizar paciente: {}", e.getMessage());
            model.addAttribute("message", "Erro ao atualizar paciente.");
            return "patientManagement/patientFragments/patient-form :: patientForm";
        }
        return "patientManagement/patientFragments/patient-info :: patientToEdit";
    }
}

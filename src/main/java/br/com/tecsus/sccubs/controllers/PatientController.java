package br.com.tecsus.sccubs.controllers;

import br.com.tecsus.sccubs.dtos.PatientAppointmentsHistoryDTO;
import br.com.tecsus.sccubs.entities.Patient;
import br.com.tecsus.sccubs.enums.Roles;
import br.com.tecsus.sccubs.enums.SocialSituationRating;
import br.com.tecsus.sccubs.security.SystemUserDetails;
import br.com.tecsus.sccubs.services.BasicHealthUnitService;
import br.com.tecsus.sccubs.services.PatientService;
import br.com.tecsus.sccubs.utils.DefaultValues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;

import java.util.List;

@Slf4j
@Controller
@SessionScope
public class PatientController {

    private final PatientService patientService;
    private final BasicHealthUnitService basicHealthUnitService;
    private Patient patientToSearch;
    private Patient patientToEdit;
    private long patientHistoryId;

    @Autowired
    public PatientController(PatientService patientService, BasicHealthUnitService basicHealthUnitService) {
        this.patientService = patientService;
        this.basicHealthUnitService = basicHealthUnitService;
        this.patientToSearch = new Patient();
        this.patientToEdit = new Patient();
    }

    @GetMapping("/patient-management")
    public String getPatientInsertPage(Model model, @AuthenticationPrincipal SystemUserDetails loggedUser) {

        if (patientToEdit.getId() != null) {
            model.addAttribute("patient", patientToEdit);
        } else {
            model.addAttribute("patient", new Patient());
        }
        model.addAttribute("socialSituations", SocialSituationRating.getDescriptionSortedByRating());

        boolean isAdmin = loggedUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Roles.ROLE_SMS.toString()));
        if (isAdmin) {
            model.addAttribute("basicHealthUnits", basicHealthUnitService.findAllUBS());
        } else {
            model.addAttribute("systemUserUBS", basicHealthUnitService.findSystemUserUBS(loggedUser.getBasicHealthUnitId()));
        }

        return "patientManagement/patient-management";
    }

    @PostMapping("/patient-management/create")
    public String registerPatient(@ModelAttribute Patient patient,
                                  @AuthenticationPrincipal SystemUserDetails loggedUser,
                                  Model model) {
        try {
            Patient newPatient = patientService.registerPatient(patient, loggedUser);
            model.addAttribute("patient", newPatient);
            model.addAttribute("message", "Paciente cadastrado com sucesso.");
            model.addAttribute("error", false);
        } catch (DataIntegrityViolationException e) {
            log.error("Violação de integridade [Paciente]: {}", e.getMessage());
            model.addAttribute("message", "CPF ou Cartão SUS já cadastrados no sistema.");
            model.addAttribute("error", true);
            return "patientManagement/patientFragments/patient-form :: patientForm";
        } catch (Exception e) {
            log.error("Erro ao cadastrar paciente: {}", e.getMessage());
            model.addAttribute("message", "Erro ao cadastrar paciente.");
            model.addAttribute("error", true);
            return "patientManagement/patientFragments/patient-form :: patientForm";
        }
        return "patientManagement/patientFragments/patient-info :: patientToEdit";
    }

    @PostMapping("/patient-management/edit")
    public String patientToEdit(@ModelAttribute Patient patient,
                                Model model) {

        model.addAttribute("patient", patient);
        return "patientManagement/patientFragments/patient-form :: patientForm";
    }

    @PostMapping("/patient-management/update")
    public String updatePatient(@ModelAttribute Patient patient,
                                @AuthenticationPrincipal SystemUserDetails loggedUser,
                                Model model) {

        try {
            Patient updatedPatient = patientService.updatePatient(patient, loggedUser);
            patientToEdit = new Patient();
            model.addAttribute("patient", updatedPatient);
            model.addAttribute("message", "Paciente atualizado com sucesso.");
            model.addAttribute("error", false);
        } catch (Exception e) {
            model.addAttribute("message", "Erro ao atualizar paciente.");
            model.addAttribute("error", true);
            log.error("Erro ao atualizar paciente: {}", e.getMessage());
            return "patientManagement/patientFragments/patient-form :: patientForm";
        }
        return "patientManagement/patientFragments/patient-info :: patientToEdit";
    }


    @GetMapping("/patient-list")
    public String getPatientsPage(Model model,
                                  @ModelAttribute Patient patient,
                                  @AuthenticationPrincipal SystemUserDetails loggedUser,
                                  @RequestParam(value = "page", defaultValue = "0", required = false) int currentPage,
                                  @RequestParam(value = "pageSize", defaultValue = "" + DefaultValues.PAGE_SIZE, required = false) int pageSize,
                                  @RequestParam(value = "pagination", defaultValue = "false", required = false) boolean isPagination){

        if (!isPagination) {
            patientToSearch = patient;
        }

        Page<Patient> patientsPage = patientService.findPatientsPage(patientToSearch, PageRequest.of(currentPage, pageSize), loggedUser);
        model.addAttribute("patientsPage", patientsPage);
        model.addAttribute("patientHistoryPage", new PageImpl<>(List.of(), PageRequest.of(0, DefaultValues.PAGE_SIZE), 0));
        model.addAttribute("patient", patientToSearch);

        if (!isPagination) {
            return "patientManagement/patient-list";
        }
        return "patientManagement/patientFragments/patient-datatable :: patientDatatable";

    }

    @GetMapping("/patient-list/history")
    public String getPatientAppointmentsHistory(Model model,
                                                @AuthenticationPrincipal SystemUserDetails loggedUser,
                                                @RequestParam(value = "id", required = false) Long patientId,
                                                @RequestParam(value = "page", defaultValue = "0", required = false) int currentPage,
                                                @RequestParam(value = "pageSizeHistory", defaultValue = "" + DefaultValues.PAGE_SIZE, required = false) int pageSizeHistory,
                                                @RequestParam(value = "pagination", defaultValue = "false", required = false) boolean isPagination) {

        if (!isPagination) {
            this.patientHistoryId = patientId;
        }

        Page<PatientAppointmentsHistoryDTO> patientHistoryPage = patientService
                .findPatientAppointmentsHistoryPage(this.patientHistoryId, PageRequest.of(currentPage, pageSizeHistory), loggedUser);

        model.addAttribute("patientHistoryPage", patientHistoryPage);
        return "patientManagement/patientFragments/patient-history :: patientHistoryDatatable";
    }

    @GetMapping(value = "/patient-list/search", produces = MediaType.TEXT_HTML_VALUE)
    public String searchPatient(@RequestParam("name") String patient,
                                @RequestParam(value = "autocomplete", defaultValue = "false", required = false) boolean autocomplete,
                                @AuthenticationPrincipal SystemUserDetails loggedUser,
                                Model model) {

        if (patient.isEmpty()) {
            model.addAttribute("patients", List.of());
            if (autocomplete) {
                return "patientManagement/patientFragments/patientSearch-dropdown :: dropdownPatientAutocomplete";
            }
            return "patientManagement/patientFragments/patientSearch-dropdown :: dropdownPatient";
        }

        final int THRESHOLD = 4;
        if (patient.length() < THRESHOLD) {
            model.addAttribute("patients", List.of());
            if (autocomplete) {
                return "patientManagement/patientFragments/patientSearch-dropdown :: dropdownPatientAutocomplete";
            }
            return "patientManagement/patientFragments/patientSearch-dropdown :: dropdownPatient";
        }

        model.addAttribute("patients", patientService.searchNativePatients(patient, loggedUser.getBasicHealthUnitId()));

        if (autocomplete) {
            return "patientManagement/patientFragments/patientSearch-dropdown :: dropdownPatientAutocomplete";
        }

        return "patientManagement/patientFragments/patientSearch-dropdown :: dropdownPatient";
    }

    @GetMapping("/patient-list/clear")
    public String clearPatientsPage() {
        patientToSearch = new Patient();
        return "redirect:/patient-list";
    }

    @GetMapping("/patient-list/edit")
    public String editSelectedPatient(@RequestParam(value = "id") long patientId) throws RuntimeException{
        patientToEdit = patientService.findPatientToEdit(patientId);
        return "redirect:/patient-management";
    }
}

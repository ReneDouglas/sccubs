package br.com.tecsus.sccubs.controllers;

import br.com.tecsus.sccubs.entities.MedicalProcedure;
import br.com.tecsus.sccubs.entities.Patient;
import br.com.tecsus.sccubs.entities.Appointment;
import br.com.tecsus.sccubs.enums.Priorities;
import br.com.tecsus.sccubs.enums.ProcedureType;
import br.com.tecsus.sccubs.security.SystemUserDetails;
import br.com.tecsus.sccubs.services.AppointmentService;
import br.com.tecsus.sccubs.services.PatientService;
import br.com.tecsus.sccubs.services.SpecialtyService;
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
public class AppointmentController {

    private final PatientService patientService;
    private final SpecialtyService specialtyService;
    private final AppointmentService appointmentService;

    public AppointmentController(PatientService patientService, SpecialtyService specialtyService, AppointmentService appointmentService) {
        this.patientService = patientService;
        this.specialtyService = specialtyService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/appointment-management")
    public String getPatientInsertPage(Model model,
                                       @AuthenticationPrincipal SystemUserDetails loggedUser) {

        Appointment appointment = new Appointment();
        appointment.setPatient(new Patient());
        appointment.setMedicalProcedure(new MedicalProcedure());

        model.addAttribute("patients", List.of());
        model.addAttribute("appointment", appointment);
        model.addAttribute("specialties", specialtyService.findSpecialties());

        return "appointmentManagement/appointment-management";
    }

    @GetMapping(value = "/appointment-management/search", produces = MediaType.TEXT_HTML_VALUE)
    public String searchPatient(@RequestParam("patientSearch") String patient,
                                @AuthenticationPrincipal SystemUserDetails loggedUser,
                                Model model) {

        if (patient.isEmpty()) {
            model.addAttribute("patients", List.of());
            return "appointmentManagement/appointmentFragments/patientSearch-dropdown :: dropdownPatient";
        }

        final int THRESHOLD = 4;
        if (patient.length() < THRESHOLD) {
            model.addAttribute("patients", List.of());
            return "appointmentManagement/appointmentFragments/patientSearch-dropdown :: dropdownPatient";
        }

        model.addAttribute("patients", patientService.searchNativePatients(patient, loggedUser.getBasicHealthUnitId()));
        return "appointmentManagement/appointmentFragments/patientSearch-dropdown :: dropdownPatient";
    }

    @GetMapping(value = "/appointment-management/load", produces = MediaType.TEXT_HTML_VALUE)
    public String loadPatient(@RequestParam("id") Long idPatient,
                              Model model,
                              @AuthenticationPrincipal SystemUserDetails loggedUser) {

        Appointment appointment = new Appointment();
        appointment.setPatient(patientService.findByIdAndUBS(idPatient, loggedUser.getBasicHealthUnitId()));

        model.addAttribute("patients", List.of());
        model.addAttribute("appointment", appointment);
        model.addAttribute("specialties", specialtyService.findSpecialties());
        return "appointmentManagement/appointment-management";
    }

    @GetMapping(value = "/appointment-management/procedures", produces = MediaType.TEXT_HTML_VALUE)
    public String loadPrioritiesByProcedure(@RequestParam("procedureType") String procedureType,
                                            @RequestParam("specialty") Long specialtyId,
                                            Model model) {

        List<MedicalProcedure> procedures;
        List<Priorities> priorities = List.of(Priorities.ELETIVO, Priorities.URGENCIA);

        if (procedureType.equals(ProcedureType.CONSULTA.toString())) {
            procedures = appointmentService.findBySpecialtyIdAndProcedureType(specialtyId, ProcedureType.CONSULTA);
            priorities = List.of(Priorities.ELETIVO, Priorities.URGENCIA, Priorities.RETORNO);
        } else if (procedureType.equals(ProcedureType.EXAME.toString())){
            procedures = appointmentService.findBySpecialtyIdAndProcedureType(specialtyId, ProcedureType.EXAME);
        } else {
            procedures = appointmentService.findBySpecialtyIdAndProcedureType(specialtyId, ProcedureType.CIRURGIA);
        }

        model.addAttribute("procedures", procedures);
        model.addAttribute("priorities", priorities);
        return "appointmentManagement/appointmentFragments/appointment-procedureAndPriority :: proceduresAndPriorities";
    }

    @PostMapping("/appointment-management/create")
    public String registerAppointment(@ModelAttribute Appointment appointment,
                                            @AuthenticationPrincipal SystemUserDetails loggedUser,
                                            Model model) {
        return null;
    }
}

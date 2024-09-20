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
import br.com.tecsus.sccubs.services.exceptions.AppointmentRegistrationFailureException;
import br.com.tecsus.sccubs.services.exceptions.CancelAppointmentException;
import br.com.tecsus.sccubs.services.exceptions.DuplicateAppointmentRegistrationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@SessionScope
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
        model.addAttribute("patientOpenAppointments", List.of());
        model.addAttribute("specialties", specialtyService.findSpecialties());
        model.addAttribute("loaded", false);

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
        model.addAttribute("patientOpenAppointments", appointmentService.findPatientOpenAppointments(idPatient));
        model.addAttribute("loaded", true);
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
    public String registerAppointmentSolicitation(@ModelAttribute Appointment appointment,
                                            @AuthenticationPrincipal SystemUserDetails loggedUser,
                                            RedirectAttributes redirectAttributes) {

        try{
            appointmentService.registerAppointment(appointment, loggedUser);
            redirectAttributes.addFlashAttribute("message", "Marcação agendada com sucesso.");
            redirectAttributes.addFlashAttribute("error", false);
            log.info("Marcação agendada com sucesso.");
        } catch (AppointmentRegistrationFailureException e) {
            log.error("Erro ao registrar consulta: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("message", "Erro ao agendar marcação. Tente novamente.");
            redirectAttributes.addFlashAttribute("error", true);
        } catch (DuplicateAppointmentRegistrationException e) {
            log.error("Marcação de consulta duplicada: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("message", "Existe uma marcação em aberto para este procedimento.");
            redirectAttributes.addFlashAttribute("error", true);
        }

        return "redirect:/appointment-management/load?id=" + appointment.getPatient().getId();
    }

    @PutMapping("/appointment-management/{id}/cancel")
    public String cancelAppointmentSolicitation(@PathVariable("id") Long apptSolicitationId,
                                                @RequestParam("patientId") Long patientId,
                                                @AuthenticationPrincipal SystemUserDetails loggedUser,
                                                RedirectAttributes redirectAttributes) {

        try {
            appointmentService.cancelSolicitation(apptSolicitationId, loggedUser);
            redirectAttributes.addFlashAttribute("message", "Marcação cancelada com sucesso.");
            redirectAttributes.addFlashAttribute("error", false);
            log.info("Marcação cancelada com sucesso.");
        } catch (CancelAppointmentException e) {
            log.error("Não foi possível cancelar a marcação de consulta: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("message", "Não foi possível cancelar a marcação. Contate o TI.");
            redirectAttributes.addFlashAttribute("error", true);
        }
        return "redirect:/appointment-management/load?id=" + patientId;
    }

}

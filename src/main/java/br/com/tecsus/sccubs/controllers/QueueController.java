package br.com.tecsus.sccubs.controllers;

import br.com.tecsus.sccubs.entities.*;
import br.com.tecsus.sccubs.enums.ProcedureType;
import br.com.tecsus.sccubs.services.AppointmentService;
import br.com.tecsus.sccubs.services.BasicHealthUnitService;
import br.com.tecsus.sccubs.services.MedicalSlotService;
import br.com.tecsus.sccubs.services.SpecialtyService;
import br.com.tecsus.sccubs.utils.DefaultValues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;
import java.util.List;

@Slf4j
@Controller
@SessionScope
public class QueueController {

    private final AppointmentService appointmentService;
    private final MedicalSlotService medicalSlotService;
    private final List<BasicHealthUnit> basicHealthUnits;
    private final List<Specialty> specialties;

    @Autowired
    public QueueController(BasicHealthUnitService basicHealthUnitService, SpecialtyService specialtyService, List<BasicHealthUnit> basicHealthUnits, List<Specialty> specialties, AppointmentService appointmentService, MedicalSlotService medicalSlotService) {
        this.basicHealthUnits = basicHealthUnitService.findAllUBS();
        this.specialties = specialtyService.findSpecialties();
        this.appointmentService = appointmentService;
        this.medicalSlotService = medicalSlotService;
    }

    @GetMapping("/queue-management")
    public String getQueuePage(Model model) {

        model.addAttribute("basicHealthUnits", this.basicHealthUnits);
        model.addAttribute("specialties", this.specialties);
        model.addAttribute("consultasPage", new PageImpl<Contemplation>(List.of(), PageRequest.of(0, DefaultValues.PAGE_SIZE), 0));
        model.addAttribute("examesPage", new PageImpl<Contemplation>(List.of(), PageRequest.of(0, DefaultValues.PAGE_SIZE), 0));
        model.addAttribute("cirurgiasPage", new PageImpl<Contemplation>(List.of(), PageRequest.of(0, DefaultValues.PAGE_SIZE), 0));
        model.addAttribute("hide", "hidden");

        return "queueManagement/queue-management";
    }

    @GetMapping("/queue-management/search")
    public String searchOpenAppointmentsQueue(@RequestParam Long basicHealthUnit,
                                           @RequestParam Long specialty,
                                           Model model) {


        var consultas = appointmentService
                .findOpenAppointmentsQueuePaginated(
                        ProcedureType.CONSULTA,
                        basicHealthUnit,
                        specialty,
                        PageRequest.of(0, DefaultValues.PAGE_SIZE));
        var exames = appointmentService
                .findOpenAppointmentsQueuePaginated(
                        ProcedureType.EXAME,
                        basicHealthUnit,
                        specialty,
                        PageRequest.of(0, DefaultValues.PAGE_SIZE));
        var cirurgias = appointmentService
                .findOpenAppointmentsQueuePaginated(
                        ProcedureType.CIRURGIA,
                        basicHealthUnit,
                        specialty,
                        PageRequest.of(0, DefaultValues.PAGE_SIZE));

        model.addAttribute("selectedUBS", basicHealthUnit);
        model.addAttribute("basicHealthUnits", this.basicHealthUnits);
        model.addAttribute("selectedSpecialty", specialty);
        model.addAttribute("specialties", this.specialties);
        model.addAttribute("consultasPage", consultas);
        model.addAttribute("examesPage", exames);
        model.addAttribute("cirurgiasPage", cirurgias);
        model.addAttribute("hide", "hidden");

        return "queueManagement/queue-management";
    }

    @GetMapping("/queue-management/paginated")
    public String getOpenAppointmentsQueuePaginated(Model model,
                                           @RequestParam(value = "page", defaultValue = "0", required = false) int currentPage,
                                           @RequestParam(value = "consultasPageSize", defaultValue = "" + DefaultValues.PAGE_SIZE, required = false) int consultasPageSize,
                                           @RequestParam(value = "examesPageSize", defaultValue = "" + DefaultValues.PAGE_SIZE, required = false) int examesPageSize,
                                           @RequestParam(value = "cirurgiasPageSize", defaultValue = "" + DefaultValues.PAGE_SIZE, required = false) int cirurgiasPageSize,
                                           @RequestParam(value = "ubs") Long ubs,
                                           @RequestParam(value = "specialty") Long specialty,
                                           @RequestParam(value = "type") String procedureType) {

        model.addAttribute("selectedUBS", ubs);
        model.addAttribute("selectedSpecialty", specialty);

        if (procedureType.equals(ProcedureType.CONSULTA.toString())) {
            model.addAttribute("consultasPage", appointmentService
                    .findOpenAppointmentsQueuePaginated(
                            ProcedureType.CONSULTA,
                            ubs,
                            specialty,
                            PageRequest.of(currentPage, consultasPageSize)));
            return "queueManagement/queueFragments/queue-tabs :: consultas-datatable";
        } else if (procedureType.equals(ProcedureType.EXAME.toString())) {
            model.addAttribute("examesPage", appointmentService
                    .findOpenAppointmentsQueuePaginated(
                            ProcedureType.EXAME,
                            ubs,
                            specialty,
                            PageRequest.of(currentPage, examesPageSize)));
            return "queueManagement/queueFragments/queue-tabs :: exames-datatable";
        } else if (procedureType.equals(ProcedureType.CIRURGIA.toString())) {
            model.addAttribute("cirurgiasPage", appointmentService
                    .findOpenAppointmentsQueuePaginated(
                            ProcedureType.CIRURGIA,
                            ubs,
                            specialty,
                            PageRequest.of(currentPage, cirurgiasPageSize)));
            return "queueManagement/contemplationFragments/queue-tabs :: cirurgias-datatable";

        }
        return "queueManagement/queue-management";
    }

    @GetMapping("/queue-management/{id}/load")
    public String loadOpenAppointment(@PathVariable long id,
                                   @RequestParam Long ubs,
                                   @RequestParam Long specialty,
                                   Model model) {

        Appointment appointment = appointmentService.findById(id);
        MedicalSlot medicalSlot = new MedicalSlot();
        medicalSlot.setMedicalProcedure(appointment.getMedicalProcedure());
        medicalSlot.setBasicHealthUnit(appointment.getPatient().getBasicHealthUnit());

        model.addAttribute("selectedUBS", ubs);
        model.addAttribute("selectedSpecialty", specialty);
        model.addAttribute("appointment", appointment);
        model.addAttribute("availableSlots", medicalSlotService.findAvailableSlots(medicalSlot).getCurrentSlots());

        return "queueManagement/queueFragments/patientAppointment-info :: patientAppointmentInfo";
    }

}

package br.com.tecsus.sccubs.services;

import br.com.tecsus.sccubs.entities.Appointment;
import br.com.tecsus.sccubs.entities.AppointmentStatusHistory;
import br.com.tecsus.sccubs.enums.AppointmentStatus;
import br.com.tecsus.sccubs.repositories.AppointmentStatusHistoryRepository;
import br.com.tecsus.sccubs.security.SystemUserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AppointmentStatusHistoryService {

    private final AppointmentStatusHistoryRepository repository;

    public AppointmentStatusHistoryService(AppointmentStatusHistoryRepository apptStatusRepo) {
        this.repository = apptStatusRepo;
    }

    public List<AppointmentStatusHistory> findAllAppointmentHistory(Appointment appointment) {
        return repository.findAllByAppointment(appointment);
    }

    @Transactional
    public void registerAppointmentStatusHistory(Appointment appointment, String loggedUser) {

        AppointmentStatusHistory statusHistory = new AppointmentStatusHistory();
        statusHistory.setStatus(appointment.getStatus());
        statusHistory.setCreationUser(loggedUser);
        statusHistory.setCreationDate(LocalDateTime.now());
        statusHistory.setAppointment(appointment);

        repository.save(statusHistory);
    }

    public AppointmentStatusHistory findReferenceById(Long id) {
        return repository.getReferenceById(id);
    }
}

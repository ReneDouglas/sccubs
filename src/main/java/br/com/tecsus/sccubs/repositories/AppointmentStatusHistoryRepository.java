package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.Appointment;
import br.com.tecsus.sccubs.entities.AppointmentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AppointmentStatusHistoryRepository extends JpaRepository<AppointmentStatusHistory, Long> {

    List<AppointmentStatusHistory> findAllByAppointment(Appointment appointment);

}

package br.com.tecsus.sigaubs.repositories;

import br.com.tecsus.sigaubs.entities.Appointment;
import br.com.tecsus.sigaubs.entities.AppointmentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AppointmentStatusHistoryRepository extends JpaRepository<AppointmentStatusHistory, Long> {

    List<AppointmentStatusHistory> findAllByAppointment(Appointment appointment);

}

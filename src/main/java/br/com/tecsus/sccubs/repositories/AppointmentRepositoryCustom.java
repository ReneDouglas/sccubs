package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.dtos.PatientOpenAppointmentDTO;
import br.com.tecsus.sccubs.entities.Appointment;
import br.com.tecsus.sccubs.entities.Contemplation;
import br.com.tecsus.sccubs.enums.ProcedureType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.YearMonth;

public interface AppointmentRepositoryCustom {

    public Page<PatientOpenAppointmentDTO> findOpenAppointmentsQueuePaginated(ProcedureType type,
                                                                                    Long ubsId,
                                                                                    Long specialtyId,
                                                                                    Pageable pageable);

}

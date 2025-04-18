package br.com.tecsus.sigaubs.repositories;

import br.com.tecsus.sigaubs.dtos.PatientOpenAppointmentDTO;
import br.com.tecsus.sigaubs.enums.ProcedureType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentRepositoryCustom {

    public Page<PatientOpenAppointmentDTO> findOpenAppointmentsQueuePaginated(ProcedureType type,
                                                                              Long ubsId,
                                                                              Long specialtyId,
                                                                              Pageable pageable);

    public Page<PatientOpenAppointmentDTO> findOpenAppointmentsQueuePaginatedV2(Long ubsId,
                                                                                Long specialtyId,
                                                                                Long medicalProcedureId,
                                                                                Pageable pageable);
}

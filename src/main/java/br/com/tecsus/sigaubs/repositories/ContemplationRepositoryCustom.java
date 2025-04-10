package br.com.tecsus.sigaubs.repositories;

import br.com.tecsus.sigaubs.entities.Contemplation;
import br.com.tecsus.sigaubs.enums.AppointmentStatus;
import br.com.tecsus.sigaubs.enums.ProcedureType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.YearMonth;

public interface ContemplationRepositoryCustom {

    public Page<Contemplation> findConsultationsByUBSAndSpecialtyPaginated(ProcedureType type,
                                                                           Long ubsId,
                                                                           Long specialtyId,
                                                                           YearMonth referenceMonth,
                                                                           AppointmentStatus status,
                                                                           Pageable pageable);

}

package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.Contemplation;
import br.com.tecsus.sccubs.entities.MedicalSlot;
import br.com.tecsus.sccubs.enums.ProcedureType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.YearMonth;

public interface ContemplationRepositoryCustom {

    public Page<Contemplation> findConsultationsByUBSAndSpecialtyPaginated(ProcedureType type,
                                                                           Long ubsId,
                                                                           Long specialtyId,
                                                                           YearMonth referenceMonth,
                                                                           Boolean confirmed,
                                                                           Pageable pageable);

}

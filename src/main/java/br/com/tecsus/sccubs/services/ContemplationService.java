package br.com.tecsus.sccubs.services;

import br.com.tecsus.sccubs.entities.*;
import br.com.tecsus.sccubs.enums.ProcedureType;
import br.com.tecsus.sccubs.repositories.ContemplationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class ContemplationService {

    private final ContemplationRepository contemplationRepository;

    @Autowired
    public ContemplationService(ContemplationRepository contemplationRepository) {
        this.contemplationRepository = contemplationRepository;
    }

    public Page<Contemplation> findContemplationsByUBSAndSpecialty(ProcedureType type,
                                                                   Long ubsId,
                                                                   Long specialtyId,
                                                                   String referenceMonth,
                                                                   Pageable page) {

        YearMonth yearMonth = YearMonth.parse(referenceMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        return contemplationRepository.findConsultationsByUBSAndSpecialtyPaginated(type, ubsId, specialtyId, yearMonth, page);
    }

    public Contemplation loadContemplatedById(long contemplationId) {
        return contemplationRepository.loadFetchedContemplationById(contemplationId);
    }
}

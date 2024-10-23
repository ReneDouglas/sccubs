package br.com.tecsus.sccubs.services;

import br.com.tecsus.sccubs.entities.*;
import br.com.tecsus.sccubs.enums.ProcedureType;
import br.com.tecsus.sccubs.repositories.ContemplationRepository;
import br.com.tecsus.sccubs.security.SystemUserDetails;
import br.com.tecsus.sccubs.services.exceptions.CancelContemplationException;
import br.com.tecsus.sccubs.services.exceptions.ConfirmContemplationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class ContemplationService {

    private final ContemplationRepository contemplationRepository;
    private final DateTimeFormatter formatter;

    @Autowired
    public ContemplationService(ContemplationRepository contemplationRepository) {
        this.contemplationRepository = contemplationRepository;
        this.formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    }

    public Page<Contemplation> findContemplationsByUBSAndSpecialty(ProcedureType type,
                                                                   Long ubsId,
                                                                   Long specialtyId,
                                                                   String referenceMonth,
                                                                   String confirmed,
                                                                   Pageable page) {

        Boolean status = confirmed == null || confirmed.equals("null") ? null : Boolean.parseBoolean(confirmed);

        YearMonth yearMonth = YearMonth.parse(referenceMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        return contemplationRepository
                .findConsultationsByUBSAndSpecialtyPaginated(type, ubsId, specialtyId, yearMonth, status, page);
    }

    public Contemplation loadContemplatedById(long contemplationId) {
        return contemplationRepository.loadFetchedContemplationById(contemplationId);
    }

    @Transactional
    public void cancelContemplation(Long contemplatedId, String reason, SystemUserDetails loggedUser) throws CancelContemplationException {

            Contemplation contemplated = contemplationRepository.getReferenceById(contemplatedId);
            contemplated.setCanceled(true);
            contemplated.setUpdateUser(loggedUser.getName());
            contemplated.setUpdateDate(LocalDateTime.now());

            if (contemplated.getObservation() == null || contemplated.getObservation().isEmpty()) {
                contemplated.setObservation("Cancelado por " + loggedUser.getName() + " em " + LocalDateTime.now().format(formatter) + " -- Motivo: " + reason);
            } else {
                contemplated.setObservation(contemplated.getObservation() + " -- Cancelado por " + loggedUser.getName() + " em " + LocalDateTime.now().format(formatter) + " -- Motivo: " + reason);
            }

            contemplationRepository.save(contemplated);
    }

    @Transactional
    public void confirmContemplation(Long contemplationId, SystemUserDetails loggedUser) throws ConfirmContemplationException {

        Contemplation contemplated = contemplationRepository.getReferenceById(contemplationId);
        contemplated.setConfirmed(true);
        contemplated.setObservation("Confirmado por " + loggedUser.getName() + " em " + LocalDateTime.now().format(formatter));
        contemplated.setUpdateUser(loggedUser.getName());
        contemplated.setUpdateDate(LocalDateTime.now());

        contemplationRepository.save(contemplated);
    }
}

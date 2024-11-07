package br.com.tecsus.sccubs.repositories.Impl;

import br.com.tecsus.sccubs.dtos.PatientAppointmentsHistoryDTO;
import br.com.tecsus.sccubs.entities.Patient;
import br.com.tecsus.sccubs.repositories.PatientRepositoryCustom;
import br.com.tecsus.sccubs.utils.ValidationUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public class PatientRepositoryCustomImpl implements PatientRepositoryCustom {

    private final EntityManager em;
    private ValidationUtils validationUtils;

    @Autowired
    public PatientRepositoryCustomImpl(JpaContext jpaContext) {
        this.em = jpaContext.getEntityManagerByManagedType(Patient.class);
    }

    @Autowired
    public void setValidationUtils(ValidationUtils validationUtils) {
        this.validationUtils = validationUtils;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> searchNativePatientsContainingByUBS(String terms, Long idUBS) {

        String jpql = """
                SELECT p.*
                FROM patients p
                WHERE MATCH(p.name, p.sus_card_number, p.cpf) AGAINST(:terms IN BOOLEAN MODE)
                AND p.id_basic_health_unit = :id
                LIMIT 5
                """;

        Query nativeQuery = em.createNativeQuery(jpql, Patient.class);
        nativeQuery.setParameter("terms", "*" + terms + "*");
        nativeQuery.setParameter("id", idUBS);

        var list = (List<Patient>) nativeQuery.getResultList();
        return list;
    }

    @Override
    public Page<Patient> findPatientsPaginated(Patient patient, Pageable page) {

        StringBuilder jpql = new StringBuilder();

        jpql.append("SELECT p.id FROM Patient p ");
        jpql.append("WHERE p.basicHealthUnit.id = :ubsId ");

        if (validationUtils.attrIsNotNull(patient.getName())) {
            jpql.append("AND p.name LIKE :name ");
        }
        if (validationUtils.attrIsNotNull(patient.getPhoneNumber())) {
            jpql.append("AND p.phoneNumber = :phoneNumber ");
        }
        if (validationUtils.attrIsNotNull(patient.getCpf())) {
            jpql.append("AND p.cpf = :cpf ");
        }
        if (validationUtils.attrIsNotNull(patient.getSusNumber())) {
            jpql.append("AND p.susNumber = :susNumber ");
        }
        if (validationUtils.attrIsNotNull(patient.getAddressStreet())) {
            jpql.append("AND p.addressStreet LIKE :addressStreet ");
        }
        if (validationUtils.attrIsNotNull(patient.getSocialSituationRating())) {
            jpql.append("AND p.socialSituationRating = :socialSituationRating ");
        }
        if (validationUtils.attrIsNotNull(patient.getAcsName())) {
            jpql.append("AND p.acsName = :acsName ");
        }

        jpql.append("ORDER BY p.name");

        TypedQuery<Long> patientsIdQueryPaginated = em.createQuery(jpql.toString(), Long.class);
        attachParameters(patientsIdQueryPaginated, patient);

        patientsIdQueryPaginated.setFirstResult(page.getPageNumber() * page.getPageSize());
        patientsIdQueryPaginated.setMaxResults(page.getPageSize());

        var patientsIdsPaginated = patientsIdQueryPaginated.getResultList();
        long totalCountPatients = 0;

        if (patientsIdsPaginated.size() < page.getPageSize()) {
            totalCountPatients = patientsIdsPaginated.size();
        } else {
            Query count = em.createQuery(jpql.toString().replace("p.id", "count(p.id)"));
            attachParameters(count, patient);
            totalCountPatients = (long) count.getSingleResult();
        }

        TypedQuery<Patient> patientsQuery = em.createQuery("""
                   SELECT p FROM Patient p WHERE p.id IN :ids
        """, Patient.class);

        patientsQuery.setParameter("ids", patientsIdsPaginated);
        List<Patient> patients = patientsQuery.getResultList();

        return new PageImpl<>(patients, page, totalCountPatients);
    }

    @Override
    public Page<PatientAppointmentsHistoryDTO> findPatientAppointmentsHistoryPaginated(Patient patient, Pageable page) {

        TypedQuery<Long> appointmentsHistoryIdQueryPaginated = em.createQuery("""
                SELECT
                      a.id
                FROM
                      Appointment a
                LEFT JOIN a.contemplation c
                LEFT JOIN a.medicalProcedure mp
                LEFT JOIN mp.specialty s
                LEFT JOIN a.patient p
                WHERE p.id = :id AND p.basicHealthUnit.id = :ubsId
                ORDER BY a.requestDate DESC
                """, Long.class);

        appointmentsHistoryIdQueryPaginated.setParameter("id", patient.getId());
        appointmentsHistoryIdQueryPaginated.setParameter("ubsId", patient.getBasicHealthUnit().getId());

        appointmentsHistoryIdQueryPaginated.setFirstResult(page.getPageNumber() * page.getPageSize());
        appointmentsHistoryIdQueryPaginated.setMaxResults(page.getPageSize());

        var appointmentsHistoryIdsPaginated = appointmentsHistoryIdQueryPaginated.getResultList();
        long totalCountAppointmentsHistory = 0;

        if (appointmentsHistoryIdsPaginated.size() < page.getPageSize()) {
            totalCountAppointmentsHistory = appointmentsHistoryIdsPaginated.size();
        } else {
            TypedQuery<Long> count = em.createQuery("""
                SELECT
                      COUNT(a.id)
                FROM
                      Appointment a
                LEFT JOIN a.contemplation c
                LEFT JOIN a.medicalProcedure mp
                LEFT JOIN mp.specialty s
                LEFT JOIN a.patient p
                WHERE p.id = :id AND p.basicHealthUnit.id = :ubsId
                """, Long.class);

            count.setParameter("id", patient.getId());
            count.setParameter("ubsId", patient.getBasicHealthUnit().getId());
            totalCountAppointmentsHistory = (long) count.getSingleResult();
        }

        TypedQuery<PatientAppointmentsHistoryDTO> appointmentsHistoryQuery = em.createQuery("""
                SELECT
                      new br.com.tecsus.sccubs.dtos.PatientAppointmentsHistoryDTO(
                          a.requestDate,
                          COALESCE(c.contemplationDate, null),
                          COALESCE(c.contemplatedBy, null),
                          a.priority,
                          COALESCE(c.confirmed, false),
                          a.canceled,
                          mp.procedureType,
                          mp.description,
                          s.title,
                          COALESCE(a.observation, 'Sem observações.'),
                          mp.id,
                          a.id,
                          COALESCE(c.id, null),
                          p.id)
                FROM
                      Appointment a
                LEFT JOIN a.contemplation c
                LEFT JOIN a.medicalProcedure mp
                LEFT JOIN mp.specialty s
                LEFT JOIN a.patient p
                WHERE a.id IN :ids
                """, PatientAppointmentsHistoryDTO.class);

        appointmentsHistoryQuery.setParameter("ids", appointmentsHistoryIdsPaginated);
        List<PatientAppointmentsHistoryDTO> patientHistory = appointmentsHistoryQuery.getResultList();

        return new PageImpl<>(patientHistory, page, totalCountAppointmentsHistory);
    }

    private void attachParameters(Query query, Patient patient){

        query.setParameter("ubsId", patient.getBasicHealthUnit().getId());

        if (validationUtils.attrIsNotNull(patient.getName())) {
            query.setParameter("name", "%" + patient.getName() + "%");
        }
        if (validationUtils.attrIsNotNull(patient.getPhoneNumber())) {
            query.setParameter("phoneNumber", patient.getPhoneNumber());
        }
        if (validationUtils.attrIsNotNull(patient.getCpf())) {
            query.setParameter("cpf", patient.getCpf());
        }
        if (validationUtils.attrIsNotNull(patient.getSusNumber())) {
            query.setParameter("susNumber", patient.getSusNumber());
        }
        if (validationUtils.attrIsNotNull(patient.getAddressStreet())) {
            query.setParameter("addressStreet", "%" + patient.getAddressStreet() + "%");
        }
        if (validationUtils.attrIsNotNull(patient.getSocialSituationRating())) {
            query.setParameter("socialSituationRating", patient.getSocialSituationRating());
        }
        if (validationUtils.attrIsNotNull(patient.getAcsName())) {
            query.setParameter("acsName", patient.getAcsName());
        }
    }
}

package br.com.tecsus.sccubs.repositories.Impl;

import br.com.tecsus.sccubs.entities.Patient;
import br.com.tecsus.sccubs.repositories.PatientRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public class PatientRepositoryImpl implements PatientRepositoryCustom {

    private final EntityManager em;

    @Autowired
    public PatientRepositoryImpl(JpaContext jpaContext) {
        this.em = jpaContext.getEntityManagerByManagedType(Patient.class);
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
}

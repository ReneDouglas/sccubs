package br.com.tecsus.sccubs.repositories.Impl;

import br.com.tecsus.sccubs.entities.Patient;
import br.com.tecsus.sccubs.repositories.PatientRepositoryCustom;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Repository;

@Repository
public class PatientRepositoryImpl implements PatientRepositoryCustom {

    private final EntityManager em;

    @Autowired
    public PatientRepositoryImpl(JpaContext jpaContext) {
        this.em = jpaContext.getEntityManagerByManagedType(Patient.class);
    }
}

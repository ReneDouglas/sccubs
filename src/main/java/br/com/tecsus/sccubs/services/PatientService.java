package br.com.tecsus.sccubs.services;

import br.com.tecsus.sccubs.entities.Patient;
import br.com.tecsus.sccubs.repositories.PatientRepository;
import br.com.tecsus.sccubs.security.SystemUserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final BasicHealthUnitService basicHealthUnitService;

    public PatientService(PatientRepository patientRepository, BasicHealthUnitService basicHealthUnitService) {
        this.patientRepository = patientRepository;
        this.basicHealthUnitService = basicHealthUnitService;
    }

    @Transactional
    public Patient registerPatient(Patient patient, SystemUserDetails loggedUser) throws Exception{

        patient.setBasicHealthUnit(basicHealthUnitService.findById(loggedUser.getBasicHealthUnitId()));
        patient.setCreationUser(loggedUser.getUsername());
        patient.setCreationDate(LocalDateTime.now());

        return patientRepository.save(patient);
    }

    @Transactional
    public Patient updatePatient(Patient patient, SystemUserDetails loggedUser) throws Exception{
        patient.setUpdateUser(loggedUser.getUsername());
        patient.setUpdateDate(LocalDateTime.now());
        return patientRepository.save(patient);
    }

}

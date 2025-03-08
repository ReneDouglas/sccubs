package br.com.tecsus.sccubs.services;

import br.com.tecsus.sccubs.dtos.PatientAppointmentsHistoryDTO;
import br.com.tecsus.sccubs.entities.BasicHealthUnit;
import br.com.tecsus.sccubs.entities.Patient;
import br.com.tecsus.sccubs.repositories.PatientRepository;
import br.com.tecsus.sccubs.security.SystemUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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

        patient.setBasicHealthUnit(basicHealthUnitService.findSystemUserUBS(loggedUser.getBasicHealthUnitId()));
        patient.setCreationUser(loggedUser.getName());
        patient.setCreationDate(LocalDateTime.now());

        return patientRepository.save(patient);
    }

    @Transactional
    public Patient updatePatient(Patient patient, SystemUserDetails loggedUser) throws Exception{
        patient.setUpdateUser(loggedUser.getName());
        patient.setUpdateDate(LocalDateTime.now());
        return patientRepository.save(patient);
    }

    public List<Patient> searchNativePatients(String terms, Long id) {
       return patientRepository.searchNativePatientsContainingByUBS(terms, id);
    }

    @Transactional(readOnly = true)
    public Patient findByIdAndUBS(Long idPatient, Long idUBS) {

        if (idUBS == null) {
            return patientRepository.findById(idPatient).orElseThrow(() -> new RuntimeException("Paciente não encontrado."));
        }

        BasicHealthUnit ubs = basicHealthUnitService.findReferenceById(idUBS);
        return patientRepository.findByIdAndBasicHealthUnit(idPatient, ubs);
    }

    @Transactional(readOnly = true)
    public Page<Patient> findPatientsPage(Patient patient, PageRequest pageRequest, SystemUserDetails loggedUser) {

        //BasicHealthUnit ubs = basicHealthUnitService.findById(loggedUser.getBasicHealthUnitId());
        BasicHealthUnit ubs = new BasicHealthUnit();
        ubs.setId(loggedUser.getBasicHealthUnitId());
        patient.setBasicHealthUnit(ubs);
        return patientRepository.findPatientsPaginated(patient, pageRequest);
    }

    @Transactional(readOnly = true)
    public Page<PatientAppointmentsHistoryDTO> findPatientAppointmentsHistoryPage(Long patientId, PageRequest pageRequest, SystemUserDetails loggedUser) {

        Patient patient = new Patient();
        BasicHealthUnit ubs = new BasicHealthUnit();
        ubs.setId(loggedUser.getBasicHealthUnitId());
        //BasicHealthUnit ubs = basicHealthUnitService.findById(loggedUser.getBasicHealthUnitId());
        patient.setBasicHealthUnit(ubs);
        patient.setId(patientId);
        return patientRepository.findPatientAppointmentsHistoryPaginated(patient, pageRequest);
    }

    public Patient findPatientToEdit(long id) throws RuntimeException {
        return patientRepository.findById(id).orElseThrow(() -> {
            log.error("Paciente [id = {}] não encontrado.", id);
            return new RuntimeException("Paciente não encontrado. Contate o TI.");
        } );
    }

}

package br.com.tecsus.sigaubs.repositories;

import br.com.tecsus.sigaubs.dtos.PatientAppointmentsHistoryDTO;
import br.com.tecsus.sigaubs.entities.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PatientRepositoryCustom {

    List<Patient> searchNativePatientsContainingByUBS(String terms, Long idUBS);
    Page<Patient> findPatientsPaginated(Patient patient, Pageable page);
    Page<PatientAppointmentsHistoryDTO> findPatientAppointmentsHistoryPaginated(Patient patient, Pageable page);

}

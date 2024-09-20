package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.dtos.PatientAppointmentsHistoryDTO;
import br.com.tecsus.sccubs.entities.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PatientRepositoryCustom {

    List<Patient> searchNativePatientsContainingByUBS(String terms, Long idUBS);
    Page<Patient> findPatientsPaginated(Patient patient, Pageable page);
    Page<PatientAppointmentsHistoryDTO> findPatientAppointmentsHistoryPaginated(Patient patient, Pageable page);

}

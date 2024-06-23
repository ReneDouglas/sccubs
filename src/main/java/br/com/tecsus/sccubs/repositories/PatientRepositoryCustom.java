package br.com.tecsus.sccubs.repositories;

import br.com.tecsus.sccubs.entities.Patient;

import java.util.List;

public interface PatientRepositoryCustom {

    List<Patient> searchNativePatientsContainingByUBS(String terms, Long idUBS);

}

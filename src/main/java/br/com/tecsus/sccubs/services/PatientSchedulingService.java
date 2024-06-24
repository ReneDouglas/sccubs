package br.com.tecsus.sccubs.services;

import br.com.tecsus.sccubs.entities.MedicalProcedure;
import br.com.tecsus.sccubs.entities.Specialty;
import br.com.tecsus.sccubs.enums.ProcedureType;
import br.com.tecsus.sccubs.repositories.MedicalProcedureRepository;
import br.com.tecsus.sccubs.repositories.PatientSchedulingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientSchedulingService {

    private final PatientSchedulingRepository patientSchedulingRepository;
    private final MedicalProcedureRepository medicalProcedureRepository;

    public PatientSchedulingService(PatientSchedulingRepository patientSchedulingRepository, MedicalProcedureRepository medicalProcedureRepository) {
        this.patientSchedulingRepository = patientSchedulingRepository;
        this.medicalProcedureRepository = medicalProcedureRepository;
    }

    public List<MedicalProcedure> findBySpecialtyIdAndProcedureType(Long specialtyId, ProcedureType procedureType) {
        Specialty specialty = new Specialty();
        specialty.setId(specialtyId);
        return medicalProcedureRepository.findAllBySpecialtyAndProcedureType(specialty, procedureType);
    }
}

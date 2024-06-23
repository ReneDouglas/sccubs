package br.com.tecsus.sccubs.entities;

import br.com.tecsus.sccubs.enums.Priorities;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "patient_schedulings")
public class PatientScheduling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "request_date", updatable = false)
    private LocalDateTime requestDate;

    @Enumerated(EnumType.ORDINAL)
    private Priorities priority;
    private String observation;

    @Column(name = "creation_user", updatable = false)
    private String creationUser;

    @OneToOne
    @JoinColumn(name = "id_medical_procedure", updatable = false)
    private MedicalProcedure medicalProcedure;

    @OneToOne
    @JoinColumn(name = "id_patient", updatable = false)
    private Patient patient;

    public PatientScheduling() {
    }
}

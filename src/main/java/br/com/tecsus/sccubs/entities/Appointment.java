package br.com.tecsus.sccubs.entities;

import br.com.tecsus.sccubs.entities.converters.PriorityConverter;
import br.com.tecsus.sccubs.enums.Priorities;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "request_date", updatable = false)
    private LocalDateTime requestDate;

    @Convert(converter = PriorityConverter.class)
    private Priorities priority;

    private String observation;
    private boolean canceled;

    @Column(name = "creation_user", updatable = false)
    private String creationUser;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "update_user")
    private String updateUser;

    @OneToOne
    @JoinColumn(name = "id_medical_procedure", updatable = false)
    private MedicalProcedure medicalProcedure;

    @OneToOne
    @JoinColumn(name = "id_patient", updatable = false)
    private Patient patient;

    @OneToOne(mappedBy = "appointment")
    private Contemplation contemplation;

    public Appointment() {
    }
}

package br.com.tecsus.sccubs.entities;

import br.com.tecsus.sccubs.entities.converters.AppointmentStatusConverter;
import br.com.tecsus.sccubs.entities.converters.PriorityConverter;
import br.com.tecsus.sccubs.enums.AppointmentStatus;
import br.com.tecsus.sccubs.enums.Priorities;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
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

    @Convert(converter = AppointmentStatusConverter.class)
    private AppointmentStatus status;

    @OneToMany(mappedBy = "appointment")
    private List<AppointmentStatusHistory> appointmentStatusHistory = new ArrayList<>();

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

    @OneToOne
    @JoinColumn(name = "id_contemplation")
    private Contemplation contemplation;

    public Appointment() {
    }
}

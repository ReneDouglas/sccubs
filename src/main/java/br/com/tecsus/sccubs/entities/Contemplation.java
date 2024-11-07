package br.com.tecsus.sccubs.entities;

import br.com.tecsus.sccubs.entities.converters.PriorityConverter;
import br.com.tecsus.sccubs.enums.Priorities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@DynamicUpdate
@Table(name = "contemplations")
public class Contemplation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "contemplation_date")
    private LocalDateTime contemplationDate;

    @Column(name = "contemplated_by")
    @Convert(converter = PriorityConverter.class)
    private Priorities contemplatedBy;

    private boolean confirmed;

    @OneToOne
    @JoinColumn(name = "id_appointment")
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "id_available_medical_slot")
    private MedicalSlot medicalSlot;

    private boolean canceled;

    private String observation;

    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "creation_user", updatable = false)
    private String creationUser;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "update_user")
    private String updateUser;

    public Contemplation() {
    }
}

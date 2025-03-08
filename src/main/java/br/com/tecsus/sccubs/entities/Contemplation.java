package br.com.tecsus.sccubs.entities;

import br.com.tecsus.sccubs.entities.converters.PriorityConverter;
import br.com.tecsus.sccubs.entities.converters.ContemplationStatusConverter;
import br.com.tecsus.sccubs.enums.Priorities;
import br.com.tecsus.sccubs.enums.ContemplationStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Convert;
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

    @OneToOne
    @JoinColumn(name = "id_appointment")
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "id_available_medical_slot")
    private MedicalSlot medicalSlot;

    @Convert(converter = ContemplationStatusConverter.class)
    private ContemplationStatus status;

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

    public boolean isEmptyObservation() {
        return this.getObservation() == null || this.getObservation().isEmpty();
    }
}

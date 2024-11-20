package br.com.tecsus.sccubs.entities;

import br.com.tecsus.sccubs.enums.ProcedureType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "medical_procedures")
public class MedicalProcedure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;

    @Column(name = "type")
    //@Convert(converter = ProcedureTypeAttrConverter.class)
    @Enumerated(EnumType.STRING)
    private ProcedureType procedureType;

    @ManyToOne
    @JoinColumn(name = "id_specialty", referencedColumnName = "id")
    private Specialty specialty;

    @Column(name = "creation_date", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime creationDate;

    @Column(name = "creation_user", updatable = false)
    private String creationUser;

    public MedicalProcedure() {
    }
}

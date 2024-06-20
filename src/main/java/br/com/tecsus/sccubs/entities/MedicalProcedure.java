package br.com.tecsus.sccubs.entities;

import br.com.tecsus.sccubs.entities.converters.ProcedureTypeAttrConverter;
import br.com.tecsus.sccubs.enums.ProcedureType;
import jakarta.persistence.*;
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
    @Convert(converter = ProcedureTypeAttrConverter.class)
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

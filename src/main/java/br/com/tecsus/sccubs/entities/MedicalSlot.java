package br.com.tecsus.sccubs.entities;

import br.com.tecsus.sccubs.entities.converters.YearMonthDateAttributeConverter;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Convert;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "medical_slots")
public class MedicalSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM")
    @Convert(converter = YearMonthDateAttributeConverter.class)
    @Column(name = "reference_month", columnDefinition = "date")
    private YearMonth referenceMonth;

    @Column(name = "total_slots")
    private Integer totalSlots;

    @Column(name = "current_slots")
    private Integer currentSlots;

    @OneToMany(mappedBy = "medicalSlot")
    private Set<Contemplation> contemplations = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "id_medical_procedure", updatable = false)
    private MedicalProcedure medicalProcedure;

    @OneToOne
    @JoinColumn(name = "id_basic_health_unit", updatable = false)
    private BasicHealthUnit basicHealthUnit;

    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "creation_user", updatable = false)
    private String creationUser;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "update_user")
    private String updateUser;


    public MedicalSlot() {
    }

}

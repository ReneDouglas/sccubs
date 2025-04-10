package br.com.tecsus.sigaubs.entities;

import br.com.tecsus.sigaubs.entities.converters.SocialSituationAttrConverter;
import br.com.tecsus.sigaubs.enums.SocialSituationRating;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Convert;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "sus_card_number", updatable = false)
    private String susNumber;

    @Column(updatable = false)
    private String cpf;

    private String gender;

    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Convert(converter = SocialSituationAttrConverter.class)
    @Column(name = "social_sit_rating")
    private SocialSituationRating socialSituationRating;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address_street")
    private String addressStreet;

    @Column(name = "address_number")
    private String addressNumber;

    @Column(name = "address_complement")
    private String addressComplement;

    @Column(name = "address_ref")
    private String addressReference;

    @Column(name = "acs_name")
    private String acsName;

    @ManyToOne
    @JoinColumn(name = "id_basic_health_unit")
    private BasicHealthUnit basicHealthUnit;

    @Column(name = "creation_date", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime creationDate;

    @Column(name = "creation_user", updatable = false)
    private String creationUser;

    @Column(name = "update_date")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updateDate;

    @Column(name = "update_user")
    private String updateUser;

    public Patient() {
    }

    public String getNameSUSandCPF() {
        return "NOME: " + this.name.toUpperCase() + " - SUS: " + this.susNumber + " - CPF: " + this.cpf;
    }

    public String formattedBirthDate() {
        if (this.birthDate == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return this.birthDate.format(formatter);
    }

    public String getBirthDateWithAge() {
        if (this.birthDate == null) {
            return null;
        }
        Period period = Period.between(this.birthDate, LocalDate.now());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = this.birthDate.format(formatter);
        if (period.getMonths() > 0) {
            return formattedDate + " (" + period.getYears() + " anos e " + period.getMonths() + " meses)";
        }
        return formattedDate + " (" + period.getYears() + " anos)";
    }

}

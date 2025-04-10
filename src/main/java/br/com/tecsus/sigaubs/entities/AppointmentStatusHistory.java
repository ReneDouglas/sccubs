package br.com.tecsus.sigaubs.entities;

import br.com.tecsus.sigaubs.entities.converters.AppointmentStatusConverter;
import br.com.tecsus.sigaubs.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "appointment_status_history")
public class AppointmentStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = AppointmentStatusConverter.class)
    private AppointmentStatus status;

    @ManyToOne
    @JoinColumn(name = "id_appointment")
    private Appointment appointment;

    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "creation_user", updatable = false)
    private String creationUser;

    public AppointmentStatusHistory() {
    }
}

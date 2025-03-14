package br.com.tecsus.sccubs.entities;

import br.com.tecsus.sccubs.entities.converters.AppointmentStatusConverter;
import br.com.tecsus.sccubs.enums.AppointmentStatus;
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

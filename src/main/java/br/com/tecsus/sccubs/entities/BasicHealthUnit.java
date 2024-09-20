package br.com.tecsus.sccubs.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "basic_health_units")
public class BasicHealthUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String neighborhood;

    @ManyToOne
    @JoinColumn(name = "id_city_hall", updatable = false)
    private CityHall cityHall;

    @OneToMany(mappedBy = "basicHealthUnit"/*, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER*/)
    private List<SystemUser> systemUsers;

    @OneToMany(mappedBy = "basicHealthUnit")
    private List<Patient> patients = new ArrayList<>();

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

    public BasicHealthUnit() {
    }
}

package br.com.tecsus.sigaubs.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
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

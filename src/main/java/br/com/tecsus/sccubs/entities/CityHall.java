package br.com.tecsus.sccubs.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
/*@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)*/
@Table(name = "city_halls")
public class CityHall implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Boolean active;

    @OneToMany(mappedBy = "cityHall", cascade = CascadeType.ALL)
    private List<BasicHealthUnit> basicHealthUnits = new ArrayList<>();

    @OneToMany(mappedBy = "cityHall", cascade = CascadeType.ALL/*, fetch = FetchType.EAGER*/)
    private List<SystemUser> systemUsers = new ArrayList<>();

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

    public CityHall() {
    }
}

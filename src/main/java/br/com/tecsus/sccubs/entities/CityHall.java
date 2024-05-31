package br.com.tecsus.sccubs.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "city_halls")
public class CityHall implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Boolean active;

    @OneToMany(mappedBy = "cityHall", cascade = CascadeType.ALL)
    private List<BasicHealthUnit> basicHealthUnits = new ArrayList<>();

    @OneToMany(mappedBy = "cityHall", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SystemUser> systemUsers = new ArrayList<>();

    @Column(name = "creation_date", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(name = "creation_user", updatable=false)
    private String creationUser;

    @Column(name = "update_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @Column(name = "update_user")
    private String updateUser;

    public CityHall() {
    }
}

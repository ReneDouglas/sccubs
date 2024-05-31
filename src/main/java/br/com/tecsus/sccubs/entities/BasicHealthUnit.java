package br.com.tecsus.sccubs.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "basic_health_units")
public class BasicHealthUnit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String neighborhood;

    @ManyToOne
    @JoinColumn(name = "id_city_hall")
    private CityHall cityHall;

    @OneToMany(mappedBy = "basicHealthUnit", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SystemUser> systemUsers;

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

    public BasicHealthUnit() {
    }
}

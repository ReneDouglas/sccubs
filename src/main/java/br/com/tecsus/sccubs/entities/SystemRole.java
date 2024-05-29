package br.com.tecsus.sccubs.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.collection.spi.PersistentSet;
import java.io.Serializable;
import java.util.*;


@Getter
@Setter
@Entity
@Table(name = "system_roles")
public class SystemRole implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String role;
    private String title;
    private String description;
    private Boolean root;

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(name = "creation_user")
    private String creationUser;

    @Column(name = "update_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @Column(name = "update_user")
    private String updateUser;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private Set<SystemUser> users = new HashSet<>();

    public SystemRole() {
    }


}

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
@Table(name = "system_users")
public class SystemUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;
    private String name;
    private String email;
    private Boolean active;

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

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "system_users_roles",
            joinColumns = @JoinColumn(name = "id_system_user"),
            inverseJoinColumns = @JoinColumn(name = "id_system_role"))
    private List<SystemRole> roles = new ArrayList<>();

    @Transient
    private Long selectedRoleId;

    public SystemUser(){
    }

}

package br.com.tecsus.sccubs.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.*;


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

    @ManyToOne
    @JoinColumn(name = "id_basic_health_unit")
    private BasicHealthUnit basicHealthUnit;

    @ManyToOne
    @JoinColumn(name = "id_city_hall")
    private CityHall cityHall;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinTable(name = "system_users_roles",
            joinColumns = @JoinColumn(name = "id_system_user"),
            inverseJoinColumns = @JoinColumn(name = "id_system_role"))
    private Set<SystemRole> roles = new HashSet<>();

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

    @Transient
    private Long selectedRoleId;

    public SystemUser(){
    }

    public Boolean findRole(Long id) {
        for (SystemRole systemRole : roles) {
            if (Objects.equals(systemRole.getId(), id)) {
                return true;
            }
        }
        return false;
    }

    public Long getFirstRole() {
        return roles.stream().iterator().next().getId();
    }

}

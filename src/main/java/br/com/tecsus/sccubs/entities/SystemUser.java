package br.com.tecsus.sccubs.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;


@Getter
@Setter
@Entity
@Table(name = "system_users")
@NamedEntityGraph(name = "SystemUserGraph",
        attributeNodes =
                {       // Adicione relacionamentos que devem ser inicializados com esta entidade
                        @NamedAttributeNode("roles"),
                        @NamedAttributeNode("basicHealthUnit"),
                        @NamedAttributeNode("cityHall")
                }
)
public class SystemUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, updatable=false)
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

    @ManyToMany
    @JoinTable(name = "system_users_roles",
            joinColumns = @JoinColumn(name = "id_system_user"),
            inverseJoinColumns = @JoinColumn(name = "id_system_role"))
    private Set<SystemRole> roles = new HashSet<>();

    @Column(name = "creation_date", updatable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime creationDate;

    @Column(name = "creation_user", updatable=false)
    private String creationUser;

    @Column(name = "update_date")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updateDate;

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

    public String getFirstRoleTitle() {
        return roles.stream().iterator().next().getTitle();
    }

    public String getUsername() {
        return username == null || username.isEmpty() ? null : username;
    }

    public String getName() {
        return name == null || name.isEmpty() ? null : name;
    }

}

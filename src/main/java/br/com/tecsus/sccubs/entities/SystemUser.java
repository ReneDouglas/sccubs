package br.com.tecsus.sccubs.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


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
    @Transient private String confirmPassword;
    private String name;
    private String email;
    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "id_basic_health_unit")
    private BasicHealthUnit basicHealthUnit;

    @ManyToOne
    @JoinColumn(name = "id_city_hall", updatable = false)
    private CityHall cityHall;

    @ManyToMany
    @JoinTable(name = "system_users_roles",
            joinColumns = @JoinColumn(name = "id_system_user"),
            inverseJoinColumns = @JoinColumn(name = "id_system_role"))
    private Set<SystemRole> roles = new HashSet<>();

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

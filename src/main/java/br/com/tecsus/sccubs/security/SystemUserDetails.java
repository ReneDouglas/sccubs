package br.com.tecsus.sccubs.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class SystemUserDetails extends User {

    private String name;
    private String email;
    private Boolean active;
    private Long cityHallId;
    private String cityHallName;
    private Long basicHealthUnitId;

    public SystemUserDetails(String username,
                             String password,
                             Collection<? extends GrantedAuthority> authorities,
                             String name,
                             String email,
                             Boolean active,
                             Long cityHallId,
                             String cityHallName,
                             Long basicHealthUnitId) {
        super(username, password, authorities);
        this.name = name;
        this.email = email;
        this.active = active;
        this.cityHallId = cityHallId;
        this.cityHallName = cityHallName;
        this.basicHealthUnitId = basicHealthUnitId;
    }
}

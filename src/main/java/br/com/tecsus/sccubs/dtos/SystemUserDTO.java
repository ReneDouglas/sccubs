package br.com.tecsus.sccubs.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
public class SystemUserDTO implements Serializable {

    private Long id;
    private String login;
    private String password;
    private String name;
    private String email;
    private String role;

    public SystemUserDTO() {

    }
}

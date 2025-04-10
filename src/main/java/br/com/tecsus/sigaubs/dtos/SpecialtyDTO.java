package br.com.tecsus.sigaubs.dtos;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SpecialtyDTO {
    private Long id;
    private String title;
    private String description;
    private Boolean active;
    private List<ProcedureDTO> procedures = new ArrayList<>();
}

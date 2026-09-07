package ni.edu.uam.facturacionapp.model;

import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Empleado {
    private Integer id;
    private String nombres;
    private String apellidos;
    private Cargo cargo;
    private LocalDate fechaContratacion;
    private boolean activo;
}

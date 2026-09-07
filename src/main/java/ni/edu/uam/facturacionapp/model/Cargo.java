package ni.edu.uam.facturacionapp.model;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Cargo {
    private Integer id;
    private String nombre;
    private String descripcion;
    private boolean activo;
}

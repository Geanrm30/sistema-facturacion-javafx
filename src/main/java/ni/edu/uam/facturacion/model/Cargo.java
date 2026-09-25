package ni.edu.uam.facturacion.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Cargo {
    private Integer id;
    private String nombre;
    private String descripcion;
    private boolean activo;
}

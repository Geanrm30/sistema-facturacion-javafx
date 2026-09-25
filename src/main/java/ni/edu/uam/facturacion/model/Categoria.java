package ni.edu.uam.facturacion.model;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Categoria {
    private Integer id;
    private String nombre;
    private boolean activa;

    @Override
    public String toString() {
        return nombre;
    }
}

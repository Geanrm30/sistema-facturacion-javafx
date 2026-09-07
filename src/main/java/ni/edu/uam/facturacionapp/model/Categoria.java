package ni.edu.uam.facturacionapp.model;


import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Categoria {
    private Integer id;
    private String nombre;
    private boolean activo;

    @Override
    public String toString() {
        return nombre;
    }
}

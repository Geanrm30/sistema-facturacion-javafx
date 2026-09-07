package ni.edu.uam.facturacionapp.model;

import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Producto {
    private Integer id;
    private String codigo;
    private String nombre;
    private Categoria categoria;
    private BigDecimal precioVenta;
    private int existencia;
    private String rutaImagen;
    private boolean activo;

}

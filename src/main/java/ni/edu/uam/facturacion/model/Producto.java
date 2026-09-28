package ni.edu.uam.facturacion.model;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Producto {
    private Integer id;
    private String codigo;
    private String nombre;
    private Categoria categoria;
    private BigDecimal precioVenta;
    private int existencia;
    private boolean activo;

}

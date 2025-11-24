package py.gestion.sifi.dto;

import java.math.*;
import java.time.*;

import lombok.*;

@Getter @Setter
public class VentaDTO {
	private Integer id;
    private Integer clienteId;
    private Integer productoId;
    private Integer cantidad;
    private BigDecimal precio;
    private BigDecimal total;
    private LocalDate fecha;
    private Integer puntosBase;
    private Integer puntosPromo;
    private Integer puntosTotales;
}

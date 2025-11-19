package py.gestion.sifi.dto;

import java.math.*;

import lombok.*;

/**
 * DTO para la entidad BolsaPunto
 * Representa los datos que se envían o reciben en formato JSON.
 */
@Getter
@Setter
public class BolsaPuntoDTO {

    private Integer id;
    private Integer idCliente;              // Referencia a Cliente.id
    private Integer idVencimientoPunto;     // Referencia a VencimientoPunto.id
    private Integer puntajeAsignado;
    private Integer puntajeUtilizado;
    private Integer saldoPunto;
    private BigDecimal montoOperacion;

    // Opcional: campos descriptivos si querés mostrar nombres
    private String nombreCliente;
    private String vencimientoPunto;
}
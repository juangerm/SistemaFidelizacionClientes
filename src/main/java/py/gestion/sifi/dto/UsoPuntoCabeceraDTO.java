package py.gestion.sifi.dto;

import java.time.*;
import java.util.*;

import lombok.*;

@Getter @Setter
public class UsoPuntoCabeceraDTO {
    private Integer id;
    private Integer idCliente;
    private String nombreCliente;
    private Integer puntajeUtilizado;
    private LocalDate fecha;
    private Integer idConceptoPunto;
    private String concepto;
    private List<UsoPuntoDetalleDTO> detalles;
}
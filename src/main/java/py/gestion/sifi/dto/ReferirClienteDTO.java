package py.gestion.sifi.dto;

import java.time.*;

import lombok.*;

@Getter @Setter
public class ReferirClienteDTO {
    private Integer idReferido;
    private String nombreCompletoReferido;
    private Integer puntosObtenidos;
    private LocalDate fechaVencimientoPuntos;
}

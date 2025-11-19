package py.gestion.sifi.dto;

import java.time.*;

import lombok.*;

@Getter @Setter
public class UsoPuntoDetalleDTO {
    private Integer id;
    private LocalDate fechaFin;
    private Integer saldoPunto;
	private Integer idBolsaPunto;
	private Integer puntajeUtilizado;
}

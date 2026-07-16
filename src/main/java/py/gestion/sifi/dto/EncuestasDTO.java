package py.gestion.sifi.dto;

import java.time.*;
import java.util.*;

import lombok.*;

@Getter @Setter
public class EncuestasDTO {
	
    public Integer id;
    public Integer idCliente;
    public LocalDate fecha;

    public String pregunta1;
    public Integer respuesta1;

    public String pregunta2;
    public Integer respuesta2;

    public String pregunta3;
    public Integer respuesta3;

    public String pregunta4;
    public Integer respuesta4;

    public String pregunta5;
    public Integer respuesta5;
	
	private Long totalEncuestas;

	private List<EstadisticasEncuestaDTO> preguntas;
}


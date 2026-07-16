package py.gestion.sifi.dto;

import java.util.*;

import lombok.*;
@Getter @Setter
public class EstadisticasEncuestaDTO {

    public long totalEncuestas;

    public Map<String, Long> respuestasP1;
    public Map<String, Long> respuestasP2;
    public Map<String, Long> respuestasP3;
    public Map<String, Long> respuestasP4;
    public Map<String, Long> respuestasP5;
	
	private String pregunta;

	private Double promedio;
	
	private Integer totalRespuestas;

	private Map<String, Long> respuestas;

}

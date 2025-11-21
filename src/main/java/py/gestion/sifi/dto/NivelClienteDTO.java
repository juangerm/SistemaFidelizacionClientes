package py.gestion.sifi.dto;

import lombok.*;

@Getter@Setter
public class NivelClienteDTO {

    private String cliente;
    private String nivel;
    private Integer puntoMinimo;
    private Integer puntoMaximo;
    private Integer puntosAcumulados;
    private String beneficio; 

    public NivelClienteDTO(String cliente, String nivel, Integer puntoMinimo,
                           Integer puntoMaximo, Integer puntosAcumulados, String beneficio) {
        this.cliente = cliente;
        this.nivel = nivel;
        this.puntoMinimo = puntoMinimo;
        this.puntoMaximo = puntoMaximo;
        this.puntosAcumulados = puntosAcumulados;
        this.beneficio = beneficio;
    }

}



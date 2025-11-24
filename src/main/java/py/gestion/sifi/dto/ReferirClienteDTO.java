package py.gestion.sifi.dto;

import lombok.*;

@Getter @Setter
public class ReferirClienteDTO {

    private Integer id;
    private ClienteDTO cliente;
    private ClienteDTO referido;
    private String fechaRegistro;
    private Integer puntosObtenidos;
    private String fechaVencimientoPuntos;
}


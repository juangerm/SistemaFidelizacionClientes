package py.gestion.sifi.dto;

import java.time.*;

import lombok.*;

@Getter @Setter
public class PromocionDTO {
    private Integer id;
    private String nombre;
    private LocalDate inicio;
    private LocalDate fin;
    private Integer productoId;
    private Integer puntosExtra;
}

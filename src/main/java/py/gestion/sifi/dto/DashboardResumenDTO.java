package py.gestion.sifi.dto;

import lombok.*;

@Getter @Setter
public class DashboardResumenDTO {

    private Long totalClientes;
    private Long totalBolsas;

    private Long totalCanjes;
    private Long canjesHoy;
    private Long canjesMes;

    private Long puntosAsignados;
    private Long puntosUtilizados;
    private Long puntosSaldo;
}

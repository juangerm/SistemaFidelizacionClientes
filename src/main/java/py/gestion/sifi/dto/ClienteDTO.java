package py.gestion.sifi.dto;

import lombok.*;

@Getter @Setter
public class ClienteDTO {
    private Integer id;
    private String nombre;
    private String apellido;
    private Integer codNacionalidad;
    private Integer codTipoDocumento;
    private String numeroDocumento;
    private String fechaNacimiento;
    private String celular;
    private String email;
}



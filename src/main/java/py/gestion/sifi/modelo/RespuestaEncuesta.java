package py.gestion.sifi.modelo;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

@Entity
@Getter @Setter
public class RespuestaEncuesta {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id @Hidden
	@ReadOnly
    private Integer id;

    @Required
    @Column(name = "descripcion")
    private String descripcion;
    
    @Required
    @Column(name = "calificacion")
    private Integer calificacion;
}

package py.gestion.sifi.modelo;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

@View(members = "descripcion;"
		+ "puntoMinimo, puntoMaximo;"
		+ "beneficios")

@Entity
@Getter @Setter
public class NivelFidelizacion {
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id @Hidden
	@ReadOnly
	private Integer id;
    
	@Column(name = "descipcion")
    private String descripcion;
    
	@Column(name = "punto_minimo")
    private Integer puntoMinimo;
    
	@Column(name = "punto_maximo")
    private Integer puntoMaximo;
    
	@Column(name = "beneficios")
    private String beneficios;
}
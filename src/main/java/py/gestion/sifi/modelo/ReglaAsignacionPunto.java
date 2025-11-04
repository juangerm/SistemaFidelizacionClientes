package py.gestion.sifi.modelo;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;
import py.gestion.sifi.calculador.*;

@View(members = "datos[#limiteInferior, limiteSuperior;"
		+ "puntoEquivalente, montoEquivalente;]")
@Entity
@Getter@Setter
public class ReglaAsignacionPunto {
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id @Hidden
	@ReadOnly
	private Integer id;
	
	//@OnChange(ReglaAsignacionPuntoAlCambiarLimiteInferiorAccion.class)
	@Money
	@Column(name = "limite_Inferior")
	private Integer limiteInferior;
	
	@Money
	@Column(name = "limite_Superior")
	private Integer limiteSuperior;
	
	@DefaultValueCalculator(DefectoPunto.class)
	@Column(name = "punto_Equivalente")
	//@ReadOnly
	private Integer puntoEquivalente;
	
	@Money
	@Column(name = "monto_Equivalente")
	private Integer montoEquivalente;

}

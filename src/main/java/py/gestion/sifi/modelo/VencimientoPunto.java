package py.gestion.sifi.modelo;

import java.time.*;

import javax.persistence.*;

import org.apache.commons.logging.*;
import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.jpa.*;

import lombok.*;
import py.gestion.sifi.alCambiar.*;

@View(name="completo", members = "fechaInicio, diasValidez, fechaFin")
@View(members = "fechaInicio; diasValidez; fechaFin")
@View(name="reSimple", members = "diasValidez, fechaFin")
@Entity
@Getter@Setter
public class VencimientoPunto {
	
	public static Log log = LogFactory.getLog(VencimientoPunto.class);
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id @Hidden
	@ReadOnly
	private Integer id;

	@Column(name = "fecha_Inicio", columnDefinition = "date")
	@DefaultValueCalculator(CurrentLocalDateCalculator.class)
	private LocalDate fechaInicio;
	
	@ReadOnly
	@Column(name = "fecha_Fin", columnDefinition = "date")
	private LocalDate fechaFin;
	
	@OnChange(VencimientoPuntoDiasAlCambiar.class)
	@Column(name = "dias_Validez")
	private Integer diasValidez;
	
	public static VencimientoPunto buscarPorCodigo(Integer id) {
		 try {
			 VencimientoPunto vencimientoPunto = (VencimientoPunto) XPersistence.getManager()
						.createQuery("Select o from VencimientoPunto o where o.id = :id")
						.setParameter("id", id).getResultList().get(0);
				return vencimientoPunto;
		} catch (Exception e) {
			log.error("buscarPorcodigo " + e.getMessage());
		}
		return null;
	}

}

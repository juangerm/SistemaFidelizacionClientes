package py.gestion.sifi.modelo;

import java.time.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;

import lombok.*;
import py.gestion.sifi.calculador.*;

@View(members = "cliente;"
		+ "nuevo[referido];"
		+ "datos[fechaRegistro, puntosObtenidos;];"
		+ "vencimientoPunto;")

@Entity
@Getter@Setter
public class ReferirCliente {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id @Hidden
	@ReadOnly
	private Integer id;
	
	@ReferenceView("simple")
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "idCliente", columnDefinition = "integer", foreignKey = @ForeignKey(name = "Fk_referirCliente_cliente"))
	@Required
	@NoModify@NoCreate@NoFrame
	private Cliente cliente;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idReferido", foreignKey = @ForeignKey(name = "Fk_referirCliente_referido"))
    @ReferenceView("simple")
	@Required
	@NoFrame
	@NoSearch
    private Cliente referido;
	
	@ReadOnly
	@DefaultValueCalculator(CurrentLocalDateCalculator.class)
	private LocalDate fechaRegistro;

	@ReadOnly
	@DefaultValueCalculator(DefectoPuntoReferido.class)
	private Integer puntosObtenidos;
	
	@ReferenceView("reSimple")
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "idVencimientoPunto", columnDefinition = "integer", foreignKey = @ForeignKey(name = "Fk_referirCliente_vencimientoPunto"))
	@Required
	@NoModify @NoSearch
	private VencimientoPunto vencimientoPunto;
}

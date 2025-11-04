package py.gestion.sifi.modelo;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;
import py.gestion.sifi.alCambiar.*;
import py.gestion.sifi.calculador.*;

@Tab(name = "simple",properties ="id, vencimientoPunto.fechaFin, puntajeUtilizado, saldoPunto" )
@View(name = "simple", members = "id, vencimientoPunto, puntajeUtilizado, saldoPunto")
@View(members = "cliente; vencimientoPunto; datos[#montoOperacion, puntajeAsignado; saldoPunto, puntajeUtilizado;]")
@Entity
@Getter@Setter
public class BolsaPunto {
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id @Hidden
	@ReadOnly
	private Integer id;
	
	@ReferenceView("simple")
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "idCliente", columnDefinition = "integer", foreignKey = @ForeignKey(name = "Fk_bolsaPunto_cliente"))
	//@DescriptionsList
	//@Required
	@NoModify@NoCreate@NoFrame
	private Cliente cliente;
	
	@ReferenceView("completo")
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "idVencimientoPunto", columnDefinition = "integer", foreignKey = @ForeignKey(name = "Fk_bolsaPunto_vencimientoPunto"))
	//@DescriptionsList
	//@Required
	@NoModify//@NoSearch
	private VencimientoPunto vencimientoPunto;
	
	@Column(name = "puntaje_Asignado")
	private Integer puntajeAsignado;
	
	@DefaultValueCalculator(DefectoPuntajeUtilizado.class)
	@Column(name = "puntaje_Utilizado")
	private Integer puntajeUtilizado;
	
	@OnChange(BolsaPuntoAlCambiarPuntajeUtilizadoAccion.class)
	@Column(name = "saldo_Punto")
	private Integer saldoPunto;
	
	@Money
	@Column(name = "monto_Operacion")
	private BigDecimal montoOperacion;

}

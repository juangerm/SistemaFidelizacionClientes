package py.gestion.sifi.modelo;

import java.time.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;

import lombok.*;
import py.gestion.sifi.acciones.*;
import py.gestion.sifi.validador.*;

@Tab(name="simple", properties = "id, nombre, apellido, numeroDocumento")
@View(name = "simple",members = "cliente[#id, nombre, apellido, numeroDocumento;]")
@View(members = "datos[#nombre, apellido, nacionalidad;"
		+ "tipoDocumento, numeroDocumento, fechaNacimiento;"
		+ "celular, email, puntosAcumulados;]")
@Entity @Getter @Setter
@Table(
  name = "cliente",
  uniqueConstraints = {
    @UniqueConstraint(name="uq_cliente_doc", columnNames={"cod_tipo_documento","numero_documento"})
  }
)

public class Cliente {
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id @Hidden
	@ReadOnly
	private Integer id;
	
	
	@Column(name = "nombre")
	private String nombre;
	
	@Column(name = "apellido")
	private String apellido;
	
	@PropertyValidator(ValidadorNumeroDocumento.class)
	@Column(name = "numero_documento", length = 12)
	private String numeroDocumento;
	
	@OnChange(CalcularPuntosAcumuladosAccion.class)
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "cod_tipo_documento", columnDefinition = "integer", foreignKey = @ForeignKey(name = "Fk_cliente_tipoDocumento"))
	@DescriptionsList
	@Required
	@NoModify@NoCreate
	private TipoDocumento tipoDocumento;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "cod_nacionalidad", columnDefinition = "integer", foreignKey = @ForeignKey(name = "Fk_persona_nacionalidad"))
	@ReferenceView("basico")
	@NoFrame
	//@DescriptionsList
	@Required
	@NoModify@NoCreate
	//@DefaultValueCalculator(DefectoNacionalidad.class)
	private Nacionalidad nacionalidad;
	
	@Stereotype("EMAIL")
	@Column(name="email")
	@DisplaySize(50)
	private String email;
	
	@Stereotype("TELEPHONE")
	@Column(name="celular")
	private String celular;
	
	@Column(name = "fecha_nacimiento", columnDefinition = "date")
	@DefaultValueCalculator(CurrentLocalDateCalculator.class)
//	@OnChange(PersonaFisicaAlCambiarFechaNacimiento.class)
	private LocalDate fechaNacimiento;
	
	@ReadOnly
	@Column(name = "puntos_acumulados", columnDefinition = "integer")
	private Integer puntosAcumulados;

}

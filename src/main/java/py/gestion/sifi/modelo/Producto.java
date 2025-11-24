package py.gestion.sifi.modelo;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

@Entity
@Getter @Setter
@Tab(name = "productoTab", properties = "id, nombre, puntosRequeridos, precioReferencia, activo")
@View(name = "simple", members = "nombre, puntosRequeridos")
@View(members =
	"datos[" +
		"nombre, descripcion;" +
		"puntosRequeridos, precioReferencia;" +
		"activo;" +
	"]")
@View(name = "promocion", members = "nombre, precioReferencia")

public class Producto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Hidden
	private Integer id;

	@Required
	@Column(length = 255, nullable = false)
	private String nombre;

	@Stereotype("MEMO")
	@Column(length = 2000)
	private String descripcion;

	@Required
	@Column(name = "puntos_requeridos", nullable = false)
	private Integer puntosRequeridos;

	@Money
	@Column(name = "precio_referencia")
	private BigDecimal precioReferencia;

	@Column(nullable = false)
	private Boolean activo = true;
}

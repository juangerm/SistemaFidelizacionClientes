package py.gestion.sifi.modelo;



import java.time.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;

import lombok.*;
import py.gestion.sifi.calculador.*;

@View(members = "nombre;"
		+ "inicio, fin, puntosExtra;"
		+ "producto[producto];")
@Entity
@Getter @Setter
public class Promocion {
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id @Hidden
	@ReadOnly
	private Integer id;
	
	@Column(name = "nombre")
    private String nombre;
	
	@ReadOnly
	@DefaultValueCalculator(CurrentLocalDateCalculator.class)
	@Column(name = "inicio")
    private LocalDate inicio;
	
	@DefaultValueCalculator(CurrentLocalDateCalculator.class)
	@Column(name = "fin")
    private LocalDate fin;
	
	@ReferenceView("promocion")
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "idProducto", columnDefinition = "integer", foreignKey = @ForeignKey(name = "Fk_promocion_producto"))
	@NoModify@NoCreate @NoFrame
    private Producto producto;
    
	@DefaultValueCalculator(DefectoPunto.class)
    @Column(name = "puntosExtras")
    private Integer puntosExtra;
	
	public Promocion() {
    	this.inicio = LocalDate.now();
    }
}

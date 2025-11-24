package py.gestion.sifi.modelo;

import java.time.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.jpa.*;

import lombok.*;
import py.gestion.sifi.calculador.*;

@View(members = "cliente;"
		+ "nuevo[referido];"
		+ "datos[fechaRegistro, puntosObtenidos;];")
		//+ "vencimientoPunto;")

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
	
	/* Se crea de forma automatica en el metodo cargar puntos*/
//	@ReferenceView("reSimple")
//	@ManyToOne(fetch = FetchType.LAZY, optional = true)
//	@JoinColumn(name = "idVencimientoPunto", columnDefinition = "integer", foreignKey = @ForeignKey(name = "Fk_referirCliente_vencimientoPunto"))
//	@Required
//	@NoModify @NoSearch
//	private VencimientoPunto vencimientoPunto;
	
	@PrePersist
    public void cargarPuntos() {
		
		if (cliente == null || referido == null) {
            System.out.println("Valores incompletos, no se se procede a la carga.");
            return;
        }
		
		//generar bolsa de puntos para cliente
        BolsaPunto bolsa = new BolsaPunto();
        bolsa.setCliente(this.cliente);
        //bolsa.setMontoOperacion(this.total);
        bolsa.setPuntajeAsignado(this.puntosObtenidos);
        bolsa.setPuntajeUtilizado(0);
        bolsa.setSaldoPunto(this.puntosObtenidos);
        
      //generar bolsa de puntos para nuevo cliente
        BolsaPunto bolsaN = new BolsaPunto();
        bolsaN.setCliente(this.referido);
        //bolsaN.setMontoOperacion(this.total);
        bolsaN.setPuntajeAsignado(this.puntosObtenidos);
        bolsaN.setPuntajeUtilizado(0);
        bolsaN.setSaldoPunto(this.puntosObtenidos);

        //generar vencimiento punto
        VencimientoPunto vp = new VencimientoPunto();
        vp.setFechaInicio(this.fechaRegistro);
        vp.setDiasValidez(30);
        vp.setFechaFin(this.fechaRegistro.plusDays(30));
        XPersistence.getManager().persist(vp);

        bolsa.setVencimientoPunto(vp);
        bolsaN.setVencimientoPunto(vp);
        // persistir la bolsa
        XPersistence.getManager().persist(bolsa);
        XPersistence.getManager().persist(bolsaN);
		
	}
}

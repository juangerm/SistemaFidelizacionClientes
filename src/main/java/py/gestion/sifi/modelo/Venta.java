package py.gestion.sifi.modelo;

import java.math.*;
import java.time.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.jpa.*;

import lombok.*;
import py.gestion.sifi.alCambiar.*;

@Entity
@Getter @Setter
@View(members =
    "cliente;" +
    "producto[producto];" +
    "cantidad, precio, total;" +
    "fecha;" +
    "puntosBase, puntosPromo, puntosTotales;"
)
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Hidden @ReadOnly
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @ReferenceView("simple")
    @Required
    @NoCreate @NoModify @NoFrame
    private Cliente cliente;

    @OnChange(PrecioAlCambiarProducto.class)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idProducto", columnDefinition = "integer", foreignKey = @ForeignKey(name = "Fk_venta_producto"))
    @ReferenceView("simple")
    @Required
    @NoCreate @NoModify @NoFrame
    private Producto producto;

    @OnChange(TotalAlCambiarCantidad.class)
    @Required
    @Column(name = "cantidad")
    private Integer cantidad;

    @ReadOnly
    @Column(name = "precio")
    private BigDecimal precio;
    
    @ReadOnly
    @Column(name = "total")
    private BigDecimal total;

    @Column(name = "fecha")
    @ReadOnly
    @DefaultValueCalculator(CurrentLocalDateCalculator.class)
    private LocalDate fecha;

    @Column(name = "puntosBase")
    @ReadOnly
    private Integer puntosBase;

    @Column(name = "puntosPromo")
    @ReadOnly
    private Integer puntosPromo;

    @Column(name = "puntosTotales")
    @ReadOnly
    private Integer puntosTotales;
    
    public Venta() {
    	this.fecha = LocalDate.now();
    	//this.precio = PrecioAlCambiarProducto;
    }

    @PrePersist
    public void calcularPuntos() {
    	
    	if (total == null || producto == null) {
            System.out.println("Valores incompletos, no se calculan puntos.");
            return;
        }

        //Puntos base = segun el total se busca en la regla de puntos
    	Integer montoequivalente = (Integer) XPersistence.getManager()
				.createNativeQuery(" select monto_equivalente "
						+ "from reglaasignacionpunto "
						+ "WHERE ((:montoOperacion BETWEEN limite_inferior AND limite_superior) "
						+ " OR (limite_superior IS NULL AND :montoOperacion >= limite_inferior))")
				.setParameter("montoOperacion", total)
				.getSingleResult();
    	System.out.println("calcula el monto por el cual a dividir "+ montoequivalente);
    	
    	Integer puntoEquivalente = (Integer) XPersistence.getManager()
					.createNativeQuery(" select punto_equivalente "
							+ "from reglaasignacionpunto "
							+ "WHERE  ((:montoOperacion BETWEEN limite_inferior AND limite_superior) "
							+ " OR (limite_superior IS NULL AND :montoOperacion >= limite_inferior))")
					.setParameter("montoOperacion", total)
					.getSingleResult();
    	System.out.println("se obtiene el punto por el cual multiplicar "+puntoEquivalente);
    	
    	Integer calculoaux = (total.divide(new BigDecimal(montoequivalente)).intValue())*puntoEquivalente;
    	
        this.puntosBase = calculoaux;

        //Buscar promociones vigentes segun producto o solo fecha
        LocalDate hoy = LocalDate.now();

        Long extraLong = (Long)XPersistence.getManager()
            .createQuery(
                "SELECT COALESCE(sum(p.puntosExtra),0) FROM Promocion p " +
                " WHERE p.inicio <= :hoy AND p.fin >= :hoy "
                + " AND (p.producto.id = :prod OR p.producto IS NULL) "
            )
            .setParameter("prod", this.producto.getId())
            .setParameter("hoy", hoy)
            .getSingleResult();
        System.out.println("se busca alguna promocion "+extraLong);
        Integer extra = extraLong.intValue();
        this.puntosPromo = extra;

        //Total de puntos
        this.puntosTotales = this.puntosBase + this.puntosPromo;

        //Registrar en el cliente
       // cliente.setPuntos(cliente.getPuntos() + this.puntosTotales);
        
        //generar bolsa de puntos
        BolsaPunto bolsa = new BolsaPunto();
        bolsa.setCliente(this.cliente);
        bolsa.setMontoOperacion(this.total);
        bolsa.setPuntajeAsignado(this.puntosTotales);
        bolsa.setPuntajeUtilizado(0);
        bolsa.setSaldoPunto(this.puntosTotales);

        //generar vencimiento punto
//        VencimientoPunto vp = XPersistence.getManager()
//            .createQuery(
//                "FROM VencimientoPunto v WHERE :hoy BETWEEN v.fechaInicio AND v.fechaFin",
//                VencimientoPunto.class
//            )
//            .setParameter("hoy", hoy)
//            .setMaxResults(1)
//            .getSingleResult();
        VencimientoPunto vp = new VencimientoPunto();
        vp.setFechaInicio(hoy);
        vp.setDiasValidez(30);
        vp.setFechaFin(hoy.plusDays(30));
        XPersistence.getManager().persist(vp);

        bolsa.setVencimientoPunto(vp);

        // persistir la bolsa
        XPersistence.getManager().persist(bolsa);
    }
}

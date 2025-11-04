package py.gestion.sifi.modelo;

import java.time.*;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;

import lombok.*;

@Entity @Getter @Setter
@Table(name = "usopuntocabecera")
@Tab(properties="id, fecha, cliente.nombre, conceptoPunto.concepto, puntajeUtilizado")
@View(name="simple", members="id")
@View(members=
  "cliente; " +
  "datos[#puntajeUtilizado, fecha, conceptoPunto;]; " +
  "detalle"  // se mostrará solo-lectura
)
public class UsoPuntoCabecera {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Hidden @ReadOnly
  private Integer id;

  @ReferenceView("simple")
  @ManyToOne(fetch = FetchType.LAZY, optional = true)
  @JoinColumn(name = "idcliente", columnDefinition = "integer",
              foreignKey = @ForeignKey(name = "fk_bolsapunto_cliente"))
  //@DescriptionsList(descriptionProperties = "nombre, apellido, numeroDocumento")
  @Required @NoModify @NoCreate @NoFrame
  private Cliente cliente;

  @Positive
  @Column(name = "puntaje_utilizado")
  private Integer puntajeUtilizado; // si es null, la acción usará el puntoRequerido del concepto

  @Column(name = "fecha", columnDefinition = "date")
  @DefaultValueCalculator(CurrentLocalDateCalculator.class)
  private LocalDate fecha;

  @ReferenceView("simple")
  @ManyToOne(fetch = FetchType.LAZY, optional = true)
  @JoinColumn(name = "idconceptopunto", columnDefinition = "integer",
              foreignKey = @ForeignKey(name = "fk_usopuntocabecera_conceptopunto"))
  @DescriptionsList(descriptionProperties = "concepto")
  @Required @NoModify @NoCreate @NoFrame
  private ConceptoPunto conceptoPunto;

  /** 
   * IMPORTANTE: El detalle se genera en BD por fn_usar_puntos().
   * Lo mostramos solo lectura; sin cascade para no intentar persistir manual.
   */
  @OneToMany(mappedBy = "usoPuntoCabecera")
  @ListProperties("id, bolsaPunto.vencimientoPunto.fechaFin, bolsaPunto.puntajeUtilizado, bolsaPunto.saldoPunto")
  @ReadOnly
  private List<UsoPuntoDetalle> detalle;
}

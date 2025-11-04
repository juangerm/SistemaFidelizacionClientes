package py.gestion.sifi.modelo;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;

import lombok.*;
import py.gestion.sifi.calculador.*;

@Entity @Getter @Setter
@Table(name = "conceptopunto" /*,
  uniqueConstraints = { @UniqueConstraint(name="uq_conceptopunto_concepto", columnNames = {"concepto"}) } */
)
@View(name="simple", members="concepto; puntoRequerido")
@Tab(properties="id, concepto, puntoRequerido")
public class ConceptoPunto {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Hidden @ReadOnly
  private Integer id;

  @NotBlank
  @Column(name = "concepto")
  private String concepto;

  @DefaultValueCalculator(DefectoPunto.class)
  @Min(1)
  @Column(name = "punto_requerido")
  private Integer puntoRequerido;
}

package py.gestion.sifi.modelo;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;

import lombok.*;
import py.gestion.sifi.alCambiar.*;
import py.gestion.sifi.calculador.*;

@Entity @Getter @Setter
@Table(name = "reglaasignacionpunto")
@View(members = 
  "datos[" +
    "limiteInferior, limiteSuperior;" +
    "puntoEquivalente, montoEquivalente;" +
  "]"
)
@Tab(properties="id, limiteInferior, limiteSuperior, montoEquivalente, puntoEquivalente")
public class ReglaAsignacionPunto {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Hidden @ReadOnly
  private Integer id;
  
  @OnChange(ValidadLimiteInferiorAlCambiarAccion.class)
  //@DefaultValueCalculator(DefectoCalcularLimiteInferior.class)
  @Money
  @Column(name = "limite_inferior")
  private Integer limiteInferior;   // puede ser null (rango abierto inferior)
  
  @OnChange(ValidadLimiteSuperiorAlCambiarAccion.class)
  @Money
  @Column(name = "limite_superior")
  private Integer limiteSuperior;   // puede ser null (rango abierto superior)

  @DefaultValueCalculator(DefectoPunto.class)
  @Min(1)
  @Column(name = "punto_equivalente")
  private Integer puntoEquivalente; // > 0

  @Money
  @DefaultValueCalculator(DefectoPunto.class)
  @Min(1)
  @Column(name = "monto_equivalente")
  private Integer montoEquivalente; // > 0

  /** Validación ligera en capa modelo */
  @PrePersist @PreUpdate
  private void validarCoherencia() {
    if (montoEquivalente != null && montoEquivalente <= 0)
      throw new IllegalArgumentException("montoEquivalente debe ser > 0");
    if (puntoEquivalente != null && puntoEquivalente <= 0)
      throw new IllegalArgumentException("puntoEquivalente debe ser > 0");
    if (limiteInferior != null && limiteSuperior != null && limiteInferior > limiteSuperior)
      throw new IllegalArgumentException("Rango inválido: limiteInferior > limiteSuperior");
  }
}

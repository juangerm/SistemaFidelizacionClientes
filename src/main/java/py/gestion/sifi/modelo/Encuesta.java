package py.gestion.sifi.modelo;

import java.time.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;

import lombok.*;
import py.gestion.sifi.calculador.*;

@View(members=
    "cliente;" +
    "fecha,mensaje;" 
    + "pregunta1, respuesta1;"
    + "pregunta2, respuesta2;"
    + "pregunta3, respuesta3;"
    + "pregunta4, respuesta4;"
    + "pregunta5, respuesta5;"
    )
@Entity
@Getter@Setter
public class Encuesta {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id @Hidden
	@ReadOnly
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCliente")
    @ReferenceView("simple")
    @Required
    @NoCreate @NoModify @NoFrame
    private Cliente cliente;

    @ReadOnly
    @DefaultValueCalculator(CurrentLocalDateCalculator.class)
    @Column(name = "fecha")
    private LocalDate fecha;
    
    @Column(name = "pregunta1")
    @ReadOnly
    @DefaultValueCalculator(Pregunta1Calculator.class)
    @DisplaySize(60)
    private String pregunta1;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idRespuesta1",columnDefinition = "integer", foreignKey = @ForeignKey(name = "Fk_encuesta_respuesta1"))
    @DescriptionsList(descriptionProperties = "descripcion")
    @NoCreate @NoModify
    private RespuestaEncuesta respuesta1;
    
    @Column(name = "pregunta2")
    @ReadOnly
    @DefaultValueCalculator(Pregunta2Calculator.class)
    @DisplaySize(60)
    private String pregunta2;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idRespuesta2",columnDefinition = "integer", foreignKey = @ForeignKey(name = "Fk_encuesta_respuesta2"))
    @DescriptionsList(descriptionProperties = "descripcion")
    @NoCreate @NoModify
    private RespuestaEncuesta respuesta2;
    
    @Column(name = "pregunta3")
    @ReadOnly
    @DefaultValueCalculator(Pregunta3Calculator.class)
    @DisplaySize(60)
    private String pregunta3;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idRespuesta3",columnDefinition = "integer", foreignKey = @ForeignKey(name = "Fk_encuesta_respuesta3"))
    @DescriptionsList(descriptionProperties = "descripcion")
    @NoCreate @NoModify
    private RespuestaEncuesta respuesta3;
    
    @Column(name = "pregunta4")
    @ReadOnly
    @DefaultValueCalculator(Pregunta4Calculator.class)
    @DisplaySize(60)
    private String pregunta4;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idRespuesta4",columnDefinition = "integer", foreignKey = @ForeignKey(name = "Fk_encuesta_respuesta4"))
    @DescriptionsList(descriptionProperties = "descripcion")
    @NoCreate @NoModify 
    private RespuestaEncuesta respuesta4;
    
    @Column(name = "pregunta5")
    @ReadOnly
    @DefaultValueCalculator(Pregunta5Calculator.class)
    @DisplaySize(60)
    private String pregunta5;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idRespuesta5",columnDefinition = "integer", foreignKey = @ForeignKey(name = "Fk_encuesta_respuesta5"))
    @DescriptionsList(descriptionProperties = "descripcion")
    @NoCreate @NoModify 
    private RespuestaEncuesta respuesta5;
  
    @Transient
    @ReadOnly
    private String mensaje;
    
    public Encuesta() {
    	this.fecha = LocalDate.now();
        this.pregunta1 = "¿Cómo califica la atención recibida?";
        this.pregunta2 = "¿Qué tan satisfecho está con los beneficios del programa?";
        this.pregunta3 = "¿Recomendaría el servicio?";
        this.pregunta4 = "¿Cómo califica la facilidad de uso?";
        this.pregunta5 = "¿Tiene sugerencias para mejorar?";
    }

}

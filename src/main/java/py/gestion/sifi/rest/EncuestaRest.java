package py.gestion.sifi.rest;

import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.openxava.jpa.*;

import py.gestion.sifi.dto.*;
import py.gestion.sifi.modelo.*;
@Path("/api")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EncuestaRest {
	
	@Path("/CrearEncuestas")
    @POST
    public Response crearEncuesta(Encuesta encuesta) {
        XPersistence.getManager().persist(encuesta);
        XPersistence.commit();
        return Response.ok(encuesta).build();
    }
	
	private EncuestasDTO toDTO(Encuesta e) {
	    EncuestasDTO dto = new EncuestasDTO();
	    dto.id = e.getId();
	    dto.idCliente = e.getCliente() != null ? e.getCliente().getId() : null;
	    dto.fecha = e.getFecha();

	    dto.pregunta1 = e.getPregunta1();
	    dto.respuesta1 = e.getRespuesta1() != null ? e.getRespuesta1().getId() : null;

	    dto.pregunta2 = e.getPregunta2();
	    dto.respuesta2 = e.getRespuesta2() != null ? e.getRespuesta2().getId() : null;

	    dto.pregunta3 = e.getPregunta3();
	    dto.respuesta3 = e.getRespuesta3() != null ? e.getRespuesta3().getId() : null;

	    dto.pregunta4 = e.getPregunta4();
	    dto.respuesta4 = e.getRespuesta4() != null ? e.getRespuesta4().getId() : null;

	    dto.pregunta5 = e.getPregunta5();
	    dto.respuesta5 = e.getRespuesta5() != null ? e.getRespuesta5().getId() : null;

	    return dto;
	}
	
	@Path("/ListarEncuestas")
    @GET
    public Response obtenerTodas() {
        var list = XPersistence.getManager()
                .createQuery("FROM Encuesta e ORDER BY e.id DESC", Encuesta.class)
                .getResultList();

        // Convertir las entidades a DTOs
        var dtoList = list.stream()
                .map(this::toDTO)
                .toList();

        return Response.ok(dtoList).build();
    }
	
	@Path("/encuesta/estadisticas")
	@GET
	public Response estadisticas() {

	    var em = XPersistence.getManager();
	    
	    // Total de encuestas
	    Long total = em.createQuery("SELECT COUNT(e) FROM Encuesta e", Long.class)
	            .getSingleResult();
	    
	    // Promedio general (de todos los puntajes de las respuestas)
	    Double promedio = em.createQuery(
	            "SELECT AVG((r1.calificacion + r2.calificacion + r3.calificacion + r4.calificacion + r5.calificacion)/5) " +
	            "FROM Encuesta e " +
	            "LEFT JOIN e.respuesta1 r1 " +
	            "LEFT JOIN e.respuesta2 r2 " +
	            "LEFT JOIN e.respuesta3 r3 " +
	            "LEFT JOIN e.respuesta4 r4 " +
	            "LEFT JOIN e.respuesta5 r5",
	            Double.class
	    ).getSingleResult();

	    // Tomamos 1 encuesta para obtener los textos de las preguntas
	    Encuesta ejemplo = em.createQuery("FROM Encuesta e", Encuesta.class)
	            .setMaxResults(1)
	            .getSingleResult();

	    Map<String, Object> result = new LinkedHashMap<>();

	    //Agregamos total
	    result.put("totalEncuestas", total);
	    result.put("promedioGeneral", promedio != null ? String.format("%.0f", promedio) : null);

	    //Agregamos estadísticas por pregunta
	    result.put(ejemplo.getPregunta1(), contarRespuestas(em, "respuesta1"));
	    result.put(ejemplo.getPregunta2(), contarRespuestas(em, "respuesta2"));
	    result.put(ejemplo.getPregunta3(), contarRespuestas(em, "respuesta3"));
	    result.put(ejemplo.getPregunta4(), contarRespuestas(em, "respuesta4"));
	    result.put(ejemplo.getPregunta5(), contarRespuestas(em, "respuesta5"));

	    return Response.ok(result).build();
	}

	
	@SuppressWarnings("unchecked")
	private Map<String, Long> contarRespuestas(javax.persistence.EntityManager em, String campo) {

	    List<Object[]> lista = em.createQuery(
	            "SELECT r.descripcion, COUNT(e) " +
	            "FROM Encuesta e LEFT JOIN e." + campo + " r " +
	            "GROUP BY r.descripcion"
	    ).getResultList();

	    return lista.stream()
	            .collect(java.util.stream.Collectors.toMap(
	                    row -> row[0] != null ? row[0].toString() : "Sin respuesta",
	                    row -> (Long) row[1]
	            ));
	}


}

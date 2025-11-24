package py.gestion.sifi.acciones;

import javax.persistence.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;

public class VerEstadisticasEncuestaAccion extends ViewBaseAction {

	@Override
    public void execute() throws Exception {

        // Total de encuestas ingresadas
        Query total = XPersistence.getManager()
            .createQuery("SELECT COUNT(e) FROM Encuesta e");
        Long cantidad = (Long) total.getSingleResult();

        // Promedio general sumando todas las respuestas
        Query promedioGen = XPersistence.getManager()
            .createQuery(
                "SELECT AVG( (r1.calificacion + r2.calificacion + r3.calificacion + r4.calificacion + r5.calificacion) / 5 ) " +
                "FROM Encuesta e " +
                "JOIN e.respuesta1 r1 " +
                "JOIN e.respuesta2 r2 " +
                "JOIN e.respuesta3 r3 " +
                "JOIN e.respuesta4 r4 " +
                "JOIN e.respuesta5 r5 "
            );

        Double promedio = (Double) promedioGen.getSingleResult();

        getView().setValue(
            "mensaje",
            "Total encuestas: " + cantidad +
            " | Promedio General: " + String.format("%.0f", promedio)
        );
    }
}
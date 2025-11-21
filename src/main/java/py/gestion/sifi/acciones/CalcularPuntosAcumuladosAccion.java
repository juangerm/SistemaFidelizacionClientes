package py.gestion.sifi.acciones;

import java.time.*;

import javax.persistence.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;

public class CalcularPuntosAcumuladosAccion extends OnChangePropertyBaseAction {

    @Override
    public void execute() throws Exception {

        Integer idCliente = getView().getValueInt("id");

        if (idCliente == null || idCliente == 0) return;

        EntityManager em = XPersistence.getManager();
//        Cliente cliente = em.find(Cliente.class, idCliente);
        LocalDate hoy = LocalDate.now();

        Query q = em.createQuery(
        		"SELECT COALESCE(sum(b.puntajeAsignado),0) FROM BolsaPunto b "
        			+ " JOIN b.vencimientoPunto vp " 
            		+ " WHERE b.cliente.id = :idCliente "
            		+ " AND vp.fechaFin >= :hoy ");

        q.setParameter("idCliente", idCliente);
        q.setParameter("hoy", hoy);

        Integer puntosAcumulados = ((Number) q.getSingleResult()).intValue();
        System.out.println("puntosAcumulados " + puntosAcumulados);
        
        Query o = em.createQuery(
        		"SELECT COALESCE(sum(b.puntosObtenidos),0) FROM ReferirCliente b "
        			+ " JOIN b.vencimientoPunto vp " 
            		+ " WHERE (b.cliente.id = :idCliente OR b.referido.id = :idCliente) "
            		+ " AND vp.fechaFin >= :hoy ");

        o.setParameter("idCliente", idCliente);
        o.setParameter("hoy", hoy);

        Integer puntosObtenidos = ((Number) o.getSingleResult()).intValue();
        System.out.println("puntosObtenidos " + puntosObtenidos);
        
        Integer total = puntosAcumulados + puntosObtenidos;
        
        System.out.println("puntos " + total);
        getView().setValue("puntosAcumulados", total);
        //cliente.setPuntosAcumulados(puntos);
//        System.out.println("total "+puntos);
//        em.merge(cliente);
//        em.flush();
//        getView().setValue("cliente.puntosAcumulados", puntos);
    }
}

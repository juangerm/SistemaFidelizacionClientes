package py.gestion.sifi.acciones;

import javax.persistence.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;

public class UsarPuntosFifoAction extends ViewBaseAction {

  @Override
  public void execute() throws Exception {
    Integer idCliente  = (Integer) getView().getValue("cliente.id");
    Integer idConcepto = (Integer) getView().getValue("conceptoPunto.id");
    Integer puntos     = (Integer) getView().getValue("puntajeUtilizado"); // puede ser null

    if (idCliente == null)  { addError("Debe seleccionar un cliente");  return; }
    if (idConcepto == null) { addError("Debe seleccionar un concepto"); return; }

    EntityManager em = XPersistence.getManager();
    Query q = em.createNativeQuery("select fn_usar_puntos(:c,:p,cast(:n as integer))");
    q.setParameter("c", idCliente);
    q.setParameter("p", idConcepto);
    q.setParameter("n", puntos); // null o Integer

    Object r = q.getSingleResult();
    Integer idCab = (r == null) ? null : Integer.valueOf(r.toString());
    if (idCab == null) { addError("No se pudo registrar el uso de puntos"); return; }

    //showMessage("Uso de puntos registrado (ID " + idCab + ")");
    getView().clear();
    getView().setValue("id", idCab);  // setea la PK en la vista
    getView().findObject();           // sin argumentos
    getView().refresh();              // recarga la colección 'detalle'
  }
}

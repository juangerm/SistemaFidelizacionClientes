package py.gestion.sifi.alCambiar;

import org.openxava.actions.OnChangePropertyBaseAction;
import org.openxava.jpa.XPersistence;
import py.gestion.sifi.modelo.ConceptoPunto;
import py.gestion.sifi.modelo.Producto;

public class UsoPuntoCabeceraAlCambiarProductoAccion extends OnChangePropertyBaseAction{

	@Override
	public void execute() throws Exception {
		if (getNewValue() == null) {
			//addError("indicar concepto");
			return;
		}
		Integer productoId = getView().getSubview("producto").getValueInt("id");
		
		if(productoId == 0) {
			addError("indicar producto");
			return;
		}
		
		Producto producto = XPersistence.getManager().find(Producto.class, productoId);
		
		getView().setValue("puntajeUtilizado", producto.getPuntosRequeridos());
		
	}
	
	

}

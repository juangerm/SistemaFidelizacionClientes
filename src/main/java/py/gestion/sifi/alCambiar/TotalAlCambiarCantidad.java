package py.gestion.sifi.alCambiar;

import java.math.*;

import javax.persistence.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;

import py.gestion.sifi.modelo.*;

public class TotalAlCambiarCantidad extends OnChangePropertyBaseAction{
	
	@Override
	public void execute() throws Exception {
		
		if(getNewValue() == null) {
			return ;
		}
		
		try {
			EntityManager em = XPersistence.getManager();
			Integer idProducto = getView().getSubview("producto").getValueInt("id");
			Producto producto = em.find(Producto.class, (idProducto == null ? 0 : getView().getSubview("producto").getValueInt("id")));
			Integer cantidad = getView().getValueInt("cantidad");
			BigDecimal total = (producto.getPrecioReferencia()).multiply(new BigDecimal(cantidad));
			
			getView().setValue("total", total);
			
			
		}catch (Exception e) {
			addError("no se pudo obtener el total del producto por cantidad");
		}
	}

}

package py.gestion.sifi.alCambiar;

import javax.persistence.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;

import py.gestion.sifi.modelo.*;

public class PrecioAlCambiarProducto extends OnChangePropertyBaseAction{
	
	@Override
	public void execute() throws Exception {
		
		if(getNewValue() == null) {
			return ;
		}
		
		try {
			EntityManager em = XPersistence.getManager();
			Integer idProducto = getView().getSubview("producto").getValueInt("id");
			System.out.println("id producto "+idProducto);
			Producto producto = em.find(Producto.class,( idProducto == null ? 0 : getView().getSubview("producto").getValueInt("id")));
			
			
			getView().setValue("precio", producto.getPrecioReferencia());
			
			
		}catch (Exception e) {
			addError("no se pudo obtener el precio del producto");
		}
	}

}

package py.gestion.sifi.alCambiar;

import org.openxava.actions.*;
import org.openxava.jpa.*;

import py.gestion.sifi.modelo.*;

public class UsoPuntoCabeceraAlCambiarConceptoPuntoAccion extends OnChangePropertyBaseAction{

	@Override
	public void execute() throws Exception {
		if (getNewValue() == null) {
			//addError("indicar concepto");
			return;
		}
		Integer conceptoId = getView().getSubview("conceptoPunto").getValueInt("id");
		
		if(conceptoId == 0) {
			addError("indicar concepto");
			return;
		}
		
		ConceptoPunto conceptoPunto = XPersistence.getManager().find(ConceptoPunto.class, conceptoId);
		
		getView().setValue("puntajeUtilizado", conceptoPunto.getPuntoRequerido());
		
	}
	
	

}

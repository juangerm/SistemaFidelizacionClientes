package py.gestion.sifi.alCambiar;

import java.math.*;

import org.openxava.actions.*;

public class CalcularPuntoRequeridoAlCambiarPrecio extends OnChangePropertyBaseAction{

	@Override
	public void execute() throws Exception {
		
		if(getNewValue() == null) {
			return ;
		}
		
		BigDecimal precio = (BigDecimal) getView().getValue("precio");
		BigDecimal puntoNecesario;
		BigDecimal montoDivisor = new BigDecimal(600);
		
		if (precio != null) {
			puntoNecesario = precio.divideToIntegralValue(montoDivisor);
			getView().setValue("puntoRequerido", puntoNecesario);
		}
		
	}

}

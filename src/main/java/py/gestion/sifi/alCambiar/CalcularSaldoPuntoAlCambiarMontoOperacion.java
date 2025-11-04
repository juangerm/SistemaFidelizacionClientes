package py.gestion.sifi.alCambiar;

import org.openxava.actions.*;
import org.openxava.jpa.*;

public class CalcularSaldoPuntoAlCambiarMontoOperacion extends OnChangePropertyBaseAction{

	@Override
	public void execute() throws Exception {
		
		if(getNewValue() == null) {
			return ;
		}
		
		try {
			Integer montoOperacion = getView().getValueInt("montoOperacion");
			
			Integer monto = 0;
			Integer punto = 0;
			if  (montoOperacion != null) {
				monto = montoEquivalente(montoOperacion);
				punto =  puntoEquivalente(montoOperacion);
			}
			Integer puntajeAsignado = (montoOperacion/monto)*punto;
			getView().setValue("puntajeAsignado",puntajeAsignado);
			Integer puntajeUtilizado = getView().getValueInt("puntajeUtilizado");
			Integer saldoPunto = puntajeAsignado - puntajeUtilizado;
			getView().setValue("saldoPunto", saldoPunto);
		}catch (Exception e) {
			addError("no_se pudo calcular el salso restante");
		}
		
	}

	private Integer montoEquivalente(Integer montoOperacion) {
		Integer monto = 0;
		System.out.println("parametro "+ montoOperacion);
		try {
			 monto = (Integer) XPersistence.getManager()
					.createNativeQuery(" select monto_equivalente "
							+ "from reglaasignacionpunto "
							+ "WHERE ((:montoOperacion BETWEEN limite_inferior AND limite_superior) "
							+ " OR (limite_superior IS NULL AND :montoOperacion >= limite_inferior))")
					.setParameter("montoOperacion", montoOperacion)
					.getSingleResult();
		} catch (Exception e) {
			addError("no_se_pudo_encontrar_la_regla");
		}
		
		return monto == null ? 0 : monto ;
	}
	
	private Integer puntoEquivalente(Integer montoOperacion) {
		Integer monto = 0;
		try {
			 monto = (Integer) XPersistence.getManager()
					.createNativeQuery(" select punto_equivalente "
							+ "from reglaasignacionpunto "
							+ "WHERE  ((:montoOperacion BETWEEN limite_inferior AND limite_superior) "
							+ " OR (limite_superior IS NULL AND :montoOperacion >= limite_inferior))")
					.setParameter("montoOperacion", montoOperacion)
					.getSingleResult();
		} catch (Exception e) {
			addError("no_se_pudo_encontrar_la_regla");
		}
		
		return monto == null ? 0 : monto ;
	}

}

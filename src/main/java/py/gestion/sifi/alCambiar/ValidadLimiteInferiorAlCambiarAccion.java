package py.gestion.sifi.alCambiar;

import org.openxava.actions.*;
import org.openxava.jpa.*;

public class ValidadLimiteInferiorAlCambiarAccion extends OnChangePropertyBaseAction{

	@Override
	public void execute() throws Exception {
		if(getNewValue() == null) {
			return ;
		}
		
		try {
			Integer limiteInferior = getView().getValueInt("limiteInferior");
			//Integer lInferior = 0;
			if(limiteInferior != null) {
				busquedaLimiteInferior(limiteInferior);
			}
		
		}catch (Exception e) {
			addError("el monto limite inferior no puede ser null");
		}
		
	}

	private Void busquedaLimiteInferior(Integer limiteInferior) {
		//Integer existe = 0;
		System.out.println("parametro "+ limiteInferior);
		try {
			Object	resultado = XPersistence.getManager()
		            .createNativeQuery(
		                "SELECT COUNT(*) " +
		                "FROM reglaasignacionpunto " +
		                "WHERE limite_superior IS NOT NULL " +
		                "AND :limiteInferior BETWEEN limite_inferior AND limite_superior"
		            )
		            .setParameter("limiteInferior", limiteInferior)
		            .getSingleResult();
			 
			Integer existe = ((Number) resultado).intValue();

		        // Si encuentra una regla que cubre ese monto, es un solapamiento
		        if (existe > 0) {
		            addError("El monto se solapa con una regla ya existente existe");
		        }

		        // También verificamos si ya existe una regla sin límite superior
		        Object sinLimite = XPersistence.getManager()
		            .createNativeQuery(
		                "SELECT COUNT(*) " +
		                "FROM reglaasignacionpunto " +
		                "WHERE limite_superior IS NULL"
		            )
		            .getSingleResult();
		        Integer sLimite = ((Number) sinLimite).intValue();

		        // Si existe una regla abierta y el nuevo límite inferior está debajo, también error
		        if (sLimite > 0) {
		            Integer minimoAbierto = (Integer) XPersistence.getManager()
		                .createNativeQuery(
		                    "SELECT limite_inferior " +
		                    "FROM reglaasignacionpunto " +
		                    "WHERE limite_superior IS NULL " +
		                    "LIMIT 1"
		                )
		                .getSingleResult();
		            Integer minimoSuperior = (Integer) XPersistence.getManager()
			                .createNativeQuery(
			                    "SELECT limite_superior " +
			                    "FROM reglaasignacionpunto " +
			                    "WHERE limite_superior IS NOT NULL " + 
			                    "and :limiteInferior > limite_superior" +
			                    "LIMIT 1"
			                )
			                .getSingleResult();

		            if (limiteInferior <= minimoAbierto && limiteInferior <= minimoSuperior) {
		                addError("El monto se solapa con una regla abierta (sin límite superior)");
		            }
		        }
		} catch (Exception e) {
			addError("el monto se solapa con una regla ya existente");
		}
		
		return null;
	}

}

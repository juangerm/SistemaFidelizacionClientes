package py.gestion.sifi.alCambiar;

import org.openxava.actions.*;
import org.openxava.jpa.*;

public class ValidadLimiteSuperiorAlCambiarAccion extends OnChangePropertyBaseAction{

	@Override
	public void execute() throws Exception {
		if(getNewValue() == null) {
			return ;
		}
		
		try {
			Integer limiteSuperior = getView().getValueInt("limiteSuperior");
			Integer limiteInferior = getView().getValueInt("limiteInferior");
			if(limiteSuperior <= limiteInferior) {
				addError("El limite superior no puede ser igual o menor al limite inferior");
			}
			//Integer lInferior = 0;
			if(limiteSuperior != null) {
				busquedaLimiteSuperior(limiteSuperior);
			}
		
		}catch (Exception e) {
			addInfo("el monto limite superior queda abierto");
		}
		
	}

	private Void busquedaLimiteSuperior(Integer limiteSuperior) {
		//Integer existe = 0;
		System.out.println("parametro "+ limiteSuperior);
		try {
			Object	resultado = XPersistence.getManager()
		            .createNativeQuery(
		                "SELECT COUNT(*) " +
		                "FROM reglaasignacionpunto " +
		                "WHERE limite_superior IS NOT NULL " +
		                "AND :limiteSuperior BETWEEN limite_inferior AND limite_superior"
		            )
		            .setParameter("limiteSuperior", limiteSuperior)
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
		                    "SELECT limite_superior " +
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
			                    "and :limiteSuperior > limite_superior" +
			                    "LIMIT 1"
			                )
			                .getSingleResult();

		            if (limiteSuperior <= minimoAbierto && limiteSuperior <= minimoSuperior) {
		                addError("El monto se solapa con una regla abierta (sin límite superior)");
		            }
		        }
		} catch (Exception e) {
			addError("el monto se solapa con una regla ya existente");
		}
		
		return null;
	}

}

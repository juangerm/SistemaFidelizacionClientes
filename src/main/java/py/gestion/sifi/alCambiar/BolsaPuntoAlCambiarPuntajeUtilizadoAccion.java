package py.gestion.sifi.alCambiar;

import org.openxava.actions.*;

public class BolsaPuntoAlCambiarPuntajeUtilizadoAccion extends OnChangePropertyBaseAction{

	@Override
	public void execute() throws Exception {
		if(getNewValue() == null) {
			return ;
		}
		Integer puntajeAsignado = getView().getValueInt("puntajeAsignado");
		System.out.println("puntajeAsignado "+puntajeAsignado);
		getView().setValue("saldoPunto", puntajeAsignado);
		
	}
	

}

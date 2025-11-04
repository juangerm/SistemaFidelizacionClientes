package py.gestion.sifi.alCambiar;

import org.openxava.actions.*;

public class ReglaAsignacionPuntoAlCambiarLimiteInferiorAccion extends OnChangePropertyBaseAction{
	
	@Override
	public void execute() throws Exception {
		
		if(getNewValue() == null) {
			return ;
		}
		
//		public verificarLimiteInferior throws Exception {
//			EntityManager em = XPersistence.getManager();
//			try {		
//				Integer limiteInferior = getView().getValueInt("limiteInferior");
//				Query query=em.createQuery("SELECT n from ReglaAsignacionPunto n where n.limiteInferior= :limiteInferior'")
//						.setParameter("limiteInferior", limiteInferior);
//				System.out.println(query.getSingleResult());
//				return query.getSingleResult();
//				
//			}catch(Exception e) {
//				return null;
//			}
//			
//		}
		
//		try {
//			Integer limiteInferior = getView().getValueInt("limiteInferior");
//			ReglaAsignacionPunto reglaLimiteInferior = (ReglaAsignacionPunto) XPersistence.getManager()
//					.createQuery("Select o from ReglaAsignacionPunto o where o.limiteInferior = :limiteInferior")
//					.setParameter("limiteInferior", limiteInferior).getResultList().get(0);
//			return;
//		}catch (Exception e) {
//			addError("Ya exixte limite inferior");
//		}
		
	}

}

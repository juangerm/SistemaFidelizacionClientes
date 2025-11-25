package py.gestion.sifi.rest;

import java.math.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;

import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.openxava.jpa.*;

import py.gestion.sifi.dto.*;
import py.gestion.sifi.modelo.*;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServicioRest {

	/*
	 * =========================== HELPERS (DTO mappers) ===========================
	 */
	private static ClienteDTO toDTO(Cliente c) {
		ClienteDTO dto = new ClienteDTO();
		dto.setId(c.getId());
		dto.setNombre(c.getNombre());
		dto.setApellido(c.getApellido());
		dto.setNumeroDocumento(c.getNumeroDocumento());
		dto.setFechaNacimiento(c.getFechaNacimiento() != null ? c.getFechaNacimiento().toString() : null);
		dto.setCelular(c.getCelular());
		dto.setEmail(c.getEmail());
		dto.setCodNacionalidad(c.getNacionalidad() != null ? c.getNacionalidad().getCodNacionalidad() : null);
		dto.setCodTipoDocumento(c.getTipoDocumento() != null ? c.getTipoDocumento().getCodTipoDocumento() : null);
		return dto;
	}

	private static BolsaPuntoDTO toDTO(BolsaPunto b) {
		BolsaPuntoDTO dto = new BolsaPuntoDTO();
		dto.setId(b.getId());
		dto.setIdCliente(b.getCliente() != null ? b.getCliente().getId() : null);
		dto.setIdVencimientoPunto(b.getVencimientoPunto() != null ? b.getVencimientoPunto().getId() : null);
		dto.setPuntajeAsignado(b.getPuntajeAsignado());
		dto.setPuntajeUtilizado(b.getPuntajeUtilizado());
		dto.setSaldoPunto(b.getSaldoPunto());
		dto.setMontoOperacion(b.getMontoOperacion());
		dto.setNombreCliente(
				b.getCliente() != null ? b.getCliente().getNombre() + " " + b.getCliente().getApellido() : null);
		dto.setVencimientoPunto(
				b.getVencimientoPunto() != null ? b.getVencimientoPunto().getFechaFin().toString() : null);
		return dto;
	}
	
	private static PromocionDTO toDTO(Promocion p) {
	    PromocionDTO dto = new PromocionDTO();
	    dto.setId(p.getId());
	    dto.setNombre(p.getNombre());
	    dto.setInicio(p.getInicio());
	    dto.setFin(p.getFin());
	    dto.setPuntosExtra(p.getPuntosExtra());

	    if (p.getProducto() != null) {
	        dto.setProductoId(p.getProducto().getId());
	    }

	    return dto;
	}
	
	private static VentaDTO toDTO(Venta v) {
	    VentaDTO dto = new VentaDTO();
	    dto.setId(v.getId());
	    dto.setClienteId(v.getCliente().getId());
	    dto.setProductoId(v.getProducto().getId());
	    dto.setCantidad(v.getCantidad());
	    dto.setPrecio(v.getPrecio());
	    dto.setTotal(v.getTotal());
	    dto.setFecha(v.getFecha());
	    dto.setPuntosBase(v.getPuntosBase());
	    dto.setPuntosPromo(v.getPuntosPromo());
	    dto.setPuntosTotales(v.getPuntosTotales());
	    return dto;
	}

	private static ReferirClienteDTO toReferirClienteDTO(ReferirCliente r, LocalDate fechaFin) {

	    ReferirClienteDTO dto = new ReferirClienteDTO();
	    dto.setId(r.getId());
	    dto.setCliente(toDTO(r.getCliente()));
	    dto.setReferido(toDTO(r.getReferido()));
	    dto.setFechaRegistro(r.getFechaRegistro().toString());
	    dto.setPuntosObtenidos(r.getPuntosObtenidos());

	    dto.setFechaVencimientoPuntos(
	        fechaFin != null ? fechaFin.toString() : null
	    );

	    return dto;
	}


	/*
	 * =========================== CLIENTES ===========================
	 */
	@GET
	@Path("/clientes")
	public List<ClienteDTO> listarClientes() {
		EntityManager em = XPersistence.getManager();
		List<Cliente> list = em.createQuery("select c from Cliente c", Cliente.class).getResultList();
		List<ClienteDTO> out = new ArrayList<>();
		for (Cliente c : list)
			out.add(toDTO(c));
		return out;
	}

	@GET
	@Path("/clientes/{id}")
	public ClienteDTO obtenerCliente(@PathParam("id") Integer id) {
		EntityManager em = XPersistence.getManager();
		Cliente c = em.find(Cliente.class, id);
		if (c == null)
			throw new WebApplicationException("Cliente no encontrado", 404);
		return toDTO(c);
	}
	
	@GET
	@Path("/clientes/segmento")
	public List<ClienteSegmentoDTO> segmentarClientes(
	        @QueryParam("edadMin") Integer edadMin,
	        @QueryParam("edadMax") Integer edadMax,
	        @QueryParam("pais") String pais,
	        @QueryParam("comprasMin") Integer comprasMin
	) {
	    EntityManager em = XPersistence.getManager();

	    StringBuilder jpql = new StringBuilder("SELECT c FROM Cliente c WHERE 1=1 ");
	    if (edadMin != null) jpql.append(" AND c.fechaNacimiento <= :fechaMax ");
	    if (edadMax != null) jpql.append(" AND c.fechaNacimiento >= :fechaMin ");
	    if (pais != null && !pais.isEmpty()) jpql.append(" AND UPPER(c.nacionalidad.pais.descripcion) = UPPER(:pais) ");

	    TypedQuery<Cliente> query = em.createQuery(jpql.toString(), Cliente.class);

	    LocalDate hoy = LocalDate.now();
	    if (edadMin != null) query.setParameter("fechaMax", hoy.minusYears(edadMin));
	    if (edadMax != null) query.setParameter("fechaMin", hoy.minusYears(edadMax));
	    if (pais != null && !pais.isEmpty()) query.setParameter("pais", pais);

	    List<Cliente> clientes = query.getResultList();
	    List<ClienteSegmentoDTO> out = new ArrayList<>();

	    for (Cliente c : clientes) {

	        List<UsoPuntoCabecera> compras = em.createQuery(
	                "SELECT u FROM UsoPuntoCabecera u WHERE u.cliente.id = :cliente ORDER BY u.fecha DESC",
	                UsoPuntoCabecera.class)
	                .setParameter("cliente", c.getId())
	                .getResultList();

	        int totalCompras = compras.size();

	        if (comprasMin != null && totalCompras < comprasMin) continue;

	        LocalDate fechaUltimaCompra = compras.isEmpty()
	                ? null
	                : compras.get(0).getFecha();

	        Set<String> conceptos = new HashSet<>();
	        for (UsoPuntoCabecera cab : compras) {
	            if (cab.getConceptoPunto() != null) {
	                conceptos.add(cab.getConceptoPunto().getConcepto());
	            }
	        }
 
	        ClienteSegmentoDTO dto = new ClienteSegmentoDTO();
	        dto.id = c.getId();
	        dto.nombre = c.getNombre();
	        dto.ciudad = c.getNacionalidad().getPais().getDescripcion();
	        dto.totalCompras = totalCompras;
	        dto.ultimaCompra = fechaUltimaCompra;
	        dto.conceptosComprados = new ArrayList<>(conceptos);

	        out.add(dto);
	    }

	    return out;
	}

	@GET
	@Path("/clientes/{id}/nivel")
	public NivelClienteDTO obtenerNivelCliente(@PathParam("id") Integer id) {

	    EntityManager em = XPersistence.getManager();
	    Cliente c = em.find(Cliente.class, id);

	    if (c == null)
	        throw new WebApplicationException("Cliente no encontrado", 404);

	    Integer puntos = c.getPuntosAcumulados();
	    if (puntos == null) puntos = 0;

	    List<NivelFidelizacion> niveles = em.createQuery(
	        "SELECT n FROM NivelFidelizacion n " +
	        "WHERE (n.puntoMaximo IS NOT NULL AND :p BETWEEN n.puntoMinimo AND n.puntoMaximo) " +
	        "   OR (n.puntoMaximo IS NULL AND :p >= n.puntoMinimo)",
	        NivelFidelizacion.class
	    )
	    .setParameter("p", puntos)
	    .getResultList();

	    if (niveles.isEmpty()) {
	        throw new WebApplicationException("No existe nivel para " + puntos + " puntos", 404);
	    }

	    NivelFidelizacion nivel = niveles.get(0);

	    return new NivelClienteDTO(
	        c.getNombre() + " " + c.getApellido(),
	        nivel.getDescripcion(),              
	        nivel.getPuntoMinimo(),         
	        nivel.getPuntoMaximo(),         
	        puntos,                         
	        nivel.getBeneficios()            
	    );
	}

	@POST
	@Path("/clientes")
	public Cliente crearCliente(Cliente body) {
		try {
			EntityManager em = XPersistence.getManager();
			em.persist(body);
			return body;
		} catch (Exception e) {
			XPersistence.rollback();
			throw new WebApplicationException("Error al crear cliente: " + e.getMessage(), 500);
		}
	}

	@PUT
	@Path("/clientes/{id}")
	public ClienteDTO actualizarCliente(@PathParam("id") Integer id, Cliente in) {
		EntityManager em = XPersistence.getManager();
		try {
			Cliente c = em.find(Cliente.class, id);
			if (c == null)
				throw new WebApplicationException("Cliente no encontrado con ID " + id, 404);

			c.setNombre(in.getNombre());
			c.setApellido(in.getApellido());
			c.setNumeroDocumento(in.getNumeroDocumento());
			c.setFechaNacimiento(in.getFechaNacimiento());
			c.setCelular(in.getCelular());
			c.setEmail(in.getEmail());
			if (in.getNacionalidad() != null)
				c.setNacionalidad(em.find(Nacionalidad.class, in.getNacionalidad().getCodNacionalidad()));
			if (in.getTipoDocumento() != null)
				c.setTipoDocumento(em.find(TipoDocumento.class, in.getTipoDocumento().getCodTipoDocumento()));
			em.merge(c);
			return toDTO(c);
		} catch (Exception e) {
			XPersistence.rollback();
			throw new WebApplicationException("Error al actualizar cliente: " + e.getMessage(), 500);
		}
	}

	@DELETE
	@Path("/clientes/{id}")
	public Response eliminarCliente(@PathParam("id") Integer id) {
		EntityManager em = XPersistence.getManager();
		try {
			Cliente c = em.find(Cliente.class, id);
			if (c == null)
				return Response.status(404).entity("Cliente no encontrado").build();
			em.remove(c);
			return Response.ok().entity("Eliminado").build();
		} catch (Exception e) {
			XPersistence.rollback();
			return Response.serverError().entity("Error al eliminar: " + e.getMessage()).build();
		}
	}
	
	@GET @Path("/clientes/{id}/saldo")
	public Integer obtenerSaldo(@PathParam("id") Integer id) {
	    Object r = XPersistence.getManager()
	        .createNativeQuery("SELECT fn_total_saldo_disponible(:id)")
	        .setParameter("id", id)
	        .getSingleResult();
	    return r == null ? 0 : Integer.parseInt(r.toString());
	}

	/*
	 * =========================== CONCEPTOS ===========================
	 */
	@GET
	@Path("/conceptos")
	public List<ConceptoPunto> listarConceptos() {
		return XPersistence.getManager().createQuery("from ConceptoPunto", ConceptoPunto.class).getResultList();
	}

	@POST
	@Path("/conceptos")
	public ConceptoPunto crearConcepto(ConceptoPunto c) {
		try {
			XPersistence.getManager().persist(c);
			return c;
		} catch (Exception e) {
			XPersistence.rollback();
			throw new WebApplicationException("Error al crear concepto: " + e.getMessage(), 500);
		}
	}

	@PUT
	@Path("/conceptos/{id}")
	public ConceptoPunto actualizarConcepto(@PathParam("id") Integer id, ConceptoPunto in) {
		EntityManager em = XPersistence.getManager();
		try {
			ConceptoPunto c = em.find(ConceptoPunto.class, id);
			if (c == null)
				throw new WebApplicationException("No encontrado", 404);
			c.setConcepto(in.getConcepto());
			c.setPuntoRequerido(in.getPuntoRequerido());
			return em.merge(c);
		} catch (Exception e) {
			XPersistence.rollback();
			throw new WebApplicationException("Error al actualizar concepto: " + e.getMessage(), 500);
		}
	}

	@DELETE
	@Path("/conceptos/{id}")
	public Response eliminarConcepto(@PathParam("id") Integer id) {
		EntityManager em = XPersistence.getManager();
		try {
			ConceptoPunto c = em.find(ConceptoPunto.class, id);
			if (c == null)
				return Response.status(404).entity("No encontrado").build();
			em.remove(c);
			return Response.ok("Eliminado").build();
		} catch (Exception e) {
			XPersistence.rollback();
			return Response.serverError().entity("Error: " + e.getMessage()).build();
		}
	}

	/*
	 * =========================== REGLAS ===========================
	 */
	@GET
	@Path("/reglas")
	public List<ReglaAsignacionPunto> listarReglas() {
		return XPersistence.getManager().createQuery("from ReglaAsignacionPunto", ReglaAsignacionPunto.class)
				.getResultList();
	}

	@POST
	@Path("/reglas")
	public ReglaAsignacionPunto crearRegla(ReglaAsignacionPunto r) {
		try {
			XPersistence.getManager().persist(r);
			return r;
		} catch (Exception e) {
			XPersistence.rollback();
			throw new WebApplicationException("Error al crear regla: " + e.getMessage(), 500);
		}
	}

	@PUT
	@Path("/reglas/{id}")
	public ReglaAsignacionPunto actualizarRegla(@PathParam("id") Integer id, ReglaAsignacionPunto in) {
		EntityManager em = XPersistence.getManager();
		try {
			ReglaAsignacionPunto r = em.find(ReglaAsignacionPunto.class, id);
			if (r == null)
				throw new WebApplicationException("No encontrado", 404);
			r.setLimiteInferior(in.getLimiteInferior());
			r.setLimiteSuperior(in.getLimiteSuperior());
			r.setPuntoEquivalente(in.getPuntoEquivalente());
			r.setMontoEquivalente(in.getMontoEquivalente());
			return em.merge(r);
		} catch (Exception e) {
			XPersistence.rollback();
			throw new WebApplicationException("Error al actualizar regla: " + e.getMessage(), 500);
		}
	}

	@DELETE
	@Path("/reglas/{id}")
	public Response eliminarRegla(@PathParam("id") Integer id) {
		EntityManager em = XPersistence.getManager();
		try {
			ReglaAsignacionPunto r = em.find(ReglaAsignacionPunto.class, id);
			if (r == null)
				return Response.status(404).entity("No encontrado").build();
			em.remove(r);
			return Response.ok("Eliminado").build();
		} catch (Exception e) {
			XPersistence.rollback();
			return Response.serverError().entity("Error: " + e.getMessage()).build();
		}
	}

	/*
	 * =========================== VENCIMIENTOS ===========================
	 */
	@GET
	@Path("/vencimientos")
	public List<VencimientoPunto> listarVencimientos() {
		return XPersistence.getManager().createQuery("from VencimientoPunto", VencimientoPunto.class).getResultList();
	}

	@POST
	@Path("/vencimientos")
	public VencimientoPunto crearVencimiento(VencimientoPunto v) {
		try {
			XPersistence.getManager().persist(v);
			return v;
		} catch (Exception e) {
			XPersistence.rollback();
			throw new WebApplicationException("Error al crear vencimiento: " + e.getMessage(), 500);
		}
	}

	@PUT
	@Path("/vencimientos/{id}")
	public VencimientoPunto actualizarVencimiento(@PathParam("id") Integer id, VencimientoPunto in) {
		EntityManager em = XPersistence.getManager();
		try {
			VencimientoPunto v = em.find(VencimientoPunto.class, id);
			if (v == null)
				throw new WebApplicationException("No encontrado", 404);
			v.setDiasValidez(in.getDiasValidez());
			v.setFechaInicio(in.getFechaInicio());
			v.setFechaFin(in.getFechaFin());
			return em.merge(v);
		} catch (Exception e) {
			XPersistence.rollback();
			throw new WebApplicationException("Error al actualizar vencimiento: " + e.getMessage(), 500);
		}
	}

	@DELETE
	@Path("/vencimientos/{id}")
	public Response eliminarVencimiento(@PathParam("id") Integer id) {
		EntityManager em = XPersistence.getManager();
		try {
			VencimientoPunto v = em.find(VencimientoPunto.class, id);
			if (v == null)
				return Response.status(404).entity("No encontrado").build();
			em.remove(v);
			return Response.ok("Eliminado").build();
		} catch (Exception e) {
			XPersistence.rollback();
			return Response.serverError().entity("Error: " + e.getMessage()).build();
		}
	}

	/*
	 * =========================== BOLSAS ===========================
	 */
	@GET
	@Path("/bolsas")
	public List<BolsaPuntoDTO> listarBolsas() {
		EntityManager em = XPersistence.getManager();
		List<BolsaPunto> list = em.createQuery("from BolsaPunto", BolsaPunto.class).getResultList();
		List<BolsaPuntoDTO> out = new ArrayList<>();
		for (BolsaPunto b : list)
			out.add(toDTO(b));
		return out;
	}

	@GET
	@Path("/bolsas/{id}")
	public BolsaPuntoDTO obtenerBolsa(@PathParam("id") Integer id) {
		BolsaPunto b = XPersistence.getManager().find(BolsaPunto.class, id);
		if (b == null)
			throw new WebApplicationException("Bolsa no encontrada", 404);
		return toDTO(b);
	}

	@POST @Path("/bolsas")
	public BolsaPuntoDTO crearBolsa(BolsaPunto b) {
	  try {
	    EntityManager em = XPersistence.getManager();

	    if (b.getCliente() != null && b.getCliente().getId() != null) {
	      b.setCliente(em.getReference(Cliente.class, b.getCliente().getId()));
	    }
	    if (b.getVencimientoPunto() != null && b.getVencimientoPunto().getId() != null) {
	      b.setVencimientoPunto(em.getReference(VencimientoPunto.class, b.getVencimientoPunto().getId()));
	    }
	    em.persist(b);
	    em.flush();
	    em.refresh(b);
	    BolsaPuntoDTO dto = toDTO(b);
	    return dto;

	  } catch (Exception e) {
	    XPersistence.rollback();
	    throw new WebApplicationException("Error al crear bolsa: " + e.getMessage(), 500);
	  }
	}


	@PUT
	@Path("/bolsas/{id}")
	public BolsaPuntoDTO actualizarBolsa(@PathParam("id") Integer id, BolsaPuntoDTO in) {
		/* No permitir tocar saldo/puntaje_utilizado directamente por API */
		EntityManager em = XPersistence.getManager();
		try {
			BolsaPunto b = em.find(BolsaPunto.class, id);
			if (b == null)
				throw new WebApplicationException("Bolsa no encontrada", 404);
			b.setMontoOperacion(in.getMontoOperacion());
			if (in.getIdCliente() != null)
				b.setCliente(em.find(Cliente.class, in.getIdCliente()));
			if (in.getIdVencimientoPunto() != null)
				b.setVencimientoPunto(em.find(VencimientoPunto.class, in.getIdVencimientoPunto()));
			em.merge(b);
			return toDTO(b);
		} catch (Exception e) {
			XPersistence.rollback();
			throw new WebApplicationException("Error al actualizar bolsa: " + e.getMessage(), 500);
		}
	}

	@DELETE
	@Path("/bolsas/{id}")
	public Response eliminarBolsa(@PathParam("id") Integer id) {
		EntityManager em = XPersistence.getManager();
		try {
			BolsaPunto b = em.find(BolsaPunto.class, id);
			if (b == null)
				return Response.status(404).entity("No encontrada").build();
			em.remove(b);
			return Response.ok("Eliminada").build();
		} catch (Exception e) {
			XPersistence.rollback();
			return Response.serverError().entity("Error: " + e.getMessage()).build();
		}
	}

	@GET
	@Path("/bolsas/cliente/{idCliente}")
	public List<BolsaPuntoDTO> listarBolsasPorCliente(@PathParam("idCliente") Integer idCliente,
			@QueryParam("soloConSaldo") @DefaultValue("false") boolean soloConSaldo,
			@QueryParam("soloVigentes") @DefaultValue("false") boolean soloVigentes) {

		EntityManager em = XPersistence.getManager();

		StringBuilder sql = new StringBuilder("select b.id, b.idcliente, b.idvencimientopunto, b.puntaje_asignado, "
				+ "       b.puntaje_utilizado, b.saldo_punto, b.monto_operacion, v.fecha_fin, "
				+ "       cl.nombre, cl.apellido " + "from bolsapunto b "
				+ "left join vencimientopunto v on v.id = b.idvencimientopunto "
				+ "join cliente cl on cl.id = b.idcliente " + "where b.idcliente = :cid ");

		if (soloConSaldo)
			sql.append("and b.saldo_punto > 0 ");
		if (soloVigentes)
			sql.append("and (v.id is null or v.fecha_fin is null or v.fecha_fin >= current_date) ");
		sql.append("order by b.id");

		@SuppressWarnings("unchecked")
		List<Object[]> rows = em.createNativeQuery(sql.toString()).setParameter("cid", idCliente).getResultList();

		List<BolsaPuntoDTO> out = new ArrayList<>();
		for (Object[] r : rows) {
			BolsaPuntoDTO d = new BolsaPuntoDTO();
			d.setId((Integer) r[0]);
			d.setIdCliente((Integer) r[1]);
			d.setIdVencimientoPunto((Integer) r[2]);
			d.setPuntajeAsignado((Integer) r[3]);
			d.setPuntajeUtilizado((Integer) r[4]);
			d.setSaldoPunto((Integer) r[5]);
			d.setMontoOperacion((java.math.BigDecimal) r[6]);
			java.sql.Date f = (java.sql.Date) r[7];
			d.setVencimientoPunto(f == null ? null : f.toLocalDate().toString());
			String nombre = (String) r[8];
			String apellido = (String) r[9];
			d.setNombreCliente((nombre == null && apellido == null) ? null : (nombre + " " + apellido).trim());
			out.add(d);
		}
		return out;
	}

	// 2.b) Atajo: solo con saldo y vigentes (friendly)
	@GET
	@Path("/bolsas/cliente/{idCliente}/disponibles")
	public List<BolsaPuntoDTO> listarBolsasDisponiblesPorCliente(@PathParam("idCliente") Integer idCliente) {
		return listarBolsasPorCliente(idCliente, true, true);
	}

	/*
	 * =========================== CANJES (cabecera + detalle)
	 * ===========================
	 */

	// LISTAR canjes con DTO
	@GET
	@Path("/canjes")
	public List<UsoPuntoCabeceraDTO> listarCanjes() {
		EntityManager em = XPersistence.getManager();

		@SuppressWarnings("unchecked")
		List<Object[]> cabs = em
				.createNativeQuery("select c.id, c.idcliente, (cl.nombre || ' ' || cl.apellido) as nombre_cliente, "
						+ "       c.puntaje_utilizado, c.fecha, c.idconceptopunto, cp.concepto "
						+ "from usopuntocabecera c " + "join cliente cl on cl.id = c.idcliente "
						+ "join conceptopunto cp on cp.id = c.idconceptopunto " + "order by c.id desc")
				.getResultList();

		Map<Integer, UsoPuntoCabeceraDTO> mapa = new LinkedHashMap<>();
		for (Object[] r : cabs) {
			UsoPuntoCabeceraDTO dto = new UsoPuntoCabeceraDTO();
			Integer id = (Integer) r[0];
			dto.setId(id);
			dto.setIdCliente((Integer) r[1]);
			dto.setNombreCliente((String) r[2]);
			dto.setPuntajeUtilizado((Integer) r[3]);
			dto.setFecha(((java.sql.Date) r[4]).toLocalDate());
			dto.setIdConceptoPunto((Integer) r[5]);
			dto.setConcepto((String) r[6]);
			dto.setDetalles(new ArrayList<>());
			mapa.put(id, dto);
		}

		if (mapa.isEmpty())
			return new ArrayList<>();

		@SuppressWarnings("unchecked")
		List<Object[]> dets = em
				.createNativeQuery("select ud.idusopuntocabecera, ud.id, b.id as id_bolsa, "
						+ "       v.fecha_fin, b.saldo_punto, ud.puntos_consumidos " + "from usopuntodetalle ud "
						+ "join bolsapunto b on b.id = ud.idbolsapunto "
						+ "left join vencimientopunto v on v.id = b.idvencimientopunto "
						+ "where ud.idusopuntocabecera in (:ids) " + "order by ud.id")
				.setParameter("ids", mapa.keySet()).getResultList();

		for (Object[] r : dets) {
			Integer idCab = (Integer) r[0];
			UsoPuntoDetalleDTO d = new UsoPuntoDetalleDTO();
			d.setId((Integer) r[1]);
			d.setIdBolsaPunto((Integer) r[2]);
			java.sql.Date f = (java.sql.Date) r[3];
			d.setFechaFin(f == null ? null : f.toLocalDate());
			d.setSaldoPunto((Integer) r[4]);
			d.setPuntajeUtilizado((Integer) r[5]); // puntos_consumidos
			mapa.get(idCab).getDetalles().add(d);
		}

		return new ArrayList<>(mapa.values());
	}

	// Body de creación
	public static class CrearCanjeRequest {
		public Integer idCliente;
		public Integer idConceptoPunto;
		public Integer puntos; // opcional (null => usa punto_requerido)
	}

	@POST @Path("/canjes")
	public Response crearCanje(CrearCanjeRequest req) {
	  if (req == null || req.idCliente == null || req.idConceptoPunto == null)
	    throw new WebApplicationException("idCliente e idConceptoPunto son obligatorios", 400);

	  EntityManager em = XPersistence.getManager();

	  Object raw = em.createNativeQuery("select fn_usar_puntos(:c,:p,cast(:n as integer))")
	      .setParameter("c", req.idCliente)
	      .setParameter("p", req.idConceptoPunto)
	      .setParameter("n", req.puntos)
	      .getSingleResult();

	  if (raw == null) throw new WebApplicationException("No se pudo crear el canje", 500);

	  Integer idCab;
	  if (raw instanceof Number) {
	    idCab = ((Number) raw).intValue();
	  } else {
	    idCab = Integer.valueOf(raw.toString());
	  }

	  UsoPuntoCabeceraDTO dto = obtenerCanje(idCab);
	  return Response.created(UriBuilder.fromPath("/canjes/{id}").build(idCab)).entity(dto).build();
	}


	// OBTENER canje (DTO)
	@GET @Path("/canjes/{id}")
	public UsoPuntoCabeceraDTO obtenerCanje(@PathParam("id") Integer id) {
	  EntityManager em = XPersistence.getManager();

	  // 1) Cabecera segura (sin NoResultException)
	  @SuppressWarnings("unchecked")
	  List<Object[]> cabList = em.createNativeQuery(
	    "select c.id, c.idcliente, (cl.nombre || ' ' || cl.apellido) as nombre_cliente, " +
	    "       c.puntaje_utilizado, c.fecha, c.idconceptopunto, cp.concepto " +
	    "from usopuntocabecera c " +
	    "join cliente cl on cl.id = c.idcliente " +
	    "left join conceptopunto cp on cp.id = c.idconceptopunto " +  // <-- cambio aquí
	    "where c.id = :id"
	  ).setParameter("id", id).getResultList();

	  if (cabList.isEmpty()) {
	    throw new WebApplicationException("Canje no encontrado", 404);
	  }

	  Object[] cab = cabList.get(0);

	  UsoPuntoCabeceraDTO dto = new UsoPuntoCabeceraDTO();
	  dto.setId(((Number) cab[0]).intValue());
	  dto.setIdCliente(((Number) cab[1]).intValue());
	  dto.setNombreCliente((String) cab[2]);
	  dto.setPuntajeUtilizado(((Number) cab[3]).intValue());
	  dto.setFecha(((java.sql.Date) cab[4]).toLocalDate());
	  dto.setIdConceptoPunto(cab[5] == null ? null : ((Number) cab[5]).intValue());
	  dto.setConcepto((String) cab[6]);

	  // 2) Detalle
	  @SuppressWarnings("unchecked")
	  List<Object[]> dets = em.createNativeQuery(
	    "select ud.id, v.fecha_fin, b.saldo_punto, ud.idbolsapunto, ud.puntos_consumidos " +
	    "from usopuntodetalle ud " +
	    "join bolsapunto b on b.id = ud.idbolsapunto " +
	    "left join vencimientopunto v on v.id = b.idvencimientopunto " +
	    "where ud.idusopuntocabecera = :id " +
	    "order by ud.id"
	  ).setParameter("id", id).getResultList();

	  List<UsoPuntoDetalleDTO> ds = new ArrayList<>();
	  for (Object[] r : dets) {
	    UsoPuntoDetalleDTO d = new UsoPuntoDetalleDTO();
	    d.setId(((Number) r[0]).intValue());
	    java.sql.Date f = (java.sql.Date) r[1];
	    d.setFechaFin(f == null ? null : f.toLocalDate());
	    d.setSaldoPunto(r[2] == null ? null : ((Number) r[2]).intValue());
	    d.setIdBolsaPunto(((Number) r[3]).intValue());
	    d.setPuntajeUtilizado(((Number) r[4]).intValue()); // puntos_consumidos
	    ds.add(d);
	  }
	  dto.setDetalles(ds);

	  return dto;
	}

	// ACTUALIZAR canje: no permitido
	@PUT
	@Path("/canjes/{id}")
	public Response actualizarCanje(@PathParam("id") Integer id, CrearCanjeRequest body) {
		return Response.status(Response.Status.METHOD_NOT_ALLOWED)
				.entity("No se permite actualizar un canje. Elimine y cree nuevamente.").build();
	}

	// ELIMINAR canje: trigger revierte puntos
	@DELETE
	@Path("/canjes/{id}")
	public Response eliminarCanje(@PathParam("id") Integer id) {
		EntityManager em = XPersistence.getManager();
		try {
			UsoPuntoCabecera c = em.find(UsoPuntoCabecera.class, id);
			if (c == null)
				return Response.status(404).entity("No encontrado").build();
			em.remove(c); // BEFORE DELETE trigger devuelve puntos y borra detalle
			return Response.ok("Eliminado y revertido").build();
		} catch (Exception e) {
			XPersistence.rollback();
			return Response.serverError().entity("Error al eliminar: " + e.getMessage()).build();
		}
	}
	
	/*
	 * =========================== Referir Cliente ===========================
	 */
	
	@GET
	@Path("/referidos/listar")
	public List<ReferirClienteDTO> listarReferidos() {

	    EntityManager em = XPersistence.getManager();

	    List<ReferirCliente> lista = em.createQuery(
	        "SELECT r FROM ReferirCliente r ORDER BY r.fechaRegistro DESC",
	        ReferirCliente.class
	    ).getResultList();

	    List<ReferirClienteDTO> salida = new ArrayList<>();
	    for (ReferirCliente r : lista) {
	        // obtener fecha de vencimiento
	        LocalDate fechaFin = null;
	        try {
	            fechaFin = em.createQuery(
	                    "SELECT bp.vencimientoPunto.fechaFin " +
	                    "FROM BolsaPunto bp " +
	                    "WHERE bp.cliente.id = :id AND bp.puntajeAsignado = :puntos " +
	                    "AND bp.vencimientoPunto.fechaInicio = :inicio",
	                    LocalDate.class)
	                .setParameter("id", r.getCliente().getId())
	                .setParameter("puntos", r.getPuntosObtenidos())
	                .setParameter("inicio", r.getFechaRegistro())
	                .setMaxResults(1)
	                .getSingleResult();

	        } catch (Exception ex) {
	            // puede no encontrar puntos (no rompe)
	        }
	        salida.add(toReferirClienteDTO(r, fechaFin));
	    }

	    return salida;
	}

	@POST
	@Path("/referirCliente")
	public ReferirClienteDTO crearReferido(ReferirCliente r) {

	    EntityManager em = XPersistence.getManager();

	    Cliente cliente = em.find(Cliente.class, r.getCliente().getId());
	    if (cliente == null) {
	        throw new WebApplicationException("Cliente que refiere no encontrado", 404);
	    }

	    Cliente nuevoReferido = new Cliente();
	    Cliente refData = r.getReferido();
	    nuevoReferido.setNombre(refData.getNombre());
	    nuevoReferido.setApellido(refData.getApellido());
	    nuevoReferido.setNumeroDocumento(refData.getNumeroDocumento());
	    nuevoReferido.setTipoDocumento(refData.getTipoDocumento());
	    nuevoReferido.setNacionalidad(refData.getNacionalidad());
	    nuevoReferido.setEmail(refData.getEmail());
	    nuevoReferido.setCelular(refData.getCelular());
	    nuevoReferido.setFechaNacimiento(refData.getFechaNacimiento());

	    em.persist(nuevoReferido);

	    ReferirCliente registro = new ReferirCliente();
	    registro.setCliente(cliente);
	    registro.setReferido(nuevoReferido);
	    registro.setFechaRegistro(LocalDate.now());
	    registro.setPuntosObtenidos(100);

	    em.persist(registro);

	    ReferirClienteDTO dto = new ReferirClienteDTO();

	    // Datos del cliente que refiere
	    dto.setCliente(toDTO(cliente));

	    // Datos del cliente nuevo (referido)
	    dto.setReferido(toDTO(nuevoReferido));

	    // Datos del registro
	    dto.setFechaRegistro(registro.getFechaRegistro().toString());
	    dto.setPuntosObtenidos(registro.getPuntosObtenidos());
	    dto.setFechaVencimientoPuntos(registro.getFechaRegistro().plusDays(30).toString());

	    return dto;
	}

	
	/*
	 * =========================== Promociones ===========================
	 */

	@GET 
	@Path("/promociones/activas")
	public List<PromocionDTO> promocionesActivas() {

	    LocalDate hoy = LocalDate.now();

	    List<Promocion> list = XPersistence.getManager()
	        .createQuery("FROM Promocion p WHERE p.inicio <= :hoy AND p.fin >= :hoy", Promocion.class)
	        .setParameter("hoy", hoy)
	        .getResultList();

	    return list.stream()
	            .map(p -> toDTO(p))
                .collect(Collectors.toList());
	}
	
	@POST 
	@Path("/promociones")
	public Promocion crearPromocion(Promocion p) {
	    XPersistence.getManager().persist(p);
	    return p;
	}

	/*
	 * =========================== Venta ===========================
	 */
	@GET
	@Path("/listarVenta")
	public List<VentaDTO> listar(
	        @QueryParam("inicio") String inicioStr,
	        @QueryParam("fin") String finStr) {

	    EntityManager em = XPersistence.getManager();

	    LocalDate inicio = null;
	    LocalDate fin = null;

	    // Convertimos fechas si las envían
	    if (inicioStr != null && !inicioStr.isEmpty()) {
	        inicio = LocalDate.parse(inicioStr);
	    }
	    if (finStr != null && !finStr.isEmpty()) {
	        fin = LocalDate.parse(finStr);
	    }
	    String jpql = "SELECT v FROM Venta v WHERE 1=1";
	    if (inicio != null) {
	        jpql += " AND v.fecha >= :inicio";
	    }
	    if (fin != null) {
	        jpql += " AND v.fecha <= :fin";
	    }
	    TypedQuery<Venta> query = em.createQuery(jpql, Venta.class);
	    if (inicio != null) {
	        query.setParameter("inicio", inicio);
	    }
	    if (fin != null) {
	        query.setParameter("fin", fin);
	    }
	    List<Venta> ventas = query.getResultList();

	    // Convertimos a DTO
	    return ventas.stream()
	            .map(v -> toDTO(v))
                .collect(Collectors.toList());
	}

	
	@POST
    @Path("/crearVenta")
	public VentaDTO crear(VentaDTO dto) {

	    EntityManager em = XPersistence.getManager();


	    Cliente cliente = em.find(Cliente.class, dto.getClienteId());
	    Producto producto = em.find(Producto.class, dto.getProductoId());

	    //Crear la entidad Venta
	    Venta venta = new Venta();
	    venta.setCliente(cliente);
	    venta.setProducto(producto);
	    venta.setCantidad(dto.getCantidad());

	    venta.setPrecio(producto.getPrecioReferencia());
	    venta.setTotal(producto.getPrecioReferencia().multiply(BigDecimal.valueOf(dto.getCantidad())));

	    em.persist(venta);
	    XPersistence.commit();

	    return toDTO(venta);
	}
	
	
	/*
	 * =========================== Productos ===========================
	 */
	
    @GET
    @Path("/productos/{id}")
    public Producto obtenerProducto(@PathParam("id") Integer id) {
        EntityManager em = XPersistence.getManager();
        Producto p = em.find(Producto.class, id);
        if (p == null) {
            throw new WebApplicationException("Producto no encontrado", 404);
        }
        return p;
    }
    
	@GET @Path("/productos")
	public List<Producto> listarProductos() {
	    return XPersistence.getManager()
	        .createQuery("from Producto", Producto.class)
	        .getResultList();
	}

	@POST @Path("/productos")
	public Producto crearProducto(Producto p) {
	    XPersistence.getManager().persist(p);
	    return p;
	}
	
    @PUT
    @Path("/productos/{id}")
    public Producto actualizarProducto(@PathParam("id") Integer id, Producto in) {
        EntityManager em = XPersistence.getManager();
        try {
            Producto p = em.find(Producto.class, id);
            if (p == null) {
                throw new WebApplicationException("Producto no encontrado", 404);
            }

            p.setNombre(in.getNombre());
            p.setDescripcion(in.getDescripcion());
            p.setPuntosRequeridos(in.getPuntosRequeridos());
            p.setPrecioReferencia(in.getPrecioReferencia());
            p.setActivo(in.getActivo() != null ? in.getActivo() : p.getActivo());

            em.merge(p);
            return p;
        } catch (Exception e) {
            XPersistence.rollback();
            throw new WebApplicationException("Error al actualizar producto: " + e.getMessage(), 500);
        }
    }

    @DELETE
    @Path("/productos/{id}")
    public Response eliminarProducto(@PathParam("id") Integer id) {
        EntityManager em = XPersistence.getManager();
        try {
            Producto p = em.find(Producto.class, id);
            if (p == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Producto no encontrado")
                        .build();
            }

            em.remove(p);
            return Response.ok("Producto eliminado correctamente").build();
        } catch (Exception e) {
            XPersistence.rollback();
            return Response.serverError()
                    .entity("Error al eliminar producto: " + e.getMessage())
                    .build();
        }
    }

	public static class CrearCanjeProductoRequest {
	    public Integer idCliente;
	    public Integer idProducto;
	}
	
	@POST @Path("/canjes/producto")
	public Response crearCanje(CrearCanjeProductoRequest req) {
	  if (req == null || req.idCliente == null || req.idProducto == null)
	    throw new WebApplicationException("idCliente e idProducto son obligatorios", 400);

	  EntityManager em = XPersistence.getManager();

	    Object raw = em.createNativeQuery("select fn_canjear_producto(:c,:p)")
		        .setParameter("c", req.idCliente)
		        .setParameter("p", req.idProducto)
		        .getSingleResult();

	    if (raw == null) {
	        throw new WebApplicationException("No se pudo crear el canje por producto", 500);
	    }

	  Integer idCab;
	  if (raw instanceof Number) {
	    idCab = ((Number) raw).intValue();
	  } else {
	    idCab = Integer.valueOf(raw.toString());
	  }

	  UsoPuntoCabeceraDTO dto = obtenerCanje(idCab);
	  return Response.created(UriBuilder.fromPath("/canjes/{id}").build(idCab)).entity(dto).build();
	}
	
    /*
     * =========================== DASHBOARD ===========================
     */

    @GET
    @Path("/dashboard/resumen")
    public DashboardResumenDTO obtenerResumenDashboard() {
        EntityManager em = XPersistence.getManager();
        DashboardResumenDTO dto = new DashboardResumenDTO();

        Object r;

        // Total de clientes
        r = em.createNativeQuery("select count(*) from cliente")
              .getSingleResult();
        dto.setTotalClientes(((Number) r).longValue());

        // Total de bolsas de puntos (operaciones que generan puntos)
        r = em.createNativeQuery("select count(*) from bolsapunto")
              .getSingleResult();
        dto.setTotalBolsas(((Number) r).longValue());

        // Total de canjes
        r = em.createNativeQuery("select count(*) from usopuntocabecera")
              .getSingleResult();
        dto.setTotalCanjes(((Number) r).longValue());

        // Canjes de hoy
        r = em.createNativeQuery(
                "select count(*) from usopuntocabecera " +
                "where fecha = current_date")
              .getSingleResult();
        dto.setCanjesHoy(((Number) r).longValue());

        // Canjes del mes actual
        r = em.createNativeQuery(
        	    "select count(*) from usopuntocabecera " +
        	    "where fecha >= date_trunc('month', current_date) " +
        	    "  and fecha <= current_date"
        	).getSingleResult();
        dto.setCanjesMes(((Number) r).longValue());

        // Puntos asignados
        r = em.createNativeQuery(
                "select coalesce(sum(puntaje_asignado), 0) from bolsapunto")
              .getSingleResult();
        dto.setPuntosAsignados(((Number) r).longValue());

        // Puntos utilizados
        r = em.createNativeQuery(
                "select coalesce(sum(puntaje_utilizado), 0) from bolsapunto")
              .getSingleResult();
        dto.setPuntosUtilizados(((Number) r).longValue());

        // Saldo total de puntos (vigentes)
        r = em.createNativeQuery(
                "select coalesce(sum(saldo_punto), 0) from bolsapunto")
              .getSingleResult();
        dto.setPuntosSaldo(((Number) r).longValue());

        return dto;
    }


}

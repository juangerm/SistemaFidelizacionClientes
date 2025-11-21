package py.gestion.sifi.dto;

import java.time.*;
import java.util.*;

public class ClienteSegmentoDTO {
	public Integer id;
	public String nombre;
	public String ciudad;
	public Integer edad;
	public Integer cantidadCompras;
	public String ultimoConcepto;
    public Integer totalCompras;                   
    public LocalDate ultimaCompra;             
    public List<String> conceptosComprados;  
}

